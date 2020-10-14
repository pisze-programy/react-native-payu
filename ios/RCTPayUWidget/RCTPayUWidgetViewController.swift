import UIKit
import PayU_SDK_Lite

class RCTPayUWidgetViewController: UIViewController {
  var paymentWidgetVisualStyle: PUVisualStyle!
  var paymentWidgetService: PUPaymentWidgetService!
  var networkingService = NetworkingService()
  var cvvAuthorizationHandler: PUCvvAuthorizationHandler!
  var applePayHandler: PUApplePayHandler!
  
  var payUEnvironment: PUEnvironment = .sandbox
  var payUPosID = "301948"
  var isBlikEnabled = true
  
  
  func configureView() {
    // Configure payment components
    paymentWidgetVisualStyle = PUVisualStyle()
    paymentWidgetVisualStyle.accentColor = AppBranding.primaryColor
      
    let viewController = UIApplication.shared.windows.first!.rootViewController

    configurePaymentWidgetService()
    cvvAuthorizationHandler = PUCvvAuthorizationHandler(visualStyle: paymentWidgetVisualStyle, uIparent: viewController!, environment: payUEnvironment)
  }
  
  func configurePaymentWidgetService() {
      
    let config = PUPaymentMethodsConfiguration()
    config.environment = payUEnvironment
    config.posID = payUPosID
    config.isBlikEnabled = isBlikEnabled
      
    let paymentMethods = networkingService.fetchPaymentMethods()
    
    config.cardTokens = paymentMethods.filter({ $0 is PUCardToken }) as! [PUCardToken]
    config.blikTokens = paymentMethods.filter({ $0 is PUBlikToken }) as! [PUBlikToken]
    config.payByLinks = paymentMethods.filter({ $0 is PUPayByLink }) as! [PUPayByLink]
    config.pexTokens = paymentMethods.filter({ $0 is PUPexToken }) as! [PUPexToken]

    self.paymentWidgetService = PUPaymentWidgetService(configuration: config)
    self.paymentWidgetService.delegate = self
  }
  
  @objc
  func dismissKeyboard (_ sender: UITapGestureRecognizer) {
      view.endEditing(true)
  }
  
  private func navigate(to viewController: UIViewController) {
    let rootVC = UIApplication.shared.windows.first!.rootViewController
    let navigationController = UINavigationController(rootViewController: viewController)
    navigationController.modalPresentationStyle = .currentContext
    rootVC!.present(navigationController, animated: true, completion: nil)
  }
  
  private func displayShoppingConfirmation() {
    print("--- displayShoppingConfirmation", "displayShoppingConfirmation");
    // TODO: call RN
  }
  
  private func displayError(_ error: String?) {
    print("--- displayError", error!);
  }
  
  func payAction() {
      // 1. Ensure user has selected any payment method
      guard let selectedPaymentMethod = paymentWidgetService.selectedPaymentMethod else {
          return
      }
      
      // 2. Check which method is selected
      switch selectedPaymentMethod {
          
          
      case is PUBlikCode: // HINT: PUBlikCode is equivalent for PayU Android SKD's BlikGeneric
          
          // 2.1.1 Ensure there is a valid code in textfield.
          if paymentWidgetService.isBlikAuthorizationCodeRequired,
              let code = paymentWidgetService.blikAuthorizationCode {
              
              // 2.1.2 Ask your networking service to create order request (OCR) with given data
              networkingService.createOrder(withBlikCode: code) { [weak self] in
                  // 2.1.3 Payment completed, present success or failure
                  self?.displayShoppingConfirmation()
              }
          } else {
              // 2.1.1.1 Blik code is invalid, show error
              displayError("Invalid blik code")
          }
          break
          
      case is PUBlikToken:
          
          // 2.2.0 - If user tapped "Enter new BLIK code", isBlikAuthorizationCodeRequired is set. Handle this like PUBlikCode.
          if paymentWidgetService.isBlikAuthorizationCodeRequired {
              
              // 2.2.0.1 - same as 2.1.1
              if let code = paymentWidgetService.blikAuthorizationCode {
                  
                  // 2.2.0.2 - same as 2.1.2
                  networkingService.createOrder(withBlikCode: code) { [weak self] in
                      // 2.2.0.3 - same as 2.1.3
                      self?.displayShoppingConfirmation()
                  }
                  break
              } else {
                  // 2.2.0.1.1 - same as 2.1.1.1
                  displayError("Invalid blik code")
                  break
              }
          }
          
          // 2.2.1 Ask your networking service to create order request (OCR) with given data
          networkingService.createOrder(forBlikTokenPaymentMethod: selectedPaymentMethod as! PUBlikToken) { [weak self] blikAlternatives in
              
              // 2.2.2 If blik alternatives exists, present them in PUBlikAlternativesViewController.
              //       Don't forget to set the delegate.
              if let blikAlternatives = blikAlternatives {
                  let chooseBlikController = PUBlikAlternativesViewController(itemsList: blikAlternatives, visualStyle: self?.paymentWidgetVisualStyle ?? PUVisualStyle.default())
                  chooseBlikController.delegate = self
                  self?.navigate(to: chooseBlikController)
              } else {
                  // 2.2.3 Payment completed, present success or failure
                  displayShoppingConfirmation()
              }
          }
          break
          
      // HINT: Pay by link and PEX are similar web payment methods
      case is PUPayByLink, is PUPexToken:
          
          // 2.3.1 Ask your networking service to create order request (OCR) with given data
          networkingService.crateOrder(withWebPaymentMethod: selectedPaymentMethod) { [weak self] authorizationRequest in
              
              // 2.3.2 Prepare PUWebAuthorizationViewController object reference
              var authorizationController: PUWebAuthorizationViewController?
              
              // 2.3.3 Create a proper PUWebAuthorizationViewController object
              if let authorizationRequest = authorizationRequest as? PUPayByLinkAuthorizationRequest {
                  authorizationController = PUWebAuthorizationBuilder().viewController(for: authorizationRequest, visualStyle: self?.paymentWidgetVisualStyle ?? PUVisualStyle.default())
              } else if let authorizationRequest  = authorizationRequest as? PUPexAuthorizationRequest {
                  authorizationController = PUWebAuthorizationBuilder().viewController(for: authorizationRequest, visualStyle: self?.paymentWidgetVisualStyle ?? PUVisualStyle.default())
              }
              
              // 2.3.4 Present authorizationController - user will continue payment in webview.
              //       Don't forget to set the delegate.
              authorizationController?.authorizationDelegate = self;
              if let authorizationController = authorizationController {
                  self?.navigate(to: authorizationController)
              }
          }
          break
          
      case is PUCardToken:
          // 2.4.1 Ask your networking service to create order request (OCR) with given data
          networkingService.crateOrder(withCardTokenPaymentMethod: selectedPaymentMethod as! PUCardToken) { [weak self] authorizationRequest, refReqId in
              
              // 2.4.2 If there is a CVV authorization challange, authorize it using refReqId and cvvAuthorizationHandler
              if let refReqId = refReqId {
                  self?.cvvAuthorizationHandler.authorizeRefReqId(refReqId) { [weak self] result in
                      // 2.4.2.1 Payment completed, present success or failure
                      if result == PUCvvAuthorizationResult.statusSuccess {
                          self?.displayShoppingConfirmation()
                      } else {
                          self?.displayError("cvv authorization failed")
                      }
                  }
                  return
              }
              
              // 2.4.3 If there is a 3ds authorization challange, prepare and present PUWebAuthorizationViewController
              if let authorizationRequest = authorizationRequest {
                  let authorizationController = PUWebAuthorizationBuilder().viewController(for: authorizationRequest, visualStyle: self?.paymentWidgetVisualStyle ?? PUVisualStyle.default())
                  authorizationController.authorizationDelegate = self;
                  self?.navigate(to: authorizationController)
                  return
              }
              
              // 2.4.4 If there is no challenge, payment is completed, present success or failure
              displayShoppingConfirmation()
          }
          break
          
      case is PUApplePay:
        print("---PUApplePay", "case");
        // 2.5.1 Create PUApplePayTransaction object with your data
        let applePayTransaction = PUApplePayTransaction(merchantIdentifier: "merchant.pl.goingapp.going",
                                                        currencyCode: PUCurrencyCode.PLN,
                                                        countryCode: PUCountryCode.PL,
                                                        contactEmailAddress: "email_address@toUser.pl",
                                                        paymentItemDescription: "Item description",
                                                        amount: NSDecimalNumber(string: "10.5"))
        
        // 2.5.2 Create PUApplePayHandler object and assign a delegate
        applePayHandler = PUApplePayHandler()
        self.applePayHandler.delegate = self
        
        let viewController = UIApplication.shared.windows.first!.rootViewController
        // 2.5.3 Authorize transaction with applePayHandler.
        applePayHandler.authorizeTransaction(applePayTransaction, withUIparent: viewController)
        print("---PUApplePay", "authorizeTransaction");
        // 2.5.4 In delegate method, ask your networking service to create order request (OCR) with given data
          
      default:
          break
      }
  }

}

extension RCTPayUWidgetViewController: PUPaymentWidgetServiceDelegate {
  func paymentWidgetServiceDidDeselectPaymentMethod(_ paymentWidgetService: PUPaymentWidgetService) {
      // update UI if needed (e.g. disable PAY button)
  }
  
  func paymentWidgetService(_ paymentWidgetService: PUPaymentWidgetService, didSelect cardToken: PUCardToken) {
      // update UI if needed (e.g. enable PAY button)
  }
  
  func paymentWidgetService(_ paymentWidgetService: PUPaymentWidgetService, didSelect payByLink: PUPayByLink) {
      // update UI if needed (e.g. enable PAY button)
  }
  
  func paymentWidgetService(_ paymentWidgetService: PUPaymentWidgetService, didSelect applePay: PUApplePay) {
      // update UI if needed (e.g. enable PAY button)
    print("---PUApplePay", "paymentWidgetService");
  }
  
  func paymentWidgetService(_ paymentWidgetService: PUPaymentWidgetService, didSelect blikCode: PUBlikCode) {
      // update UI if needed (e.g. enable PAY button)
  }
  
  func paymentWidgetService(_ paymentWidgetService: PUPaymentWidgetService, didSelect blikToken: PUBlikToken) {
      // update UI if needed (e.g. enable PAY button)
  }
  
  func paymentWidgetService(_ paymentWidgetService: PUPaymentWidgetService, didSelect pexToken: PUPexToken) {
      // update UI if needed (e.g. enable PAY button)
  }
  
  func paymentWidgetService(_ paymentWidgetService: PUPaymentWidgetService, didDelete cardToken: PUCardToken) {
      // update UI if needed
  }
  
  func paymentWidgetService(_ paymentWidgetService: PUPaymentWidgetService, didDelete pexToken: PUPexToken) {
      // update UI if needed
  }
}

extension RCTPayUWidgetViewController: PUBlikAlternativesViewControllerDelegate {
  func blikAlternativesViewController(_ blikAlternativesViewController: PUBlikAlternativesViewController, didSelect blikAlternative: PUBlikAlternative) {
      // 2.2.2.1 Ask your networking service to continue payment with selected blik alternative
      networkingService.continuePayment(withBlikAlternative: blikAlternative) { [weak self] in
          // 2.2.2.2 Payment completed, present success or failure
          self?.displayShoppingConfirmation()
      }
  }
}

extension RCTPayUWidgetViewController: PUAuthorizationDelegate {
  
  func authorizationRequest(_ request: PUAuthorizationRequest, didFinishWith result: PUAuthorizationResult, userInfo: [AnyHashable : Any]? = nil) {
      switch result {
      case .success:
          // 2.3.5 Payment completed, present success
          handleAuthorizationResultSuccess()
          break
      case .failure:
          // 2.3.5 Payment completed with failure, present failure
          guard let userInfo = userInfo,
              let error = userInfo[PUAuthorizationResultErrorUserInfoKey] as? Error
              else { return }
          handleAuthorizationResultFailure(error)
          break
      case .continueCvv:
          // 2.4.3.1 If there is a CVV authorization challange, authorize it using refReqId and cvvAuthorizationHandler (note: cvv authorization challenge can appear as a part of 3ds authorization as well)
          guard let request = request as? PU3dsAuthorizationRequest,
              let userInfo = userInfo,
              let refReqId = userInfo[PUAuthorizationResultRefReqIdUserInfoKey] as? String else { return }
          handleAuthorizationResultContinueCVV(request, refReqId: refReqId)
          break
      default:
          break
      }
  }

  private func handleAuthorizationResultSuccess() {
      displayShoppingConfirmation()
  }
  private func handleAuthorizationResultFailure(_ error: Error) {
      displayError("authorization failed")
  }
  private func handleAuthorizationResultContinueCVV(_ requst: PU3dsAuthorizationRequest, refReqId: String) {
      self.cvvAuthorizationHandler.authorizeRefReqId(refReqId) { [weak self] result in
          // 2.4.3.2 show error or continue depending on the result
          switch result {
          case .statusSuccess:
              self?.displayShoppingConfirmation()
          case .statusFailure:
              self?.displayError("cvv authorization failed")
          case .statusCanceled:
              self?.displayError("cvv authorization cancelled")
          }
      }
  }
}

extension RCTPayUWidgetViewController: PUApplePayHandlerDelegate {
  func paymentTransactionCanceled(byUser transaction: PUApplePayTransaction!) {
      // handle cancel action
      displayError("Payment canceled by user")
  }

  func paymentTransaction(_ transaction: PUApplePayTransaction!, result: String!) {
      // 2.5.4 Ask your networking service to create order request (OCR) with given data
      networkingService.crateOrder { [weak self] in
          // 2.5.5 Payment completed, display succes or failure
          self?.displayShoppingConfirmation()
      }
  }
}

