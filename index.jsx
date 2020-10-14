import React from 'react';
import { requireNativeComponent } from 'react-native';

var PayUWidget = requireNativeComponent('PaymentChooserWidget');
var PayUWidgetCmp = function(props) {
    return (
      <PayUWidget {...props} ref={props.refObject} style={{ minHeight: 100 }} />
    );
};

module.exports = PayUWidgetCmp;
export default PayUWidgetCmp;
