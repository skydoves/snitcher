# Exception Trace Screen

Snitcher provides ready-to-use exception tracing screens (such as `ExceptionTraceActivity`, built with the `ExceptionTraceScreen` Composable), giving you the flexibility to extensively tailor these screens according to your preferences, and even design your own distinct tracing interfaces. 

```kotlin
Snitcher.install(
  application = this,
  traceActivity = ExceptionTraceActivity::class
)
```

If you don't specify the `traceActivity` parameter, the default value will be `ExceptionTraceActivity`. You can tailor the launched activity by modifying the `traceActivity` parameter to match your preferred choice. The example below demonstrates the construction of a customized trace Activity:

```kotlin
class MyExceptionTraceActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      val exception by Snitcher.exception.collectAsState()
      val launcher by Snitcher.launcher.collectAsState()

      SnitcherTheme {
        if (exception != null) {
          if (BuildConfig.DEBUG) {
            // implement your own exception trace screen
            ExceptionTraceScreen(
              launcher = launcher,
              snitcherException = exception!!,
            )
          } else {
            // implement your own app restore screen
            AppRestoreScreen(launcher = launcher)
          }
        }
      }
    }
  }
}
```

As demonstrated in the example above, Snitcher provides access to the `SnitcherException` and the package name of the launcher Activity. This information can be utilized to construct highly customized trace screens that align with your specific needs. Snitcher conveniently provides this information through [StateFlow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/)s, allowing you to observe these values without the need for cumbersome intent handling when initiating the trace Activity.

```kotlin
val exception: SnitcherException? by Snitcher.exception.collectAsState()
val launcher: String by Snitcher.launcher.collectAsState()
```

> **Note**: Following any app crashes, you can readily observe the exception details across various components at any time and from any location.
