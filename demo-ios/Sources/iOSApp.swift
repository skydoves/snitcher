import SwiftUI
import Snitcher

@main
struct SnitcherDemoApp: App {

  init() {
    // The defaults are enough here. Every parameter can be given explicitly instead, which is how
    // you pass your own theme, texts, or an extra exception handler.
    Snitcher.shared.install()
  }

  var body: some Scene {
    WindowGroup {
      ContentView()
    }
  }
}
