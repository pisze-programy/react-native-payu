package pl.krbz.payu.payment.services;

public class API {

  public static String getBaseUri() {
    return "https://api";
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
