package pl.krbz.payu.cart;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.payu.android.front.sdk.payment_library_payment_chooser.payment_method.internal.ui.payment_method.view.PaymentMethodActivity;
import com.payu.android.front.sdk.payment_library_payment_methods.model.PaymentMethod;

import pl.goingapp.MainApplication;
import pl.goingapp.payu.payment.process.GooglePay;
import pl.goingapp.payu.payment.chooser.PaymentChooserWidgetManager;
import pl.goingapp.payu.payment.services.Payment;

public class CartActivityManager extends ReactContextBaseJavaModule {
  private static final String REACT_CLASS = "PayUCart";

  CartActivityManager(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @NonNull
  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @ReactMethod
  public void startPaymentMethodChooser() {
    Activity activity = MainApplication.getActiveActivity();

    PaymentMethodActivity.start(activity);
  }

  @ReactMethod
  public void startPaymentProcess() {
     Payment.transactionInitialize();
  }

  @ReactMethod
  public PaymentMethod getCurrentSelectedMethod() {
    return PaymentChooserWidgetManager.getCurrentSelectedMethod();
  }

  @ReactMethod
  public void cleanPaymentMethods() {
    PaymentChooserWidgetManager.cleanPaymentMethods();
  }

  @ReactMethod
  public void googlePayActivation() {
    GooglePay googlePay = new GooglePay();
    googlePay.init();
  }
}
