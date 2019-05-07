
package com.reactlibrary;

import com.facebook.react.modules.core.DeviceEventManagerModule;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Callback;

public class RNRfidOrca50Module extends ReactContextBaseJavaModule implements LifecycleEventListener {

	private final ReactApplicationContext reactContext;
	private RNRfidOrca50Thread scannerthread = null;

	public RNRfidOrca50Module(ReactApplicationContext reactContext) {
		super(reactContext);
		this.reactContext = reactContext;
		this.reactContext.addLifecycleEventListener(this);

		this.scannerthread = new RNRfidOrca50Thread(this.reactContext) {
			@Override
			public void dispatchEvent(String name, WritableMap data) {
				RNRfidOrca50Module.this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
						.emit(name, data);
			}

			@Override
			public void dispatchEvent(String name, String data) {
				RNRfidOrca50Module.this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
						.emit(name, data);
			}

			@Override
			public void dispatchEvent(String name, WritableArray data) {
				RNRfidOrca50Module.this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
						.emit(name, data);
			}
		};
		scannerthread.start();
	}

	@Override
	public String getName() {
		return "RNRfidOrca50";
	}

	@Override
	public void onHostResume() {
		if (this.scannerthread != null) {
			this.scannerthread.onHostResume();
		}
	}

	@Override
	public void onHostPause() {
		if (this.scannerthread != null) {
			this.scannerthread.onHostPause();
		}
	}

	@Override
	public void onHostDestroy() {
		if (this.scannerthread != null) {
			this.scannerthread.onHostDestroy();
		}
	}

	@ReactMethod
	public void connect(Callback callback) {
		if (this.scannerthread != null) {
			callback.invoke(this.scannerthread.connect());
		}
	}

	@ReactMethod
	public void cleanTagBuffer() {
		if (this.scannerthread != null) {
			this.scannerthread.cleanTagBuffer();
		}
	}

	@ReactMethod
	public void startRead() {
		if (this.scannerthread != null) {
			this.scannerthread.startRead();
		}
	}

	@ReactMethod
	public void stopRead() {
		if (this.scannerthread != null) {
			this.scannerthread.stopRead();
		}
	}

	@ReactMethod
	public void barcodeRead() {
		if (this.scannerthread != null) {
			this.scannerthread.barcodeRead();
		}
	}

	@ReactMethod
	public void barcodeStop() {
		if (this.scannerthread != null) {
			this.scannerthread.barcodeStop();
		}
	}

	@ReactMethod
	public void isConnected(Callback callback) {
		if (this.scannerthread != null) {
			callback.invoke(this.scannerthread.isConnected());
		}
	}

	@ReactMethod
	public void setAntennaPower(String level) {
		if (this.scannerthread != null) {
			this.scannerthread.setAntennaPower(level);
		}
	}

	@ReactMethod
	public void getAntennaPower() {
		if (this.scannerthread != null) {
			this.scannerthread.getAntennaPower();
		}
	}
}