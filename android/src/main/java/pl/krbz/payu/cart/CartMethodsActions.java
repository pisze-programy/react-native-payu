package pl.krbz.payu.cart;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.payu.android.front.sdk.payment_library_payment_chooser.payment_method.external.listener.PaymentMethodsCallback;
import com.payu.android.front.sdk.payment_library_payment_chooser.payment_method.external.listener.PosIdListener;
import com.payu.android.front.sdk.payment_library_payment_chooser.payment_method.internal.providers.PaymentMethodActions;
import com.payu.android.front.sdk.payment_library_payment_methods.model.PaymentMethod;
import com.payu.android.front.sdk.payment_library_payment_methods.model.PaymentMethodCreator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import pl.krbz.payu.cart.CartActivityManager;
import pl.krbz.payu.cart.R;

// Usage @payu/payu_payment_methods_fully_qualified_name
public class CartMethodsActions extends PaymentMethodActions {
  private final List<PaymentMethod> paymentMethods;

  public CartMethodsActions(Context context) {
    super(context);
    Log.d("[CartMethodsActions]", "CartMethodsActions");
    paymentMethods = Collections.emptyList();
  }

  @Override
  public void providePaymentMethods(@NonNull final PaymentMethodsCallback callback) {
    Log.d("[CartMethodsActions]", "providePaymentMethods");

    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        callback.onFetched(paymentMethods);
      }
    }, 20);
  }

  @Override
  public void onPaymentMethodRemoved(@NonNull PaymentMethod paymentMethod) {
    Log.d("[CartMethodsActions]", "onPaymentMethodRemoved");
  }

  @Override
  public void providePosId(@NonNull PosIdListener posIdListener) {
    Log.d("[CartMethodsActions]", "providePosId");
    Activity activity = CartActivityManager.getActivity();

    String posId = activity.getResources().getString(R.string.payu_posId);
    Log.d("[CartMethodsActions] ", String.valueOf(posId));
    posIdListener.onPosId(posId);
  }

  @Override
  public void provideBlikPaymentMethods(@NonNull final PaymentMethodsCallback callback) {
    Log.d("[CartMethodsActions]", "provideBlikPaymentMethods");
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        callback.onFetched(paymentMethods);
      }
    }, 20);
  }
}
