package com.example.funnysound.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

fun Intent.getData(key: String): String {

    return extras?.getString(key) ?: "intent is null"
}

fun <T> Context.openActivity(it: Class<T>, extras: Bundle.() -> Unit = {}) {
    val intent = Intent(this, it)
    intent.putExtras(Bundle().apply(extras))
    startActivity(intent)
}


fun AppCompatActivity.onBackPressedCallback(isEnabled: Boolean, callback: () -> Unit) {
    onBackPressedDispatcher.addCallback(this,
        object : OnBackPressedCallback(isEnabled) {
            override fun handleOnBackPressed() {
                callback()
            }
        })
}