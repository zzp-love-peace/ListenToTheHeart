package com.zzp.dtrip

import android.widget.Toast
import com.zzp.ailamp.application.MyApplication

fun String.showToast() { Toast.makeText(MyApplication.context, this, Toast.LENGTH_SHORT).show() }