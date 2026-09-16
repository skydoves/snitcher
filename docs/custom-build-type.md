# Build Types

If you intend to launch distinct trace activities and implement different behaviors or flavors, you can install Snitcher based on specific build types, as shown in the example below:

```kotlin
Snitcher.install(
  application = this,
  traceActivity = if (BuildConfig.DEBUG) {
    MyExceptionTraceActivity::class
  } else {
    RestoreActivity::class
  },
  exceptionHandler = { exception ->
    if (!BuildConfig.DEBUG) {
      Firebase.crashlytics.log(exception.stackTrace)
    }
  },
)
```

Alternatively, you can create a single trace Activity and manage the different build types within the activity itself, as demonstrated in the example below:

```kotlin
Snitcher.install(
  application = this,
  traceActivity = MyExceptionTraceActivity::class,
)

class MyExceptionTraceActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      val exception by Snitcher.exception.collectAsState()
      val launcher by Snitcher.launcher.collectAsState()

      SnitcherTheme {
        exception?.let {
          if (Snitcher.isDebuggable) {
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

`Snitcher.isDebuggable` is taken from the application's debuggable flag on Android, from the binary kind on iOS, and is true on the desktop. Assign it yourself to decide which screen is displayed:

```kotlin
Snitcher.isDebuggable = BuildConfig.DEBUG
```
