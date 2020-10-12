package pl.goingapp.payu.payment.services;

import android.util.Log;

import com.payu.android.front.sdk.payment_library_google_pay_module.model.GooglePayTokenResponseException;
import com.payu.android.front.sdk.payment_library_google_pay_module.model.PaymentDataRequestException;
import com.payu.android.front.sdk.payment_library_payment_chooser.payment_method.external.widget.PaymentChooserWidget;
import com.payu.android.front.sdk.payment_library_payment_methods.model.PaymentMethod;

import pl.goingapp.payu.payment.chooser.PaymentChooserWidgetManager;
import pl.goingapp.payu.payment.process.BLIK;
import pl.goingapp.payu.payment.process.Card;
import pl.goingapp.payu.payment.process.GooglePay;
import pl.goingapp.payu.payment.process.PBL;

public class Payment {
  public static void transactionInitialize() {
    PaymentMethod paymentMethod = PaymentChooserWidgetManager.getCurrentSelectedMethod();
    PaymentChooserWidget paymentChooserWidget = PaymentChooserWidgetManager.getInstance();

    if (paymentMethod != null) {
      Log.v("PaymentService", "transactionInitialize: " + paymentMethod.getPaymentType());

      switch (paymentMethod.getPaymentType()) {
        case PBL:
        case PEX:
          PBL.requestPayment();
          break;
        case CARD:
          Card.process(paymentChooserWidget, paymentMethod.getValue());
          break;
        case GOOGLE_PAY:
          try {
            GooglePay.process();
          } catch (PaymentDataRequestException | GooglePayTokenResponseException e){
            Log.d("GooglePayProcess", String.valueOf(e));
          }
          break;
        case BLIK_GENERIC:
          BLIK.genericProcess(paymentChooserWidget);
          break;
        case BLIK_TOKENS:
          BLIK.tokenProcess();
          break;
        case BLIK_AMBIGUITY:
          BLIK.ambiguityProcess();
          break;
      }
    }
  }
}
