import {
  findNodeHandle,
  NativeModules,
  requireNativeComponent,
  UIManager,
  View,
} from 'react-native';
import React, { RefObject } from 'react';
import { IProps } from './PayUWidget';


/* Android */
const androidPayUWidget = requireNativeComponent<IProps>('PaymentChooserWidget');
export const androidManager = {
  startPaymentProcess: (payUWidgetReference: RefObject<View>) => {
    NativeModules.PayUCart.startPaymentProcess();
  },
}

export const androidPaymentChooserWidgetComponent = (props: IProps) => {
  return (
      <androidPayUWidget {...props} ref={props.refObject} style={{ minHeight: 50 }} />
  );
};

/* iOS */
const iOSPayUWidget = requireNativeComponent<IProps>('RCTPayUWidget');
export const iOSManager = {
  startPaymentProcess: (payUWidgetReference: RefObject<View>) => {
    UIManager.dispatchViewManagerCommand(
        findNodeHandle(payUWidgetReference.current),
        UIManager.getViewManagerConfig('RCTPayUWidget').Commands
            .startPaymentProcess,
        [],
    );
  },
}

export const iOSPaymentChooserWidgetComponent = (props: IProps) => {
  return (
      <iOSPayUWidget {...props} ref={props.refObject} style={{ minHeight: 50 }} />
  );
};
