package com.lalas.musicgpt.utils

import android.content.Context

fun Int.toRawUrl(context: Context): String? {
    try {
        val url = "android.resource://${context.packageName}/${this}"
        return url
    } catch (e: Exception) {
        return null
    }

}