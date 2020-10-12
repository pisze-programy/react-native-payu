package pl.krbz.payu.payment.chooser;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.payu.android.front.sdk.payment_library_payment_chooser.payment_method.external.widget.PaymentChooserWidget;
import com.payu.android.front.sdk.payment_library_payment_methods.model.PaymentMethod;

import pl.krbz.payu.cart.CartActivityManager;
import pl.krbz.payu.cart.R;

public class PaymentChooserWidgetManager extends SimpleViewManager<LinearLayout> {
  public static final String REACT_CLASS = "PaymentChooserWidget";

  @NonNull
  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @NonNull
  protected LinearLayout createViewInstance(@NonNull ThemedReactContext reactContext) {
    LayoutInflater inflater = (LayoutInflater) reactContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    return (LinearLayout) inflater.inflate(R.layout.payment_chooser_widget, null);
  }

  public static PaymentChooserWidget getInstance() {
    Activity activity = CartActivityManager.getActivity();
    return (PaymentChooserWidget) activity.findViewById(R.id.selected_payment_textView);
  }

  public static PaymentMethod getCurrentSelectedMethod() {
    PaymentMethod paymentMethod = PaymentChooserWidgetManager.getInstance().getPaymentMethod().getValue();

    if (paymentMethod != null) {
      Log.d("[CartActivityManager]", String.valueOf(paymentMethod.getPaymentType()));
      Log.d("[CartActivityManager]", String.valueOf(paymentMethod.getValue()));
      Log.d("[CartActivityManager]", String.valueOf(paymentMethod.toString()));
    } else {
      Log.d("[CartActivityManager]", String.valueOf(null));
    }

    return paymentMethod;
  }

  public static void cleanPaymentMethods() {
    PaymentChooserWidgetManager.getInstance().cleanPaymentMethods();
  }
}
