
# react-native-rfid-orca50

## Getting started

`$ npm install react-native-rfid-orca50 --save`

### Mostly automatic installation

`$ react-native link react-native-rfid-orca50`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-rfid-orca50` and add `RNRfidOrca50.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNRfidOrca50.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNRfidOrca50Package;` to the imports at the top of the file
  - Add `new RNRfidOrca50Package()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-rfid-orca50'
  	project(':react-native-rfid-orca50').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-rfid-orca50/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-rfid-orca50')
  	```

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNRfidOrca50.sln` in `node_modules/react-native-rfid-orca50/windows/RNRfidOrca50.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Rfid.Orca50.RNRfidOrca50;` to the usings at the top of the file
  - Add `new RNRfidOrca50Package()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNRfidOrca50 from 'react-native-rfid-orca50';

// TODO: What to do with the module?
RNRfidOrca50;
```
  