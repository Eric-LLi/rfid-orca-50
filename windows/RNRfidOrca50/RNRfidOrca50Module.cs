using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Rfid.Orca50.RNRfidOrca50
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNRfidOrca50Module : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNRfidOrca50Module"/>.
        /// </summary>
        internal RNRfidOrca50Module()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNRfidOrca50";
            }
        }
    }
}
