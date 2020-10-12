package pl.goingapp.payu.payment.process;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.payu.android.front.sdk.payment_library_core_android.events.AuthorizationDetails;
import com.payu.android.front.sdk.payment_library_core_android.events.PaymentAuthorization;
import com.payu.android.front.sdk.payment_library_webview_module.web.event.PaymentDetails;
import com.payu.android.front.sdk.payment_library_webview_module.web.service.WebPaymentService;

import pl.goingapp.MainApplication;

public class PBL {
  public static AuthorizationDetails createAuthorizationDetails() {
    // https://developers.payu.com/en/restapi.html#creating_new_order_api
    return new AuthorizationDetails.Builder()
      .withAuthorizationType(PaymentAuthorization.PAY_BY_LINK)
      // needed for payments, this url is a property that could be passed in
      // OrderCreateRequest or it is a shop page that was verfied by PayU Administrators
      .withContinueUrl("https://goingapp.mobile/payu/payment/ok")
      // redirect link from OCR request
      .withLink("https://merch-prod.snd.payu.com/pay/?orderId=B4W81M2DBK200930GUEST000P01&token=eyJhbGciOiJIUzI1NiJ9.eyJvcmRlcklkIjoiQjRXODFNMkRCSzIwMDkzMEdVRVNUMDAwUDAxIiwicG9zSWQiOiJvZzVIeTU5ZSIsImF1dGhvcml0aWVzIjpbIlJPTEVfQ0xJRU5UIl0sInBheWVyRW1haWwiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsImV4cCI6MTYwMTUzODUxNSwiaXNzIjoiUEFZVSIsImF1ZCI6ImFwaS1nYXRld2F5Iiwic3ViIjoiUGF5VSBzdWJqZWN0IiwianRpIjoiNWZlZWNiZDctMTc1MS00OTE2LWI3NGItOTU4ZGYzMDgyMzhlIn0.oQ6OBNisjoFU-0aZW5WCeUcfP8MdXcL09iwdTsnz2eE")
      .withOrderId("B4W81M2DBK200930GUEST000P01")
      .build();
  }

  public static void requestPayment() {
    Activity activity = MainApplication.getActiveActivity();

    WebPaymentService.pay(activity, PBL.createAuthorizationDetails());
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == WebPaymentService.REQUEST_CODE) {
      PaymentDetails paymentDetails = WebPaymentService.extractPaymentResult(data);
      Log.v("pblProcess rqc", String.valueOf(paymentDetails));
      Log.v("pblProcess rc", String.valueOf(resultCode));
    }
  }
}
