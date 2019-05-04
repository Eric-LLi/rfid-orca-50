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
			DeviceEventEmitter.addListener(OrcaEvent.ExeError, this.ExeErrorEvent);
		}
	}

	handleTagEvent = tag => {
		if (this.oncallbacks.hasOwnProperty(OrcaEvent.Tag)) {
			this.oncallbacks[OrcaEvent.Tag].forEach(callback => {
				callback(tag);
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

	startRead() {
		RNRfidOrca50.startRead();
	}
	stopRead() {
		RNRfidOrca50.stopRead();
	}
}

export default new RNRfidOrca50Scanner();
