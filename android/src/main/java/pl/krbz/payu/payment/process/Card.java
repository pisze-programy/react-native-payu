package pl.krbz.payu.payment.process;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.payu.android.front.sdk.payment_add_card_module.cvv_validation.view.CvvValidationListener;
import com.payu.android.front.sdk.payment_add_card_module.service.CvvValidationService;
import com.payu.android.front.sdk.payment_add_card_module.status.CvvPaymentStatus;
import com.payu.android.front.sdk.payment_library_core_android.events.AuthorizationDetails;
import com.payu.android.front.sdk.payment_library_core_android.events.PaymentAuthorization;
import com.payu.android.front.sdk.payment_library_payment_chooser.payment_method.external.widget.PaymentChooserWidget;
import com.payu.android.front.sdk.payment_library_webview_module.web.service.WebPaymentService;

import pl.krbz.payu.cart.CartActivityManager;
import pl.krbz.payu.payment.services.API;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

public class Card {
  public static void process(PaymentChooserWidget paymentChooserWidget, String token) {
    String statusCode = API.makeCardPayment(token);
    String redirectUri = "https://secure.payu.com/pay/?orderId=P55XGD9MZ9201009GUEST000P01&token=eyJhbGciOiJIUzI1NiJ9.eyJvcmRlcklkIjoiUDU1WEdEOU1aOTIwMTAwOUdVRVNUMDAwUDAxIiwicG9zSWQiOiJ6RGVubjhoTiIsImF1dGhvcml0aWVzIjpbIlJPTEVfQ0xJRU5UIl0sInBheWVyRW1haWwiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsImV4cCI6MTYwMjMyNzc0NywiaXNzIjoiUEFZVSIsImF1ZCI6ImFwaS1nYXRld2F5Iiwic3ViIjoiUGF5VSBzdWJqZWN0IiwianRpIjoiYjhmNjNkOGMtZTFkMS00NGZhLThlMzQtMmU4ODJhMDVlMWY5In0.Vjwnd0yDm3obinOaBDR33wJnsA5M0V3UloymLaak9T4";
    String orderId = "P55XGD9MZ9201009GUEST000P01";

    switch (statusCode) {
      case "WARNING_CONTINUE_CVV":
        validateCVV(
          redirectUri,
          orderId
        );
        break;
      case "SUCCESS":
        //
        break;
      case "WARNING_CONTINUE_3DS":
        continueCard3dsValidation(redirectUri);
        break;
      default:
        //
        break;
    }
  }

  public static void validateCVV(final String link, final String orderId) {
    runOnUiThread(new Runnable() {

      @Override
      public void run() {


        CvvValidationService.validateCvv(
          CartActivityManager.getActivity(),
          new AuthorizationDetails.Builder()
            .withLink(link)
            .withOrderId(orderId)
            .build(),
          new CvvValidationListener() {
            @Override
            public void onValidationCompleted(@NonNull CvvPaymentStatus cvvPaymentStatus) {
              Log.d("onValidationCompleted", String.valueOf(cvvPaymentStatus));
            }
          }
        );

      }
    });
  }

  public static AuthorizationDetails createAuthorization3dsDetails(String link) {
    return new AuthorizationDetails.Builder()
      .withAuthorizationType(PaymentAuthorization._3DS)
      .withContinueUrl("https://goingapp.mobile/payu/payment/ok")
      .withLink(link)
      .build();
  }

  public static void continueCard3dsValidation(String link) {
    Activity activity = CartActivityManager.getActivity();

    WebPaymentService.pay(activity, Card.createAuthorization3dsDetails(link));
  }
}
