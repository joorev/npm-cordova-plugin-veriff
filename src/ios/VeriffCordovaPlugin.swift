
import Veriff

@objc(TwilioVideoCordovaPlugin) class VeriffCordovaPlugin: CDVPlugin {
	
	var callbackId: String = ""
	
	@objc(launchVeriffSDK:)
	func launchVeriffSDK(command: CDVInvokedUrlCommand) {
		callbackId = command.callbackId! as String
		let sessionToken = command.arguments[0] as! String
		
		let conf = VeriffConfiguration(sessionToken: sessionToken , sessionUrl: "https://alchemy.veriff.com")
		let veriff = Veriff.shared
		veriff.delegate = self
		veriff.set(configuration: conf!)
		veriff.startAuthentication()
	}
	
	private func returnErrorToCordova(message: String) {
		self.commandDelegate!.send(
			CDVPluginResult(
				status: CDVCommandStatus_ERROR,
				messageAs: message
			),
			callbackId: self.callbackId as String
		)
	}
	
	private func returnSuccessToCordova(message: String){
		self.commandDelegate!.send(
			CDVPluginResult(
				status: CDVCommandStatus_OK,
				messageAs: message),
			callbackId: callbackId
		)
	}
}

extension VeriffCordovaPlugin : VeriffDelegate {
	
	func onSession(result: VeriffResult, sessionToken: String) {
		var text = ""
		switch result.code {
		case .STATUS_DONE:
			// Session is completed from clients perspective.
			text = "STATUS_DONE"
			break
		case .STATUS_SUBMITTED:
			// Photos are successfully uploaded.
			text = "STATUS_SUBMITTED"
			break
		case .STATUS_USER_CANCELED:
			// User cancelled the session.
			text= "STATUS_USER_CANCELED";
			break
		case .STATUS_ERROR_SESSION:
			// Invalid sessionToken is passed to the Veriff SDK.
			text = "STATUS_ERROR_SESSION"
			break
		case .UNABLE_TO_ACCESS_CAMERA:
			// Failed to access device's camera. (either access denied or there are no usable cameras)
			text = "UNABLE_TO_ACCESS_CAMERA"
			break
		case .STATUS_ERROR_NETWORK:
			// Network is unavailable.
			text = "STATUS_ERROR_NETWORK"
			break
		case .STATUS_ERROR_UNKNOWN:
			// Unknown error occurred.
			text = "STATUS_ERROR_UNKNOWN"
			break
		case .UNABLE_TO_ACCESS_MICROPHONE:
			// Failed to access device's microphone.
			text = "UNABLE_TO_ACCESS_MICROPHONE"
			break
        case .UNSUPPORTED_SDK_VERSION:
            // This version of Veriff SDK is deprecated.
            text = "UNSUPPORTED_SDK_VERSION"
            break
		}
		
		self.returnSuccessToCordova(message: text)
	}
}
