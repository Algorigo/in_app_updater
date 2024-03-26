import Flutter
import UIKit

public class InAppUpdaterPlugin: NSObject, FlutterPlugin {
    
    enum VersionError: Error {
        case invalid
    }
    
    static let METHOD_CHANNEL_NAME = "in_app_updater"
    private var eventSink: FlutterEventSink? = nil
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: METHOD_CHANNEL_NAME, binaryMessenger: registrar.messenger())
        let instance = InAppUpdaterPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
        case "getPlatformVersion":
            result("iOS " + UIDevice.current.systemVersion)
        case "checkUpdateAvailable":
            checkUpdateAvailable(result: result)
        default:
            result(FlutterMethodNotImplemented)
        }
    }
    
    private func checkUpdateAvailable(result: @escaping FlutterResult) {
        guard let info = Bundle.main.infoDictionary,
              let identifier = info["CFBundleIdentifier"] as? String,
              let url = URL(string: "http://itunes.apple.com/kr/lookup?bundleId=\(identifier)"),
              let data = try? Data(contentsOf: url),
              let json = try? JSONSerialization.jsonObject(with: data, options: .allowFragments) as? [String: Any],
              let results = json["results"] as? [[String: Any]],
              let appStoreVersion = results[0]["version"] as? String,
              let oldVersion = info["CFBundleShortVersionString"] as? String else {
            result(VersionError.invalid)
            return
        }
        
        let compareResult = oldVersion.compare(appStoreVersion, options: .numeric)
        switch compareResult {
        case .orderedAscending:
            result(true)
        case .orderedDescending, .orderedSame:
            result(false)
        }
    }
    
}
