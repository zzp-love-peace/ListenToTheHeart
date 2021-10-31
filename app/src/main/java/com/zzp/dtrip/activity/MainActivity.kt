package com.zzp.dtrip.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.huawei.hms.mlsdk.common.MLApplication
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCapture
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCaptureResult
import com.zzp.dtrip.R
import com.zzp.dtrip.body.DeleteFaceBody
import com.zzp.dtrip.body.FaceBody
import com.zzp.dtrip.data.FaceResult
import com.zzp.dtrip.data.NormalResult
import com.zzp.dtrip.fragment.ChatFragment
import com.zzp.dtrip.fragment.FriendFragment
import com.zzp.dtrip.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private val messageFragment = ChatFragment.newInstance()
    private val friendFragment = FriendFragment.newInstance()

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private var isPermissionRequested = false
    private var bitmapCurrent: Bitmap? = null

    private val TAG = "MainActivity"

    private val liveFaceCallback: MLLivenessCapture.Callback = object : MLLivenessCapture.Callback {
        override fun onSuccess(result: MLLivenessCaptureResult) {
            //检测成功的处理逻辑，检测结果可能是活体或者非活体。
            if (!result.isLive) {
                showUserWrong("未检测出人脸", this@MainActivity)
                return
            }
            bitmapCurrent = result.bitmap
            Log.e(
                "TAG",
                "拍照获取人脸照片" + bitmapCurrent?.width.toString() + "   " + bitmapCurrent?.height
            )
            if (bitmapCurrent == null) {
                showUserWrong("failed to get picture!", this@MainActivity)
                return
            }
            postFaceData(bitmapCurrent!!)
        }

        override fun onFailure(errorCode: Int) {
            //检测未完成，如相机异常CAMERA_ERROR,添加失败的处理逻辑。
            showUserWrong("检测失败 errorCode = $errorCode", this@MainActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MLApplication.getInstance().apiKey =
            "CgF6e3x9L8tbJ7yLqpxTYQQhmiVvF4tdvG5CEqxrxMnm5EHxq2uBjzork9ye1W6tllgzBiZPHx1NxDQlD+B5fy3J"
        findViewById()
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.nav_host_fragment, messageFragment)
        transaction.add(R.id.nav_host_fragment, friendFragment)
        transaction.hide(friendFragment)
        transaction.commit()
        bottomNavigationView.setOnNavigationItemSelectedListener {
            it.isChecked = true
            val transaction2 = fragmentManager.beginTransaction()
            when (it.itemId) {
                R.id.navigation_message -> {
                    transaction2.hide(friendFragment)
                    transaction2.show(messageFragment)
                    toolbar.title = "消息"
                }
                R.id.navigation_friend -> {
                    transaction2.hide(messageFragment)
                    transaction2.show(friendFragment)
                    toolbar.title = "联系人"
                }
            }
            transaction2.commit()
            false
        }
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_personal_info -> {
                    val intent = Intent(this, InformationActivity::class.java)
                    startActivity(intent)
                }
                R.id.action_personal_face -> {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
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
                R.id.action_personal_setting -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawers()
            false
        }
    }

    private fun findViewById() {
        toolbar = findViewById(R.id.tool_bar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        bottomNavigationView = findViewById(R.id.bottom_nav_view)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
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
        val appService = RetrofitManager.create<AppService>()
        val task = appService.postFaceData(FaceBody(bitmap2Base64(compressImage(image)), UserInformation.ID))
        task.enqueue(object : Callback<FaceResult> {
            override fun onResponse(call: Call<FaceResult>,
                                    response: Response<FaceResult>
            ) {
                Log.d(TAG, "onResponse: ${response.code()}")
                response.body()?.apply {
                    if (isError) {
                        Snackbar.make(bottomNavigationView, errorMsg, Snackbar.LENGTH_SHORT).show()
                        Log.d(TAG, "onResponse: $errorMsg")

                    } else {
                        Toast.makeText(this@MainActivity
                            , "人脸录入成功",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(TAG, "onResponse: success")
                    }
                }
            }

            override fun onFailure(call: Call<FaceResult>, t: Throwable) {
                Log.d(TAG, "onFailure ==> $t")
                Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteFaceData() {
        val appService = RetrofitManager.create<AppService>()
        val task = appService.deleteFace(DeleteFaceBody(UserInformation.ID))
        task.enqueue(object : Callback<NormalResult> {
            override fun onResponse(call: Call<NormalResult>, response: Response<NormalResult>) {
                response.body()?.apply {
                    Log.d(TAG, "onResponse: ${response.code()} $errorCode")
                    if (isError) {
                        Toast.makeText(this@MainActivity, "无人脸数据,删除人脸失败!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "删除人脸成功!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<NormalResult>, t: Throwable) {
                Log.d(TAG, "onFailure ==> $t")
                Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT).show()
            }

        })
    }
}