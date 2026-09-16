# Launcher (Restore) Activity

This one is Android only, since an application cannot relaunch itself on iOS, and the desktop process keeps running after a crash.

You can customize the launcher, which specifies the Activity that is started when the user restores the app from the trace screen. If you don't specify a `launcher`, the most recent Activity before the crash is launched:

```kotlin
Snitcher.install(
  application = this,
  launcher = MainActivity::class,
)
```

The launcher is published as a `StateFlow`, so your own trace screen can read it:

```kotlin
val launcher: String by Snitcher.launcher.collectAsState()
```
