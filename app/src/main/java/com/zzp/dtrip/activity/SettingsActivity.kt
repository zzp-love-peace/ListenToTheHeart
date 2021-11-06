package com.zzp.dtrip.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCapture
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCaptureResult
import com.zzp.dtrip.R
import com.zzp.dtrip.data.DeleteFaceBody
import com.zzp.dtrip.data.FaceBody
import com.zzp.dtrip.data.FaceResult
import com.zzp.dtrip.data.NormalResult
import com.zzp.dtrip.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList
import kotlin.concurrent.thread

class SettingsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    private lateinit var navSettingsView: NavigationView

    private lateinit var switchMaterial: SwitchMaterial

    private lateinit var prefs: SharedPreferences
    private var bitmapCurrent: Bitmap? = null
    private var isPermissionRequested = false

    companion object {
        var switchFlag = false
        private const val TAG = "SettingsActivity"
    }

    private val liveFaceCallback: MLLivenessCapture.Callback = object : MLLivenessCapture.Callback {
        override fun onSuccess(result: MLLivenessCaptureResult) {
            //检测成功的处理逻辑，检测结果可能是活体或者非活体。
            if (!result.isLive) {
                "未检测出人脸".showToast()
                return
            }
            bitmapCurrent = result.bitmap
            Log.e(
                "TAG",
                "拍照获取人脸照片" + bitmapCurrent?.width.toString() + "   " + bitmapCurrent?.height
            )
            if (bitmapCurrent == null) {
                "failed to get picture!".showToast()
                return
            }
            postFaceData(bitmapCurrent!!)
        }

        override fun onFailure(errorCode: Int) {
            //检测未完成，如相机异常CAMERA_ERROR,添加失败的处理逻辑。
            "检测失败 errorCode = $errorCode".showToast()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ActivityCollector.addActivity(this)
        findViewById()
        initPrefAndSwitch()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navSettingsView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_personal_face -> {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val capture = MLLivenessCapture.getInstance()
                        capture.startDetect(this, liveFaceCallback)
                    } else {
                        checkPermission()
                    }
                }
                R.id.action_personal_delete -> {
                    deleteFaceData()
                }
                R.id.action_personal_password -> {
                    if (UserInformation.isLogin) {
                        val intent = Intent(this, ReplaceActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
                R.id.action_personal_home -> {
                    //switchMaterial.callOnClick()
                    switchMaterial.performClick()
                }
                R.id.action_personal_about -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    startActivity(intent)
                }
                R.id.action_exit_login -> {
                    AlertDialog.Builder(this).apply {
                        setTitle("你确定要退出登录吗？")
                        setPositiveButton("确定") { _, _ ->
                            ActivityCollector.finishAll()
                            val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                            startActivity(intent)
                            "你已退出登录".showToast()
                        }
                        setNegativeButton("取消") { _, _ -> }
                        show()
                    }
                }
            }
            false
        }

        switchMaterial.setOnCheckedChangeListener { _, isChecked ->
            switchFlag = isChecked
            saveSwitchFlag()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun findViewById() {
        toolbar = findViewById(R.id.toolbar)
        navSettingsView = findViewById(R.id.nav_settings_view)
        switchMaterial = findViewById(R.id.switch_material)
    }

    private fun initPrefAndSwitch() {
        prefs = this.getPreferences(Context.MODE_PRIVATE)
        switchFlag = prefs.getBoolean("switch", false)
        switchMaterial.isChecked = switchFlag
        //Log.d(TAG, "initPrefAndSwitch: ")
    }

    private fun saveSwitchFlag() {
        val edit = prefs.edit()
        edit.putBoolean("switch", switchFlag)
        edit.apply()
    }

    private fun deleteFaceData() {
        val appService = RetrofitManager.create<ApiService>()
        val task = appService.deleteFace(DeleteFaceBody(UserInformation.id))
        task.enqueue(object : Callback<NormalResult> {
            override fun onResponse(call: Call<NormalResult>, response: Response<NormalResult>) {
                response.body()?.apply {
                    Log.d(TAG, "onResponse: ${response.code()} $errorCode")
                    if (isError) {
                        Toast.makeText(this@SettingsActivity, "无人脸数据,删除人脸失败!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this@SettingsActivity, "删除人脸成功!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<NormalResult>, t: Throwable) {
                Log.d(TAG, "onFailure ==> $t")
                Toast.makeText(this@SettingsActivity, t.toString(), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {
            isPermissionRequested = true
            val permissionsList = ArrayList<String>()
            for (perm in LiveHandGestureAnalyseActivity.getAllPermission()) {
                if (PackageManager.PERMISSION_GRANTED != this.checkSelfPermission(perm)) {
                    permissionsList.add(perm)
                }
            }
            if (permissionsList.isNotEmpty()) {
                requestPermissions(permissionsList.toTypedArray(), 0)
            }
        }
    }

    private fun postFaceData(image: Bitmap) {
        val appService = RetrofitManager.create<ApiService>()
        val task = appService.postFaceData(
            FaceBody(
                bitmap2Base64(compressImage(image)),
                UserInformation.id
            )
        )
        task.enqueue(object : Callback<FaceResult> {
            override fun onResponse(
                call: Call<FaceResult>,
                response: Response<FaceResult>
            ) {
                Log.d(TAG, "onResponse: ${response.code()}")
                response.body()?.apply {
                    if (isError) {
                        Snackbar.make(toolbar, errorMsg, Snackbar.LENGTH_SHORT).show()
                        Log.d(TAG, "onResponse: $errorMsg")

                    } else {
                        Toast.makeText(
                            this@SettingsActivity, "人脸录入成功",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(TAG, "onResponse: success")
                    }
                }
            }

            override fun onFailure(call: Call<FaceResult>, t: Throwable) {
                Log.d(TAG, "onFailure ==> $t")
                Toast.makeText(this@SettingsActivity, t.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }
}