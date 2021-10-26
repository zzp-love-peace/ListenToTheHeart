package com.zzp.dtrip.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zzp.dtrip.R

class FriendFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = FriendFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friend, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        findViewById(view)
    }

    private fun findViewById(root: View?) {
        root?.apply {
        }
    }
}