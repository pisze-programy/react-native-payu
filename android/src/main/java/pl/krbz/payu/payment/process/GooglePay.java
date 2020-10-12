package pl.krbz.payu.payment.process;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.payu.android.front.sdk.payment_library_google_pay_adapter.GooglePayAdapter;
import com.payu.android.front.sdk.payment_library_google_pay_module.builder.Cart;
import com.payu.android.front.sdk.payment_library_google_pay_module.listener.GooglePayVerificationListener;
import com.payu.android.front.sdk.payment_library_google_pay_module.listener.GooglePayVerificationStatus;
import com.payu.android.front.sdk.payment_library_google_pay_module.model.Currency;
import com.payu.android.front.sdk.payment_library_google_pay_module.model.GooglePayTokenResponse;
import com.payu.android.front.sdk.payment_library_google_pay_module.model.GooglePayTokenResponseException;
import com.payu.android.front.sdk.payment_library_google_pay_module.model.PaymentDataRequestException;
import com.payu.android.front.sdk.payment_library_google_pay_module.service.ErrorStatus;
import com.payu.android.front.sdk.payment_library_google_pay_module.service.GooglePayService;
import com.payu.android.front.sdk.payment_library_payment_chooser.payment_method.external.widget.PaymentChooserWidget;

import pl.krbz.payu.cart.CartActivityManager;
import pl.krbz.payu.cart.R;
import pl.krbz.payu.payment.chooser.PaymentChooserWidgetManager;

import static android.app.Activity.RESULT_CANCELED;

public class GooglePay {
  private static GooglePayService googlePayService;

  public static GooglePayService getGooglePayService(){
    return GooglePay.googlePayService;
  }

  public static void setGooglePayService(GooglePayService googlePayService) {
    GooglePay.googlePayService = googlePayService;
  }

  public void init() {
    final Activity activity = CartActivityManager.getActivity();
    // TODO set at onCreate MainApplication
    final GooglePayService googlePayService = new GooglePayService(activity);
    GooglePay.setGooglePayService(googlePayService);

    googlePayService.isReadyToPay(new GooglePayVerificationListener() {
      @Override
      public void onException(@NonNull Exception e) {
        Log.d("GooglePayProcess", "onException: " + e);
      }

      @Override
      public void onVerificationCompleted(GooglePayVerificationStatus googlePayVerificationStatus) {
        Log.d("GooglePayProcess", "onVerificationCompleted: " + googlePayVerificationStatus);

        activity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            PaymentChooserWidget paymentChooserWidget = PaymentChooserWidgetManager.getInstance();
            paymentChooserWidget.setPaymentMethodsAdapter(new GooglePayAdapter(googlePayService));
          }
        });
      }
    });
  }

  public static void process() throws PaymentDataRequestException, GooglePayTokenResponseException {
    Log.d("GooglePayProcess", "initGooglePay");

    GooglePay googlePay = new GooglePay();
    googlePay.requestGooglePayCard(googlePay.createCart(1, Currency.PLN));
  }

  public Cart createCart(int amount, Currency currency) {
    return new Cart.Builder()
      .withTotalPrice(amount)
      .withCurrency(currency)
      .build();
  }

  public void requestGooglePayCard(@NonNull Cart cart) throws PaymentDataRequestException, GooglePayTokenResponseException {
    Activity activity = CartActivityManager.getActivity();
    GooglePayService googlePayService = GooglePay.getGooglePayService();

    String posId = activity.getResources().getString(R.string.payu_posId);
    String merchantName = activity.getResources().getString(R.string.payu_merchant_name);
    Log.d("[CartMethodsActions] ", String.valueOf(posId));

    googlePayService.requestGooglePayCard(cart, posId, merchantName);
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) throws GooglePayTokenResponseException {
    Log.d("GooglePayProcess", String.valueOf("onActivityResult"));
    GooglePayService googlePayService = GooglePay.getGooglePayService();

    if (requestCode == GooglePayService.REQUEST_CODE_GOOGLE_PAY_PAYMENT) {
      if (resultCode == Activity.RESULT_OK) {
         GooglePayTokenResponse googlePayTokenResponse = googlePayService.handleGooglePayResultData(data);

        if (googlePayTokenResponse != null) {
          Log.d("GooglePayProcess", String.valueOf(googlePayTokenResponse.getGooglePayToken()));
          // TODO send info to RN
          // eyJzaWduYXR1cmUiOiJNRVVDSUM2T2FlaE1uMFlzNHIwTnMxQmxNZmlIakJuaUNDek9hYTNJQjAvWTNUOWNBaUVBeEJnSExGZVNHOGNzTGN2ZUtPekMzT2FmKytWUmJ6ejBjeWh6VThsZ3NzVVx1MDAzZCIsInByb3RvY29sVmVyc2lvbiI6IkVDdjEiLCJzaWduZWRNZXNzYWdlIjoie1wiZW5jcnlwdGVkTWVzc2FnZVwiOlwiSkVrQm1FdFlOZkZtYjJrWUpaSmdMN05pajg0RnEvY2hOb0FOL2g0cWNMOExaSW1qYTlMSkZjVVFVaS9yK0pxUzc1OGRJb2NaZ1J6ZkVVRDFYNkMrZmRHM3RvbXpwemoyZnR5YjhRREZlQUROVzgxQ3dyMy9DYVZWVEFrbGM5N2FSclVDY21na3V5YTNXakR3Y2RKSUZaV1N1OTZ5dGJrREZIK2tsZWlzS0JLUlV6S3Zyc3R1cWFsemhxcFJ5T3FYbGU3ODNQVXRvSTUxNjlQcG1HSzBOK1pwcUtobkpaTjByaWQ0MDBKdStKbmNLMEYrTWlKaUF1ZmpUeVNlc1lnZlBMcGxTc1h4SjlKdmVma3h3MWZBTjBleEpFUk93QW5mWVZ6QlpWRDZLNzdCTVBSdVdxYWFqTGlDdFBpdGNMdkYxeEdGdStQOG9yY0Y4ZVdXZHRwYXlOSE9iaFZRdFZMR1F0RmJIdFMwZDU4QlpGLzd6ZzNRSUpEWDNzekx5SmN6TVFIMGlSV3lDQjNUMk1BeEt6Nm1qc3Fqczl6YmVXNU9tRG5vU2pDSHZTV3kySkNoY2FSY2lLOWVBZXhwYktSUlNuQVxcdTAwM2RcIixcImVwaGVtZXJhbFB1YmxpY0tleVwiOlwiQk1rSUJmYjd6ZmlRWGRNNDcySVlzUVMxdmRLZys2dkNGWmk0Sm1zNTJUOGdDODdHQ3VyZXRVdUJRcjRUZlNwYmlEYmFYMDNaSDREMFRidFl0RzFkZ1FBXFx1MDAzZFwiLFwidGFnXCI6XCJqZG1tWG00Yk1nWU5HYStuOUNlbkdFZjlMOFRldWN2SzM5Q3dFUmkyczZRXFx1MDAzZFwifSJ9
        } else {
          Log.d("GooglePayProcess", String.valueOf("googlePayTokenResponse != null"));
          // TODO: Send info to RN
        }
      } else if (resultCode == RESULT_CANCELED) {
        //User has canceled payment
      } else if (resultCode == GooglePayService.RESULT_ERROR) {
         ErrorStatus errorStatus = googlePayService.handleGooglePayErrorStatus(data);

         Log.d("GooglePayProcess", String.valueOf(errorStatus));
         // TODO: Send info to RN
      }
    }
  }
}
