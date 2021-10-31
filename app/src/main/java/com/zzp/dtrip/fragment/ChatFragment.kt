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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_chat, container, false)

        return view
    }

}