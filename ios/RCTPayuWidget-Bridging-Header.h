// RCTBridgeModule will provide an interface to register a bridge module
// required for all types of bridge
#import "React/RCTBridgeModule.h"

// required only for UI Views
#import "React/RCTViewManager.h"

// Corresponding header in your Bridging-Header:
// required only for Event Emitters
#import "React/RCTEventEmitter.h"

// To have access to the UI Manager, you must import its header:
// required for calling methods on ViewManagers
#import "React/RCTUIManager.h"
