package pl.krbz.payu.payment.services;

import android.app.Activity;

import pl.krbz.MainApplication;
import pl.krbz.R;

public class API {

  public static String getBaseUri() {
    Activity activity = MainApplication.getActiveActivity();

    return activity.getResources().getString(R.string.payu_API_base_uri);
  }

  public static void makeBlikPayment() {
    //
    // onCallback -> pass info to RN
  }

  public static String makeCardPayment(String token) {
    //    {
    //      "status":{
    //      "statusCode":"WARNING_CONTINUE_CVV",
    //    },
    //      "redirectUri":"{redirect_url}",
    //      "orderId":"WZHF5FFDRJ140731GUEST000P01",
    //      "extOrderId":"{your_order_id}",
    //    }

    // return "WARNING_CONTINUE_CVV";
    return "WARNING_CONTINUE_3DS";
  }
}
