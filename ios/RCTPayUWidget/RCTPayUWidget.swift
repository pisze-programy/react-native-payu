import Foundation

@objc(RCTPayUWidget)
class RCTPayUWidget: RCTViewManager {
  override func view() -> UIView! {
    return RCTPayUWidgetView()
  }

  override static func requiresMainQueueSetup() -> Bool {
    return true
  }
  
  @objc func startPaymentProcess(_ node: NSNumber) {
    DispatchQueue.main.async {
      let component = self.bridge.uiManager.view(
        forReactTag: node
      ) as! RCTPayUWidgetView
      component.paymentProcess()
    }
  }

}