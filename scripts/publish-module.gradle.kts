import com.skydoves.snitcher.Configuration

apply(plugin = "com.vanniktech.maven.publish")

rootProject.extra.apply {
  set("libVersion", Configuration.versionName)
}
