# iOS

Snitcher records a crash on iOS and leaves the termination to the runtime, because an unhandled Kotlin exception that reaches the Objective-C boundary always terminates the process. The crash is persisted synchronously and displayed on the launch that follows it.

## Install

```swift
import Snitcher

@main
struct SampleApp: App {

  init() {
    Snitcher.shared.install()
  }

  var body: some Scene {
    WindowGroup { ContentView() }
  }
}
```

`Snitcher.shared.install()` uses the defaults. Kotlin default arguments do not cross the Objective-C boundary, so the fully configurable entry point takes every parameter:

```swift
Snitcher.shared.install(
  storageDirectory: NSTemporaryDirectory(),
  theme: SnitcherThemeConfig.companion.defaultThemeConfig(),
  strings: SnitcherStrings.companion.defaultStrings(),
  platformInfo: "1.0.0 iPhone",
  catchObjectiveCExceptions: true,
  exceptionHandler: { exception in
    print(exception.stackTrace)
  }
)
```

From Kotlin, the same installer takes default arguments as usual:

```kotlin
Snitcher.install(
  theme = SnitcherThemeConfig(lightColors = SnitcherColor.defaultColors()),
)
```

## Present the screen

```swift
struct ContentView: View {

  @State private var showsCrash = Snitcher.shared.exception.value != nil

  var body: some View {
    MyContent()
      .fullScreenCover(isPresented: $showsCrash) {
        SnitcherScreen {
          Snitcher.shared.clear()
          showsCrash = false
        }
        .ignoresSafeArea()
      }
  }
}

struct SnitcherScreen: UIViewControllerRepresentable {
  let onRestore: () -> Void

  func makeUIViewController(context: Context) -> UIViewController {
    SnitcherViewControllerKt.snitcherViewController(onRestore: onRestore)
  }

  func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

The bundled controller renders the same Compose screens as the other platforms. Build your own screen instead by observing `Snitcher.exception` and calling `ExceptionTraceScreen` from a `ComposeUIViewController`.

## What is captured, and what is not

| Kind of crash | Captured |
| --- | --- |
| Unhandled Kotlin exception, including one crossing into Swift | yes, through `setUnhandledExceptionHook` |
| Unhandled exception inside a coroutine | yes, and the process keeps running |
| Uncaught `NSException` raised by Swift or Objective-C | yes, through `NSSetUncaughtExceptionHandler` |
| Swift runtime trap, such as `fatalError` or a nil force unwrap | no, it raises a signal |
| Memory errors, `SIGSEGV`, `SIGABRT` | no, it raises a signal |
| Watchdog terminations and out of memory kills | no |

Pass `catchObjectiveCExceptions = false` if another crash reporter owns `NSSetUncaughtExceptionHandler`. Snitcher keeps and calls the handler that was installed before it in either case.

The stack trace is a list of symbol strings rather than structured frames, which is what Kotlin/Native exposes, so `SnitcherException.stackTraceElement` is empty on iOS. Symbol names in a release build need the dSYM, or the `sourceInfoType=libbacktrace` binary option:

```kotlin
kotlin {
  targets.withType<KotlinNativeTarget>().configureEach {
    binaries.all {
      binaryOption("sourceInfoType", "libbacktrace")
    }
  }
}
```

## Where the crash is stored

The caches directory of the app by default, which you can change with `storageDirectory`.
