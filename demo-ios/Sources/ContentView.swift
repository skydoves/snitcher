import SwiftUI
import UIKit
import Snitcher

struct ContentView: View {

  @State private var showsCrash = Snitcher.shared.exception.value != nil

  var body: some View {
    VStack(spacing: 24) {
      Text("Snitcher")
        .font(.largeTitle.bold())

      Button("Raise an exception") {
        raiseDemoException()
      }
      .buttonStyle(.borderedProminent)
    }
    .onAppear {
      // lets an automated run reproduce a crash without a tap, which is how the screenshots in the
      // documentation are taken.
      if ProcessInfo.processInfo.arguments.contains("--crash-on-launch") {
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) { raiseDemoException() }
      }
    }
    .fullScreenCover(isPresented: $showsCrash) {
      SnitcherScreen(onRestore: {
        Snitcher.shared.clear()
        showsCrash = false
      })
      .ignoresSafeArea()
    }
  }
}

private func raiseDemoException() {
  NSException(
    name: NSExceptionName("RuntimeException"),
    reason: "This is an intended runtime exception.",
    userInfo: nil
  ).raise()
}

/// Wraps the UIViewController that Snitcher builds so that SwiftUI can present it.
struct SnitcherScreen: UIViewControllerRepresentable {

  let onRestore: () -> Void

  func makeUIViewController(context: Context) -> UIViewController {
    SnitcherViewControllerKt.snitcherViewController(onRestore: onRestore)
  }

  func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
