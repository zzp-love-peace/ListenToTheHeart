package com.zzp.dtrip.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCapture
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCaptureResult
import com.zzp.dtrip.R
import com.zzp.dtrip.activity.SettingsActivity
import com.zzp.dtrip.body.DeleteFaceBody
import com.zzp.dtrip.body.FaceBody
import com.zzp.dtrip.data.FaceResult
import com.zzp.dtrip.data.NormalResult
import com.zzp.dtrip.activity.InformationActivity
import com.zzp.dtrip.activity.LiveHandGestureAnalyseActivity
import com.zzp.dtrip.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class ChatFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = ChatFragment()
        private const val TAG = "ChatFragment"
    }

    private lateinit var chatRecycler: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private var bitmapCurrent: Bitmap? = null
    private var isPermissionRequested = false

    private val liveFaceCallback: MLLivenessCapture.Callback = object : MLLivenessCapture.Callback {
        override fun onSuccess(result: MLLivenessCaptureResult) {
            //检测成功的处理逻辑，检测结果可能是活体或者非活体。
            if (!result.isLive) {
                showUserWrong("未检测出人脸", requireContext())
                return
            }
            bitmapCurrent = result.bitmap
            Log.e(
                "TAG",
                "拍照获取人脸照片" + bitmapCurrent?.width.toString() + "   " + bitmapCurrent?.height
            )
            if (bitmapCurrent == null) {
                showUserWrong("failed to get picture!",requireContext())
                return
            }
            postFaceData(bitmapCurrent!!)
        }

        override fun onFailure(errorCode: Int) {
            //检测未完成，如相机异常CAMERA_ERROR,添加失败的处理逻辑。
            showUserWrong("检测失败 errorCode = $errorCode", requireContext())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_chat, container, false)
        findViewById(view)
        (requireActivity() as AppCompatActivity).let {
            it.setSupportActionBar(toolbar)
            it.supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_menu)
            }
        }
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_personal_info -> {
                    val intent = Intent(requireContext(), InformationActivity::class.java)
                    startActivity(intent)
                }
                R.id.action_personal_face -> {
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val capture = MLLivenessCapture.getInstance()
                        capture.startDetect(requireActivity(), liveFaceCallback)
                    } else {
                        checkPermission()
                    }
                }
                R.id.action_personal_delete -> {
                    deleteFaceData()
                }
                R.id.action_personal_setting -> {
                    val intent = Intent(requireContext(), SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

    private fun findViewById(root: View?) {
        root?.apply {
            chatRecycler = findViewById(R.id.chat_recycler)
            toolbar = findViewById(R.id.tool_bar)
            drawerLayout = findViewById(R.id.drawer_layout)
            navView = findViewById(R.id.nav_view)
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {
            isPermissionRequested = true
            val permissionsList = ArrayList<String>()
            for (perm in LiveHandGestureAnalyseActivity.getAllPermission()) {
                if (PackageManager.PERMISSION_GRANTED != requireContext().checkSelfPermission(perm)) {
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
                                    response: Response<FaceResult>) {
                Log.d(TAG, "onResponse: ${response.code()}")
                response.body()?.apply {
                    if (isError) {
                        Snackbar.make(requireView(), errorMsg, Snackbar.LENGTH_SHORT).show()
                        Log.d(TAG, "onResponse: $errorMsg")

                    } else {
                        Toast.makeText(requireContext()
                            , "人脸录入成功",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(TAG, "onResponse: success")
                    }
                }
            }

            override fun onFailure(call: Call<FaceResult>, t: Throwable) {
                Log.d(TAG, "onFailure ==> $t")
                Toast.makeText(requireContext(), t.toString(), Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(requireContext(), "无人脸数据,删除人脸失败!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "删除人脸成功!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<NormalResult>, t: Throwable) {
                Log.d(TAG, "onFailure ==> $t")
                Toast.makeText(requireContext(), t.toString(), Toast.LENGTH_SHORT).show()
            }

        })
    }

}