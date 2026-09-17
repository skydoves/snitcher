# Tracing Global Exceptions

Every platform installer takes an `exceptionHandler` lambda, which runs with the captured crash before the process goes down. It is useful to report exceptions to another platform, such as [Firebase Crashlytics](https://firebase.google.com/docs/crashlytics).

```kotlin
Snitcher.install(
  application = this,
  exceptionHandler = { exception: SnitcherException ->
    Firebase.crashlytics.log(exception.stackTrace)
  },
)
```

The handler runs on the crashing thread, before the process is killed, so keep it short. Blocking on the network there delays, or prevents, the trace screen.

The handler gives you a `SnitcherException`, which carries the exception name, the message, the whole stack trace, and the thread information.

```kotlin
Snitcher.install(
  application = this,
  exceptionHandler = { exception: SnitcherException ->
    val name: String = exception.packageName
    val message: String = exception.message
    val stackTrace: String = exception.stackTrace
    val threadName: String = exception.threadName

    // do something
  },
)
```

On Android and on the desktop the original throwable can be restored, which is handy for reporters that take a `Throwable`:

```kotlin
val throwable: Throwable = exception.toThrowable()
```

Kotlin/Native does not expose structured stack trace frames, so `toThrowable()` is only available on Android and on the desktop, and `SnitcherException.stackTraceElement` is empty on iOS. The whole trace is always available as a string.

## Chaining with another crash reporter

On Android, Snitcher delegates to the handler that was installed before it, so a reporter keeps working. Two handlers are skipped on purpose: the one from the Android runtime and the one from Firebase Crashlytics, because both kill the process and the trace screen would never appear. Report from `exceptionHandler` instead:

```kotlin
Snitcher.install(
  application = this,
  exceptionHandler = { exception ->
    Firebase.crashlytics.recordException(exception.toThrowable())
  },
)
```

## Observing the crash anywhere

Snitcher publishes the most recent crash as a `StateFlow`, so any part of your app can observe it, including on the launch that follows a crash.

```kotlin
val exception: SnitcherException? by Snitcher.exception.collectAsState()
val launcher: String by Snitcher.launcher.collectAsState()
```

Once you have handled it, clear it so that it is not displayed again:

```kotlin
Snitcher.clear()
```
