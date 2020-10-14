import React from 'react';
import { requireNativeComponent } from 'react-native';

var iOSPayUWidget = requireNativeComponent('RCTPayUWidget');
var androidPayUWidget = requireNativeComponent('PaymentChooserWidget');

export {
    iOSPayUWidget,
    androidPayUWidget,
}
