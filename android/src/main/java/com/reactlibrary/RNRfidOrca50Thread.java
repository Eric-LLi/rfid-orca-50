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
import com.rfid.RFIDReaderHelper;
import com.rfid.ReaderConnector;
import com.rfid.bean.MessageTran;
import com.rfid.rxobserver.RXObserver;
import com.rfid.config.*;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;

public abstract class RNRfidOrca50Thread extends Thread {
	private ReactApplicationContext context;
	private byte btReadId = (byte) 0xFF;
	private byte btRepeat = (byte) 0x01;
	private int baud = 115200;
	private String mPosPort = "dev/ttyS4";
	private ModuleConnector connector;
	private RFIDReaderHelper mReaderHelper = null;
	private RXObserver rxObserver = null;
	private RXTXListener mListener = null;

	public RNRfidOrca50Thread(ReactApplicationContext context) {
		this.context = context;
		connector = new ReaderConnector();
	}

	public abstract void dispatchEvent(String name, WritableMap data);

	public abstract void dispatchEvent(String name, String data);

	public abstract void dispatchEvent(String name, WritableArray data);

	public void onHostResume() {
		if (isConnected()) {
			ModuleManager.newInstance().setUHFStatus(true);
		}
	}

	public void onHostPause() {
		if (isConnected()) {
			ModuleManager.newInstance().setUHFStatus(false);
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
		try {
			result = connector.connectCom(mPosPort, baud);
			mReaderHelper = RFIDReaderHelper.getDefaultHelper();
			if (!ModuleManager.newInstance().setUHFStatus(true)) {
				throw new RuntimeException(
						"UHF RFID power on failure,may you open in other Process and do not exit it");
			}
			InitialListener();
			int triggerResult = mReaderHelper.setTrigger(true);
			Log.e("triggerResult", triggerResult + "");

		} catch (Exception ex) {
			HandleError(ex);
		}
		return result;
	}

	public void disconnect() {
		if (isConnected()) {
			try {
				mReaderHelper.unRegisterObserver(rxObserver);
				mReaderHelper.signOut();
				ModuleManager.newInstance().setUHFStatus(false);
				ModuleManager.newInstance().release();
				connector.disConnect();
			} catch (Exception ex) {
				HandleError(ex);
			}
		}
	}

	public void reset() {
		if (isConnected()) {
			mReaderHelper.resetInventoryBuffer(btReadId);
		}
	}

	public void startRead() {

		if (isConnected()) {
			try {
				byte status = (byte) Integer.parseInt("23", 16);
				MessageTran messageTran = new MessageTran(btReadId, (byte) 0xA0, new byte[] { status });
				mReaderHelper.sendCommand(messageTran.getAryTranData());
				// mReaderHelper.realTimeInventory(btReadId, btRepeat);
			} catch (Exception ex) {
				HandleError(ex);
			}
		}
	}

	public void stopRead() {
		if (isConnected()) {
			byte status = (byte) Integer.parseInt("00", 16);
			MessageTran messageTran = new MessageTran(btReadId, (byte) 0xA0, new byte[] { status });
			mReaderHelper.sendCommand(messageTran.getAryTranData());
		}
	}

	private void InitialListener() {
		rxObserver = new RXObserver() {

			@Override
			protected void onExeCMDStatus(byte cmd, byte status) {
				WritableMap event = Arguments.createMap();
				String cmdName = CMD.format(cmd);
				if (status != 0) {
					String statusName = ERROR.format(status);
					Log.e("onExeCMDStatus", cmdName);
					Log.e("onExeCMDStatus", statusName);
					event.putString("cmdName", cmdName);
					event.putString("statusName", statusName);
					dispatchEvent("HandleError", event);
				}
			}

			@Override
			protected void refreshSetting(ReaderSetting readerSetting) {
				Log.e("refreshSetting", "refreshSetting");
			}

			@Override
			protected void onInventoryTag(RXInventoryTag tag) {
				Log.e("InventoryTag", tag.strEPC.trim());
			}

			@Override
			protected void onInventoryTagEnd(RXInventoryTag.RXInventoryTagEnd tagEnd) {
				Log.e("InventoryTagEnd", tagEnd.mTagCount + "");
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
				Log.e("onLostConnect", "onLostConnect");
			}
		};

		mReaderHelper.registerObserver(rxObserver);
		mReaderHelper.setRXTXListener(mListener);
	}

	private void HandleError(Exception ex) {
		WritableMap event = Arguments.createMap();
		event.putString("exeError", ex.getMessage());
		dispatchEvent("HandleError", event);
	}
}
