package com.amosh.currencyexchanger.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager


@Suppress("DEPRECATION") // Fixed for higher versions
class ScreenUtils(var context: Context) {
    var metrics: DisplayMetrics
    val height: Int
        get() = metrics.heightPixels

    val width: Int
        get() = metrics.widthPixels

    init {
        val wm = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) context.display
            else wm.defaultDisplay

        metrics = DisplayMetrics()
        display?.getRealMetrics(metrics)
    }
}