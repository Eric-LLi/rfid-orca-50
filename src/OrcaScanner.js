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

			DeviceEventEmitter.addListener(OrcaEvent.ExeError, this.ExeErrorEvent);
		}
	}

	ExeErrorEvent = event => {
		console.log(event, 'ExeErrorEvent');
	};

	connect = callback => {
		RNRfidOrca50.connect(callback);
	};
}

export default new RNRfidOrca50Scanner();
