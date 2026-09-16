# Exception Trace Screen

Snitcher provides ready-to-use screens, `ExceptionTraceScreen` and `AppRestoreScreen`, which are Compose Multiplatform composables and render the same way on every platform. Each platform also gives you a host for them, and you are free to replace any of it.

## The screens

```kotlin
@Composable
public fun ExceptionTraceScreen(
  snitcherException: SnitcherException,
  modifier: Modifier = Modifier,
  onRestore: (() -> Unit)? = null,
  onDebug: (() -> Unit)? = null,
)

@Composable
public fun AppRestoreScreen(
  modifier: Modifier = Modifier,
  onRestore: (() -> Unit)? = null,
)
```

A button is hidden when its callback is null, which is how the screens adapt to what a platform supports. iOS cannot restore a throwable, for instance, so it never shows the debug button.

## Android

The bundled `ExceptionTraceActivity` is launched after a crash. Replace it with your own by giving the `traceActivity` parameter:

```kotlin
Snitcher.install(
  application = this,
  traceActivity = MyExceptionTraceActivity::class,
)
```

```kotlin
class MyExceptionTraceActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val exception by Snitcher.exception.collectAsState()
      val launcher by Snitcher.launcher.collectAsState()

      SnitcherTheme {
        exception?.let {
          if (BuildConfig.DEBUG) {
            // the android overload wires the restore and debug actions for you
            ExceptionTraceScreen(launcher = launcher, snitcherException = it)
          } else {
            AppRestoreScreen(launcher = launcher)
          }
        }
      }
    }
  }
}
```

## Desktop

`SnitcherTraceWindow` opens a window whenever there is a crash. Build your own window instead when you want a different shell:

```kotlin
application {
  val exception by Snitcher.exception.collectAsState()

  exception?.let {
    Window(onCloseRequest = { Snitcher.clear() }, title = "Crash") {
      SnitcherTheme {
        ExceptionTraceScreen(
          snitcherException = it,
          onRestore = { Snitcher.clear() },
          onDebug = { Snitcher.debug(it) },
        )
      }
    }
  }
}
```

## iOS

`snitcherViewController(onRestore:)` wraps the screens in a `UIViewController`. Build your own controller when you want to embed the screen in your own navigation:

```kotlin
fun myCrashViewController(): UIViewController = ComposeUIViewController {
  val exception by Snitcher.exception.collectAsState()

  SnitcherTheme {
    exception?.let {
      ExceptionTraceScreen(snitcherException = it, onRestore = { Snitcher.clear() })
    }
  }
}
```
