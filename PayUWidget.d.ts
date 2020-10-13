import { StyleProp, View, ViewStyle } from 'react-native';
import { RefObject } from 'react';

export enum PaymentMethod {
  PBL,
  CARD,
  GOOGLE_PAY,
  PEX,
  BLIK_TOKENS,
  BLIK_GENERIC,
  BLIK_AMBIGUITY,
}

export interface IPaymentType {
  paymentMethod: PaymentMethod;
  message: string;
}

export interface IProps {
  refObject?: RefObject<View>;
  style?: StyleProp<ViewStyle>;
  onPaymentFailure?: (payment: IPaymentType) => void;
  onPaymentSuccess?: (payment: IPaymentType) => void;
}
