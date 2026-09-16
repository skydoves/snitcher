# Android

Install Snitcher in your `Application` class. Snitcher becomes the default uncaught exception handler, persists the crash synchronously, launches the exception tracing activity, and kills the crashed process.

```kotlin
class App : Application() {

  override fun onCreate() {
    super.onCreate()

    Snitcher.install(application = this)
  }
}
```

`install` is an extension of the `Snitcher` object that lives in the Android source set, so import it explicitly:

```kotlin
import com.skydoves.snitcher.Snitcher
import com.skydoves.snitcher.install
```

## What the user sees

The bundled `ExceptionTraceActivity` displays the full [exception trace screen](custom-trace-screen.md) on a debuggable build, and the friendly restore screen otherwise. The decision comes from `Snitcher.isDebuggable`, which is taken from the application's debuggable flag and can be overridden:

```kotlin
Snitcher.isDebuggable = BuildConfig.DEBUG
```

## Where the crash is stored

The crash is written to `filesDir/snitcher.json` before the process is killed, and it is read back on the next launch, so `Snitcher.exception` is never empty right after a crash.

Clear it once you have handled it:

```kotlin
Snitcher.clear()
```

## Requirements

Android 6.0 (API 23) and above, which is the floor of the Compose and AndroidX libraries that Snitcher builds on.
