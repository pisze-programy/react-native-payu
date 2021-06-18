package pl.krbz.payu.payment.types;

import android.app.Application;

public abstract class MainApplication extends Application {
    public abstract void setGooglePayService();
}
