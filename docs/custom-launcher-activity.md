# Launcher (Restore) Activity

Furthermore, you can customize the launcher (restore activity), specifying which Activity should be executed upon restoration within the trace activity. If you don't specify a `launcher` activity, the most recent Activity that encountered a crash will automatically be launched when users press the 'restore' button. However, if you wish to launch a particular Activity instead of the most recent one, you can accomplish this by providing the launcher parameter:

```kotlin
Snitcher.install(
  application = this,
  launcher = MainActivity::class,
)
```