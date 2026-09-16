package com.theone.rootbridge

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.io.File

object RootShield {

    fun checkSuBinary(): Boolean {
        val paths = arrayOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/system/sd/xbin/su",
            "/data/local/su",
            "/data/local/bin/su",
            "/system/bin/failsafe/su",
            "/data/local/tmp/su"
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }
        return false
    }

    fun checkDangerousProps(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    fun checkRootPackages(context: Context): Boolean {
        val packages = arrayOf(
            "com.topjohnwu.magisk",
            "me.weishu.kernelsu",
            "com.apatch",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser"
        )
        val pm = context.packageManager
        for (pkg in packages) {
            try {
                pm.getPackageInfo(pkg, 0)
                return true
            } catch (e: PackageManager.NameNotFoundException) {
                // Not found
            }
        }
        return false
    }

    fun isEnvironmentCompromised(context: Context): Boolean {
        return checkSuBinary() || checkDangerousProps() || checkRootPackages(context)
    }
}
