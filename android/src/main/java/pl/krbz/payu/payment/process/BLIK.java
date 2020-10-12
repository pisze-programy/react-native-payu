package pl.goingapp.payu.payment.process;

import android.util.Log;

import com.payu.android.front.sdk.payment_library_payment_chooser.payment_method.external.widget.PaymentChooserWidget;

import pl.goingapp.payu.payment.services.API;

public class BLIK {
  public static void genericProcess(PaymentChooserWidget paymentChooserWidget) {
    if (paymentChooserWidget.isBlikAuthorizationCodeProvided()) {
      Log.v("TAG_BLIK_PAYMENT", "General Blik Payment: " + paymentChooserWidget.getBlikAuthorizationCode());
      API.makeBlikPayment();
    } else {
      Log.v("TAG_BLIK_PAYMENT", "Blik code is not provided");
      // Show toast
    }
  }

  public static void tokenProcess() {
    // TODO
  }

  public static void ambiguityProcess() {
    // TODO
  }
}
