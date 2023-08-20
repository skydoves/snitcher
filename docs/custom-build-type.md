# Build Types

If you intend to launch distinct trace activities and implement different behaviors or flavors, you can install Snitcher based on specific build types, as shown in the example below:

```kotlin
Snitcher.install(
  application = this,
  traceActivity = if (BuildConfig.DEBUG) {
    MyExceptionTraceActivity::class
  } else {
    traceActivity = RestoreActivity::class,
  },
  exceptionHandler = {
    if (!BuildConfig.DEBUG) {
      Firebase.crashlytics.log(exception.stackTrace)
    }
  }
)
```

Alternatively, you can create a single trace Activity and manage the different build types within the activity itself, as demonstrated in the example below:

```kotlin
Snitcher.install(
  application = this,
  launcher = MyExceptionTraceActivity::class,
)

class MyExceptionTraceActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      val exception by Snitcher.exception.collectAsState()
      val launcher by Snitcher.launcher.collectAsState()

      SnitcherTheme {
        if (exception != null) {
          if (Snitcher.isDebuggable) {
            ExceptionTraceScreen(
              launcher = launcher,
              snitcherException = exception!!,
            )
          } else {
            AppRestoreScreen(launcher = launcher)
          }
        }
      }
    }
  }
}
```

As demonstrated in the above example, you have the flexibility to create your own trace or restore Activities and install them according to your various build types.