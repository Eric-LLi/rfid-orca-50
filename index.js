import { NativeModules } from 'react-native';

const { RNRfidOrca50 } = NativeModules;
let instance = null;

class RNRfidOrca50Scanner {
	constructor() {
		if (!instance) {
			instance = this;
		}
	}
}

export default RNRfidOrca50Scanner;
