import UIKit
import PayU_SDK_Lite

class RCTPayUWidgetView: UIView {
  var payUWidgetViewController = RCTPayUWidgetViewController()

  override init(frame: CGRect) {
    super.init(frame: frame)
    payUWidgetViewController.configureView()

    let paymentWidget = payUWidgetViewController.paymentWidgetService.getWidgetWith(payUWidgetViewController.paymentWidgetVisualStyle)
    paymentWidget.translatesAutoresizingMaskIntoConstraints = false

    self.addSubview(paymentWidget)
    NSLayoutConstraint.activate([
        paymentWidget.leadingAnchor.constraint(equalTo: self.leadingAnchor),
        paymentWidget.trailingAnchor.constraint(equalTo: self.trailingAnchor)
    ])
  }

  required init?(coder aDecoder: NSCoder) {
    fatalError("init has not been implemented")
  }

  @objc func paymentProcess() {
    payUWidgetViewController.payAction();
  }
}
