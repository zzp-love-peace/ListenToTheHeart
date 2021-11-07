package com.zzp.dtrip.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Base64
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.zzp.dtrip.application.MyApplication
import java.io.ByteArrayOutputStream

//图片压缩
fun compressImage(image: Bitmap): Bitmap {
    val matrix = Matrix()
    val w = image.width
    val h = image.height
    val trueWidth = 120.0f
    val trueHeight = trueWidth * h / w
    if (trueWidth > w) return image
    val wsx = trueHeight / w
    matrix.setScale(wsx, wsx)
    return Bitmap.createBitmap(image, 0, 0, w, h, matrix, true)
}

//bitmap转base64
fun bitmap2Base64(image: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    image.compress(
        Bitmap.CompressFormat.JPEG, 100,
        byteArrayOutputStream
    )
    byteArrayOutputStream.flush()
    byteArrayOutputStream.close()
    val imageByteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(imageByteArray, Base64.DEFAULT)
}

fun String.showToast() {
    Toast.makeText(MyApplication.context, this, Toast.LENGTH_SHORT).show()
}

//  隐藏软键盘
fun hideSoftKeyboard(activity: Activity) {
    val inputMethodManager =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(
        activity.currentFocus?.windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
    )
}

fun TextInputLayout.setFocus() {
    editText?.setOnFocusChangeListener { v, hasFocus ->  error = ""}
}

