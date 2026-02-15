package com.gma.tsunjo.school

import platform.Foundation.NSBundle

actual object AppBuildConfig {
    actual val isDebug: Boolean = NSBundle.mainBundle.bundleIdentifier?.contains(".debug") ?: false
}
