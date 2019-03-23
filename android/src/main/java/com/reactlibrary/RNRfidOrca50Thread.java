package com.reactlibrary;


import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import com.nativec.tools.ModuleManager;
import com.module.interaction.ModuleConnector;
import com.rfid.RFIDReaderHelper;
import com.rfid.ReaderConnector;
import com.rfid.rxobserver.RXObserver;
import com.rfid.config.*;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;

public abstract class RNRfidOrca50Thread extends Thread {
	private ReactApplicationContext context;
	private int baud = 115200;
	private String mPosPort = "dev/ttyS4";
	private ModuleConnector connector = null;
	private RFIDReaderHelper mReaderHelper = null;
	private RXObserver rxObserver = null;

	private byte btReadId = (byte) 0xFF;
	private byte btRepeat = (byte) 0x01;

	public RNRfidOrca50Thread(ReactApplicationContext context) {
		this.context = context;
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
		disconnect();
	}

	public boolean isConnected() {
		boolean result = false;
		if (isConnected()) {
			result = connector.isConnected();
		}
		return result;
	}

	public boolean connect() {
		boolean result = false;
		try {
			if (!ModuleManager.newInstance().setUHFStatus(true)) {
				throw new RuntimeException("UHF RFID power on failure,may you open in other" +
						" Process and do not exit it");
			}
			connector = new ReaderConnector();
			result = connector.connectCom(mPosPort, baud);
			mReaderHelper = RFIDReaderHelper.getDefaultHelper();
			InitialListener();

		} catch (RuntimeException ex) {
			HandleError(ex);
		} catch (Exception ex) {
			HandleError(ex);
		}
		return result;
	}

	public void disconnect() {
		if (isConnected()) {
			try {
				ModuleManager.newInstance().setUHFStatus(false);
				ModuleManager.newInstance().release();
				connector.disConnect();
			} catch (Exception ex) {
				HandleError(ex);
			}

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

					event.putString("cmdName", cmdName);
					event.putString("statusName", statusName);
					dispatchEvent("HandleError", event);
				}

			}

			@Override
			protected void refreshSetting(ReaderSetting readerSetting) {

			}

			@Override
			protected void onInventoryTag(RXInventoryTag tag) {

			}
		};
	}

	private void HandleError(Exception ex) {
		WritableMap event = Arguments.createMap();
		event.putString("exeError", ex.getMessage());
		dispatchEvent("HandleError", event);
	}
}
