package com.reactlibrary;

import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import com.module.interaction.RXTXListener;
import com.nativec.tools.ModuleManager;
import com.module.interaction.ModuleConnector;
import com.xdl2d.scanner.TDScannerConnector;
import com.xdl2d.scanner.TDScannerHelper;
import com.rfid.RFIDReaderHelper;
import com.rfid.ReaderConnector;
import com.rfid.bean.MessageTran;
import com.rfid.rxobserver.RXObserver;
import com.xdl2d.scanner.callback.RXCallback;
import com.rfid.config.*;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;

import java.util.ArrayList;

public abstract class RNRfidOrca50Thread extends Thread {
	private ReactApplicationContext context;

	// RFID
	private byte btReadId = (byte) 0xFF;
	private byte btRepeat = (byte) 0x01;
	private int baud = 115200;
	private String mPosPort = "dev/ttyS4";
	private ModuleConnector connector;
	private RFIDReaderHelper mReaderHelper = null;
	private RXObserver rxObserver = null;
	private RXTXListener mListener = null;
	private ArrayList<String> tags = new ArrayList<>();

	// Barcode
	private int barcode_baud = 9600;
	private String barcode_port = "dev/ttyS1";
	private TDScannerHelper mScanner;
	private ModuleConnector mConnector;
	private RXCallback callback;

	public RNRfidOrca50Thread(ReactApplicationContext context) {
		this.context = context;
		connector = new ReaderConnector();
		mConnector = new TDScannerConnector();
	}

	public abstract void dispatchEvent(String name, WritableMap data);

	public abstract void dispatchEvent(String name, String data);

	public abstract void dispatchEvent(String name, WritableArray data);

	public void onHostResume() {
		if (isConnected()) {
			ModuleManager.newInstance().setUHFStatus(true);
			ModuleManager.newInstance().setScanStatus(true);
		}
	}

	public void onHostPause() {
		if (isConnected()) {
			ModuleManager.newInstance().setUHFStatus(false);
			ModuleManager.newInstance().setScanStatus(false);
		}
	}

	public void onHostDestroy() {
		if (isConnected())
			disconnect();
	}

	public boolean isConnected() {
		return connector.isConnected();
	}

	public boolean connect() {
		boolean result = false;
		boolean barcodeResult = false;
		try {
			// RFID
			result = connector.connectCom(mPosPort, baud);
			mReaderHelper = RFIDReaderHelper.getDefaultHelper();
			if (!ModuleManager.newInstance().setUHFStatus(true)) {
				throw new RuntimeException(
						"UHF RFID power on failure,may you open in other Process and do not exit it");
			}
			int triggerResult = mReaderHelper.setTrigger(true);
			Log.e("triggerResult", triggerResult + "");
			stopRead();

			// Barcode
			barcodeResult = mConnector.connectCom(barcode_port, barcode_baud);
			mScanner = TDScannerHelper.getDefaultHelper();
			if (!ModuleManager.newInstance().setScanStatus(false)) {
				throw new RuntimeException(
						"Barcode scanner on failure,may you open in other Process and do not exit" + " it");
			}
			InitialListener();
		} catch (Exception ex) {
			HandleError(ex);
		}
		return result && barcodeResult;
	}

	public void disconnect() {
		if (mReaderHelper != null) {
			mReaderHelper.unRegisterObserver(rxObserver);
			mReaderHelper.signOut();
		}
		if (mScanner != null) {
			mScanner.signOut();
		}
		if (connector != null) {
			connector.disConnect();
		}
		if (mConnector != null) {
			mConnector.disConnect();
		}
		ModuleManager.newInstance().setScanStatus(false);
		ModuleManager.newInstance().setUHFStatus(false);
		ModuleManager.newInstance().release();

		// if (isConnected()) {
		// try {
		// mReaderHelper.unRegisterObservers();
		// mReaderHelper.signOut();
		// ModuleManager.newInstance().setUHFStatus(false);
		// ModuleManager.newInstance().setScanStatus(false);
		// ModuleManager.newInstance().release();
		// connector.disConnect();
		// mConnector.disConnect();
		// } catch (Exception ex) {
		// HandleError(ex);
		// }
		// }
	}

	public void cleanTagBuffer() {
		if (isConnected()) {
			tags = new ArrayList<String>();
			mReaderHelper.resetInventoryBuffer(btReadId);
		}
	}

	public void startRead() {
		try {
			Thread.sleep(1000);
			if (isConnected()) {
				byte status = (byte) Integer.parseInt("23", 16);
				MessageTran messageTran = new MessageTran(btReadId, (byte) 0xA0, new byte[] { status });
				mReaderHelper.sendCommand(messageTran.getAryTranData());
			}
		} catch (Exception ex) {
			HandleError(ex);
		}
	}

	public void stopRead() {
		if (isConnected()) {
			byte status = (byte) Integer.parseInt("00", 16);
			MessageTran messageTran = new MessageTran(btReadId, (byte) 0xA0, new byte[] { status });
			mReaderHelper.sendCommand(messageTran.getAryTranData());
		}
	}

	public void barcodeRead() {
		try {
			Thread.sleep(1000);
			if (isConnected()) {
				if (!ModuleManager.newInstance().setScanStatus(true)) {
					throw new RuntimeException("Barcode scanner connect fail");
				}
			}
		} catch (Exception ex) {
			HandleError(ex);
		}
	}

	public void barcodeStop() {
		try {
			Thread.sleep(1000);
			if (isConnected()) {
				if (!ModuleManager.newInstance().setScanStatus(false)) {
					throw new RuntimeException("Barcode scanner disconnect fail");
				}
			}
		} catch (Exception ex) {
			HandleError(ex);
		}
	}

	public void setAntennaPower(String powerLevel) {
		if (isConnected()) {
			byte power = (byte) Integer.parseInt(powerLevel);
			mReaderHelper.setOutputPower(btReadId, power);
		}
	}

	public void getAntennaPower() {
		try {
			Thread.sleep(1000);
			if (isConnected()) {
				mReaderHelper.getOutputPower(btReadId);
			}
		} catch (Exception ex) {
			HandleError(ex);
		}

	}

	private void InitialListener() {
		rxObserver = new RXObserver() {

			@Override
			protected void onExeCMDStatus(byte cmd, byte status) {
				String cmdName = CMD.format(cmd);
				String statusName = ERROR.format(status);
				if (status != 0) {
					// If not success, thrown error
					if (status != 16) {
						HandleError(new Exception(cmdName + " " + statusName));
					} else {
						Log.e("cmdName", cmdName);
						Log.e("statusName", statusName);
					}
				}
			}

			@Override
			protected void refreshSetting(ReaderSetting readerSetting) {
				Log.e("refreshSetting", "refreshSetting");
				if (readerSetting.btAryOutputPower.length > 0) {
					String power = readerSetting.btAryOutputPower[0] + "";
					// String power = new String(String.valueOf(readerSetting.btAryOutputPower[0]));
					dispatchEvent("getPowerLevel", power);
				}
			}

			@Override
			protected void onInventoryTag(RXInventoryTag tag) {
				String newTag = tag.strEPC.replaceAll(" ", "");

				boolean isExisted = false;
				for (int i = 0; i < tags.size(); i++) {
					if (tags.get(i).equals(newTag)) {
						isExisted = true;
						break;
					}
				}
				if (!isExisted) {
					tags.add(newTag);
					dispatchEvent("TagEvent", newTag);
					Log.e("TagEvent", newTag);
				}
			}

			@Override
			protected void onInventoryTagEnd(RXInventoryTag.RXInventoryTagEnd tagEnd) {
				// Log.e("TagEnd_mTotalRead", tagEnd.mTotalRead + "");
				// Log.e("TagEnd_mTagCount", tagEnd.mTagCount + "");
			}

			@Override
			protected void onFastSwitchAntInventoryTagEnd(RXInventoryTag.RXFastSwitchAntInventoryTagEnd tagEnd) {
				Log.e("AntInventoryTagEnd", tagEnd.mTotalRead + "");
			}

			@Override
			protected void onGetInventoryBufferTagCount(int nTagCount) {
				Log.e("InventoryBufferTagCount", nTagCount + "");
			}

			@Override
			protected void onOperationTag(RXOperationTag tag) {
				Log.e("onOperationTag", tag.strEPC);
			}

			@Override
			protected void onInventory6BTag(byte nAntID, String strUID) {
				Log.e("onInventory6BTag", strUID);
			}

			@Override
			protected void onInventory6BTagEnd(int nTagCount) {
				Log.e("onInventory6BTagEnd", nTagCount + "");
			}

			@Override
			protected void onRead6BTag(byte antID, String strData) {
				Log.e("onRead6BTag", strData);
			}

			@Override
			protected void onWrite6BTag(byte nAntID, byte nWriteLen) {
				Log.e("onWrite6BTag", nAntID + "");
			}

			@Override
			protected void onLock6BTag(byte nAntID, byte nStatus) {
				Log.e("onLock6BTag", nAntID + "");
			}

			@Override
			protected void onLockQuery6BTag(byte nAntID, byte nStatus) {
				Log.e("onLockQuery6BTag", nAntID + "");
			}

			@Override
			protected void onConfigTagMask(MessageTran msgTran) {
				Log.e("onConfigTagMask", "onConfigTagMask");
			}
		};

		mListener = new RXTXListener() {
			@Override
			public void reciveData(byte[] bytes) {

			}

			@Override
			public void sendData(byte[] bytes) {

			}

			@Override
			public void onLostConnect() {
				disconnect();
				Log.e("onLostConnect", "onLostConnect");
			}
		};

		callback = new RXCallback() {
			public void callback(byte[] bytes) {
				String barcode = new String(bytes);
				dispatchEvent("BarcodeEvent", barcode);
				Log.e("2D", barcode);
			}
		};

		mReaderHelper.registerObserver(rxObserver);
		mReaderHelper.setRXTXListener(mListener);
		mScanner.regist2DCodeData(callback);
	}

	private void HandleError(Exception ex) {
		dispatchEvent("HandleError", ex.getMessage());
	}
}
