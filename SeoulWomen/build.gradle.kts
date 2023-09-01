// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  id("com.android.application") version "8.1.1" apply false
  id("com.android.library") version "8.1.1" apply false
  id("org.jetbrains.kotlin.android") version "1.9.10" apply false
  id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
  id("com.google.dagger.hilt.android") version "2.48" apply false // https://github.com/google/dagger/releases
}