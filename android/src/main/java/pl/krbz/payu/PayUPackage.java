package pl.krbz.payu;

import androidx.annotation.NonNull;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.krbz.payu.cart.CartActivityManager;
import pl.krbz.payu.payment.chooser.PaymentChooserWidgetManager;

public class PayUPackage implements ReactPackage {
  @NonNull
  @Override
  public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactContext) {
    return Collections.singletonList(
        (ViewManager) new PaymentChooserWidgetManager()
    );
  }

  @NonNull
  @Override
  public List<NativeModule> createNativeModules(
    @NonNull ReactApplicationContext reactContext) {
    List<NativeModule> modules = new ArrayList<>();

    modules.add(new CartActivityManager(reactContext));

    return modules;
  }
}
