# Snitcher Theme

If you just want to use the pre-builts sreens, but want to customize those components, such as colors and strings, you can easily accomplish it by giving a copy of `SnitcherColor` to the `SnitcherTheme`:

```kotlin
SnitcherTheme(
  colors = SnitcherTheme.colors.copy(
    primary = Color.Blue,
    background = Color.White,
    textHighEmphasis = Color.Black
  )
) {
  if (exception != null) {
    ExceptionTraceScreen(
      launcher = launcher,
      snitcherException = exception!!,
    )
  }
}
```

If you wish to personalize the text strings within the pre-built UIs, you can override the following string values within your `strings.xml` file:

```xml
<string name="snitcher_release_crash_screen_title">Oops, Restore the previous screen?</string>
<string name="snitcher_release_crash_screen_description">The app crashed unexpectedly. We apologize for the inconvenience. Would you like to return to where you left off?</string>
<string name="snitcher_release_crash_screen_restore">Restore</string>
<string name="snitcher_debug_crash_screen_restore">Restore App</string>
<string name="snitcher_debug_crash_screen_debug_on_ide">Debug on IDE</string>
<string name="snitcher_debug_crash_screen_stacktrace">Stacktrace</string>
```
