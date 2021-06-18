package pl.krbz.payu.payment.services;

import android.app.Activity;

import com.payu.android.front.sdk.payment_library_google_pay_module.service.GooglePayService;

public class PayUGooglePayService extends GooglePayService {
    public PayUGooglePayService(Activity activity) {
        super(activity);
    }
}
