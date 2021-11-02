package com.zzp.dtrip.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zzp.dtrip.R

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