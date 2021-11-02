package com.zzp.dtrip.activity

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.navigation.ui.onNavDestinationSelected
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.huawei.hms.mlplugin.asr.MLAsrCaptureActivity
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants
import com.huawei.hms.mlsdk.asr.MLAsrConstants
import com.zzp.dtrip.R

class GestureShowActivity : AppCompatActivity() {
    private lateinit var dialogView: View//用户自定义对话框
    private lateinit var image: ImageView
    private lateinit var layoutDialog: AlertDialog.Builder
    private lateinit var showText: TextView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var visibleString:TextView//
    private val gestureList: List<String> =
        arrayListOf("一", "二", "三", "四", "五", "六", "七", "八", "九", "握拳", "单手比心", "点赞", "差评", "确认")
    private val REQUEST_CODE_ASR: Int = 100
    private var text: String = ""
    private val TAG = "语言转手势"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gesture_show)
        initView()
        initEvent()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // REQUEST_CODE_ASR是第3步中定义的当前Activity和拾音界面Activity之间的请求码。
        if (requestCode == REQUEST_CODE_ASR) {
            when (resultCode) {
                MLAsrCaptureConstants.ASR_SUCCESS -> if (data != null) {
                    val bundle = data.extras
                    // 获取语音识别得到的文本信息。
                    if (bundle!!.containsKey(MLAsrCaptureConstants.ASR_RESULT)) {
                        text = bundle.getString(MLAsrCaptureConstants.ASR_RESULT).toString()
                        showText.text = furrySearch(text)//将手势转语音显示出来
                    }
                }
                MLAsrCaptureConstants.ASR_FAILURE ->  // 识别失败处理。
                    if (data != null) {
                        val bundle = data.extras
                        // 判断是否包含错误码。
                        if (bundle!!.containsKey(MLAsrCaptureConstants.ASR_ERROR_CODE)) {
                            val errorCode = bundle.getInt(MLAsrCaptureConstants.ASR_ERROR_CODE)
                            // 对错误码进行处理。
                        }
                        // 判断是否包含错误信息。
                        if (bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_MESSAGE)) {
                            val errorMsg = bundle.getString(MLAsrCaptureConstants.ASR_ERROR_MESSAGE)
                            // 对错误信息进行处理。
                        }
                        //判断是否包含子错误码。
                        if (bundle.containsKey(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE)) {
                            val subErrorCode =
                                bundle.getInt(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE)
                            // 对子错误码进行处理。
                        }
                    }
                else -> {
                }
            }
        }
    }

    fun initView() {
        bottomNavigationView = findViewById(R.id.gesture_show_navigation)
        visibleString = findViewById(R.id.gesture_string)
        dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_layout, null)
        layoutDialog = AlertDialog.Builder(this)//对话框
        showText = findViewById(R.id.gesture_show_text)
        image = dialogView.findViewById(R.id.custom_view)
        layoutDialog.setView(dialogView)
        layoutDialog.setPositiveButton("确认",//增加个确认键
        DialogInterface.OnClickListener { _, _->})
    }

    private fun initEvent() {
        val navigationItemSelectedListener:BottomNavigationView.OnNavigationItemSelectedListener  =  object: BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.gesture_show_speak -> {
                        if (ContextCompat.checkSelfPermission(
                                this@GestureShowActivity,
                                android.Manifest.permission.RECORD_AUDIO
                            )
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this@GestureShowActivity,
                                arrayOf(Manifest.permission.RECORD_AUDIO), 1
                            )
                        } else {
                            val myIntent =
                                Intent(this@GestureShowActivity, MLAsrCaptureActivity::class.java)
                                    // 设置识别语言为英语，若不设置，则默认识别英语。支持设置："zh-CN":中文；"en-US":英语；"fr-FR":法语；"es-ES":西班牙语；"de-DE":德语；"it-IT":意大利语；"ar": 阿拉伯语；"ru-RU":俄语。
                                    .putExtra(MLAsrCaptureConstants.LANGUAGE, "zh-CN")
                                    // 设置拾音界面是否显示识别结果，MLAsrCaptureConstants.FEATURE_ALLINONE为不显示，MLAsrCaptureConstants.FEATURE_WORDFLUX为显示。
                                    .putExtra(
                                        MLAsrCaptureConstants.FEATURE,
                                        MLAsrCaptureConstants.FEATURE_WORDFLUX
                                    )
                                    // 设置使用场景，MLAsrConstants.SCENES_SHOPPING：表示购物，仅支持中文，该场景对华为商品名识别进行了优化。
                                    .putExtra(MLAsrConstants.SCENES, MLAsrConstants.SCENES_SHOPPING)
                            startActivityForResult(myIntent, REQUEST_CODE_ASR)
                        }
                        return true
                    }
                    R.id.gesture_show_show -> {
                        if (gestureList.contains(text)) {
                            show(text)
                        } else {
                            Toast.makeText(
                                this@GestureShowActivity,
                                "我还在学习该手势中，抱歉！",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        return true
                    }
                    else -> {
                        return false
                    }
                }
            }
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
    }


    /**
     * string和图片对应展示
     */
    private fun show(text: String) {
        when (text) {
            "比心" -> {
                image.setImageResource(R.drawable.heart)//将现有图片塞入自定义对话框中并展示
                layoutDialog.setView(dialogView)
                layoutDialog.show()
            }
            "一" -> {
                image.setImageResource(R.drawable.one)//将现有图片塞入自定义对话框中并展示
                layoutDialog.setView(dialogView)
                layoutDialog.show()
            }
            "二" -> {
                image.setImageResource(R.drawable.two)//将现有图片塞入自定义对话框中并展示
                layoutDialog.setView(dialogView)
                layoutDialog.show()
            }
            "三" -> {
                image.setImageResource(R.drawable.three)//将现有图片塞入自定义对话框中并展示
                layoutDialog.setView(dialogView)
                layoutDialog.show()
            }
            "四" -> {
                image.setImageResource(R.drawable.four)//将现有图片塞入自定义对话框中并展示
                layoutDialog.setView(dialogView)
                layoutDialog.show()
            }
            "五" -> {
                image.setImageResource(R.drawable.five)//将现有图片塞入自定义对话框中并展示
                layoutDialog.setView(dialogView)
                layoutDialog.show()
            }
            "六" -> {
                image.setImageResource(R.drawable.six)//将现有图片塞入自定义对话框中并展示
                layoutDialog.setView(dialogView)
                layoutDialog.show()
            }
            "七" -> {
                image.setImageResource(R.drawable.seven)//将现有图片塞入自定义对话框中并展示
                layoutDialog.setView(dialogView)
                layoutDialog.show()
            }
            "八" -> {
                image.setImageResource(R.drawable.eight)//将现有图片塞入自定义对话框中并展示
                layoutDialog.setView(dialogView)
                layoutDialog.show()
            }
            "九" -> {
                image.setImageResource(R.drawable.nine)//将现有图片塞入自定义对话框中并展示
                layoutDialog.setView(dialogView)
                layoutDialog.show()
            }
            "差评" -> {
                image.setImageResource(R.drawable.criticism)//将现有图片塞入自定义对话框中并展示
                layoutDialog.setView(dialogView)
                layoutDialog.show()
            }
            "点赞" -> {
                image.setImageResource(R.drawable.praise)//将现有图片塞入自定义对话框中并展示
                layoutDialog.setView(dialogView)
                layoutDialog.show()
            }
            "握拳" -> {
                image.setImageResource(R.drawable.fist)//将现有图片塞入自定义对话框中并展示
                layoutDialog.setView(dialogView)
                layoutDialog.show()
            }
            "确认" -> {
                image.setImageResource(R.drawable.sure)//将现有图片塞入自定义对话框中并展示
                layoutDialog.setView(dialogView)
                layoutDialog.show()
            }
            else ->{
            }
        }


    }

    /**
     * 简单的模糊搜索，用于将结果指向理想的目标
     */
    fun furrySearch(text: String): String {
        for(str in gestureList){
            if (str == text){
                visibleString.visibility = View.GONE
                showText.setTextColor(resources.getColor(R.color.black  ))
                return text
            }
            else{
                for(character in text){
                    for(str in gestureList){
                        if (str.contains(character)){
                            visibleString.visibility = View.VISIBLE//抠字达到想要的结果
                            this.text = str
                            showText.setTextColor(resources.getColor(R.color.red))
                            return str
                        }
                        Log.d("列表","$str")
                    }
                    Log.d("字符","$character")
                }
            }
        }
        visibleString.visibility = View.GONE
        showText.setTextColor(resources.getColor(R.color.black))
        return text
    }

}