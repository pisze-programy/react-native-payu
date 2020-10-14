import Foundation
import PayU_SDK_Lite

class NetworkingService {

    // NOTE: hardcoded implementation. Ask your backend to get payment methods
    // check https://payu21.docs.apiary.io/#reference/api-endpoints/paymethods-api-endpoint/retrieve-paymethods-and-tokens
    func fetchPaymentMethods() -> [PUPaymentMethod] {
        var paymentMethods: [PUPaymentMethod] = []

        let cardTokens = [
            PUCardToken(value: "TOK_1JKRVX2NMSQX3Ch2mUYLS2RW5H9o",
                        brandImageUrl: URL(string: "https://static.payu.com/images/mobile/mastercard.png")!,
                        brand: "MC",
                        maskedNumber: "6759 64** **** 8453",
                        expirationYear: "2019",
                        expirationMonth: "11",
                        preferred: false,
                        isEnabled: true)
        ]

        // empty array will result in displaying 6-digit textfield
        let blikTokens = [
            PUBlikToken(value: "TOK_1JKRVX2NMSkjagdaouS2RW5H9o",
                        brandImageUrl: URL(string: "https://static.payu.com/images/mobile/logos/pbl_blik.png")!,
                        type: "UID",
                        isEnabled: true)
        ]

        let payByLinks = [
            PUPayByLink(name: "Pekao24Przelew", value: "o", brandImageUrl: URL(string: "https://static.payu.com/images/mobile/logos/pbl_o.png")!, status: .enabled),
            PUPayByLink(name: "mTransfer", value: "m", brandImageUrl: URL(string: "https://static.payu.com/images/mobile/logos/pbl_m.png")!, status: .enabled),
            PUPayByLink(name: "Płacę z Alior Bankiem", value: "ab", brandImageUrl: URL(string: "https://static.payu.com/images/mobile/logos/pbl_ab_off.png")!, status: .disabled),
            PUPayByLink(name: "Przelew z Idea Bank", value: "ib", brandImageUrl: URL(string: "https://static.payu.com/images/mobile/logos/pbl_ib.png")!, status: .enabled),
            PUPayByLink(name: "ApplePay", value: "jp", brandImageUrl: URL(string: "https://static.payu.com/images/mobile/logos/pbl_jp.png")!, status: .enabled),
        ]

        // NOTE: PEX is a special payment method and is available only on merchant demand. Ask PayU IT support if you want to use PEX.
        let pexTokens = [
            PUPexToken(name: "mTransfer Mobilny",
                       value: "TOKE_XPJ4UKJGHVRPMQPGB6X1JJQCUSS",
                       brandImageUrl: URL(string: "http://static.payu.com/images/mobile/logos/pex_mbank.png")!,
                       accountNumber: "5311...7744",
                       status: .active,
                       payType: "payType",
                       preferred: true,
                       isEnabled: true)
        ]

        paymentMethods.append(contentsOf: cardTokens)
        paymentMethods.append(contentsOf: blikTokens)
        paymentMethods.append(contentsOf: payByLinks)
        paymentMethods.append(contentsOf: pexTokens)

        return paymentMethods
    }

    // MARK: - OCR
    func createOrder(forBlikTokenPaymentMethod paymentMethod: PUBlikToken, completionHandler: ([PUBlikAlternative]?)->()) {

        // NOTE: the flag used only in this simulation
        let shouldSimulateBlikAmbiguity = true

        // 1. Set the parameters and perform network request to your backend

        // 2. Then, map received data and check if there is a Blik Ambiguity issue.
        //      If ambiguity exists, map received data to PUBlikAlternative objects.
        //      Otherwise, continue by presenting success or failure
        let blikAlternatives = shouldSimulateBlikAmbiguity ? [PUBlikAlternative(appLabel: "Mój mbank", appKey: "skbfasdbfkusvfavsdfhvsdfsakdjf"),
                                                              PUBlikAlternative(appLabel: "Moje ING", appKey: "skbfasdbfkusvfavsdfhvsdfsakdjf")] : nil

        // 3. When ambiguity exists, pass PUBlikAlternative objects to PUBlikAlternativesViewController and present it.
        //      User will be able to select one of the alternatives.
        completionHandler(blikAlternatives)

        // 4. When user selects blik alternative, app will be notified in PUBlikAlternativesViewControllerDelegate method.
    }

    func createOrder(withBlikCode blikCode: String, completionHandler: ()->()) {

        // 1. Set the parameters and perform network request to your backend

        // 2. Then, map received data and continue by presenting success or failure
        completionHandler()
    }

    func crateOrder(withWebPaymentMethod paymentMethod: PUPaymentMethod, completionHandler: (PUAuthorizationRequest?)->()) {

        // 1. Prepare PUAuthorizationRequest object reference
        var authorizationRequest: PUAuthorizationRequest?

        // 2. Set the parameters and perform network request to your backend

        // 3. Map received data to PUPexAuthorizationRequest or PUPayByLinkAuthorizationRequest object respectively
        switch paymentMethod {
        case is PUPexToken:
            authorizationRequest = PUPexAuthorizationRequest(orderId: "orderId",
                                                             extOrderId: "extOrderId",
                                                             redirectUri: URL(string: "https://www.platnosci.pl.test.payudc.net/np/newpayment.resume.action?paymentId=173504114&hash=c558b01c0a31508d48949eb807aa8734")!,
                                                             continueUrl: URL(string: "http://multishop.dev.payudc.net/")!)
        case is PUPayByLink:
            authorizationRequest = PUPayByLinkAuthorizationRequest(orderId: "orderId",
                                                                   extOrderId: "estOrderId",
                                                                   redirectUri: URL(string: "https://merch-prod.snd.payu.com/np/newpayment.resume.action?paymentId=73443832&hash=1dc257ebb8dbfa3fef322ff40523251b&js=1")!,
                                                                   continueUrl: URL(string: "https://secure.payu.com/continue")!)
        default:
            break
        }

        // 4. Pass authorizationRequest object to PUWebAuthorizationViewControler and present it. User will continue payment in webview.
        completionHandler(authorizationRequest)

        // 5. When completes the proces, app will be notified in PUAuthorizationDelegate method.
    }

    func crateOrder(withCardTokenPaymentMethod paymentMethod: PUCardToken, completionHandler: (_ threeDSAuthorizationRequest: PU3dsAuthorizationRequest?, _ cvvAuthorizationRefReqId: String?)->()) {

        // NOTE: flags used only in this simulation
        let shouldSimulate3dsAuthorization = false
        let shouldSimulateCvvAuthorization = true

        // 2. Set the parameters and perform network request to your backend

        // 3. Map received data to PU3dsAuthorizationRequest object or CVV authorization RefReqId string respectively
        if shouldSimulate3dsAuthorization {
            let authorizationRequest = PU3dsAuthorizationRequest(orderId: "orderId", extOrderId: "extOrderId", redirectUri: URL(string: "https://merch-prod.snd.payu.com/cpm/threeds/proxy?refReqId=95c0f662a37e2d57098401f4e2e87673")!)

            // 4.1 Pass authorizationRequest object to PUWebAuthorizationViewControler and present it. User will continue payment in webview.
            completionHandler(authorizationRequest, nil)
            return
        } else if shouldSimulateCvvAuthorization {
            let refReqId = "aea3ca2774a83ab9025963005f037a34"

            // 4.2 Pass refReqId string to CvvAuthorizationHandler. User will continue payment by entering cvv code in alert popup.
            completionHandler(nil, refReqId)
            return
        }

        // 4.3 If there is no challenge, continue by presenting success or failure
        completionHandler(nil, nil)
    }

    func crateOrder(withApplePay completionHandler: ()->()) {

        // 1. Set the parameters and perform network request to your backend

        // 2. Then, map received data and continue by presenting success or failure
        completionHandler()
    }


    func continuePayment(withBlikAlternative blikAlternative: PUBlikAlternative, completionHandler: ()->()) {

        // 1. Set the parameters and perform network request to your backend

        // 2. Then, map received data and continue by presenting success or failure
        completionHandler()
    }
}
