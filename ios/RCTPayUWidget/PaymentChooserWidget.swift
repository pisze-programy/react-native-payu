import Foundation
import React

@objc(PaymentChooserWidget)
class PaymentChooserWidget: RCTViewManager {
  var viewComp = RCTPayUWidgetView();
  
  override func view() -> UIView! {
    return viewComp
  }

  override static func requiresMainQueueSetup() -> Bool {
    return true
  }

  @objc func startPaymentProcess() {
    let view = RCTPayUWidgetView();
    
    view.paymentProcess()
  }

}
