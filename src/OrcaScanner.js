import { NativeModules, DeviceEventEmitter } from 'react-native';
import { OrcaEvent } from './OrcaEvent';

const { RNRfidOrca50 } = NativeModules;
let instance = null;

class RNRfidOrca50Scanner {
	constructor() {
		if (!instance) {
			instance = this;
			this.opened = false;
			this.oncallbacks = [];

			DeviceEventEmitter.addListener(OrcaEvent.Tag, this.handleTagEvent);
			DeviceEventEmitter.addListener(OrcaEvent.Barcode, this.handleBarcodeEvent);
			DeviceEventEmitter.addListener(OrcaEvent.ExeError, this.ExeErrorEvent);
			DeviceEventEmitter.addListener(OrcaEvent.GetPowerLevel, this.GetPowerLevel);
		}
	}

	handleTagEvent = tag => {
		if (this.oncallbacks.hasOwnProperty(OrcaEvent.Tag)) {
			this.oncallbacks[OrcaEvent.Tag].forEach(callback => {
				callback(tag);
			});
		}
	}

	handleBarcodeEvent = barcode => {
		if (this.oncallbacks.hasOwnProperty(OrcaEvent.Barcode)) {
			this.oncallbacks[OrcaEvent.Barcode].forEach(callback => {
				callback(barcode);
			});
		}
	}
	ExeErrorEvent = event => {
		if (this.oncallbacks.hasOwnProperty(OrcaEvent.ExeError)) {
			this.oncallbacks[OrcaEvent.ExeError].forEach(callback => {
				callback(event);
			});
		}
	};

	GetPowerLevel = level => {
		if (this.oncallbacks.hasOwnProperty(OrcaEvent.GetPowerLevel)) {
			this.oncallbacks[OrcaEvent.GetPowerLevel].forEach(callback => {
				callback(level);
			});
		}
	}
	disconnect = () => {
		RNRfidOrca50.disconnect();
	}
	connect = callback => {
		RNRfidOrca50.connect(callback);
	};

	on(event, callback) {
		this.oncallbacks[event] = [];
		this.oncallbacks[event].push(callback);
	}

	removeon(event, callback) {
		if (this.oncallbacks.hasOwnProperty(event)) {
			this.oncallbacks[event].forEach((funct, index) => {
				// if (callback === undefined || callback === null) {
				// this.oncallbacks[event] = [];
				// } else
				if (funct.toString() === callback.toString()) {
					this.oncallbacks[event].splice(index, 1);
				}
			});
		}
	}

	isConnected(callback) {
		RNRfidOrca50.isConnected(callback);
	}
	startRead() {
		RNRfidOrca50.startRead();
	}
	stopRead() {
		RNRfidOrca50.stopRead();
	}
	barcodeRead(){
		RNRfidOrca50.barcodeRead();
	}
	barcodeStop(){
		RNRfidOrca50.barcodeStop();
	}
	cleanTagBuffer() {
		RNRfidOrca50.cleanTagBuffer();
	}
	setAntennaPower(level) {
		RNRfidOrca50.setAntennaPower(level);
	}
	getAntennaPower(){
		RNRfidOrca50.getAntennaPower();
	}
}

export default new RNRfidOrca50Scanner();
