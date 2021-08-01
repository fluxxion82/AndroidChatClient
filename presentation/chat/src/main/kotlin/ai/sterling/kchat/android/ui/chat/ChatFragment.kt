package ai.sterling.kchat.android.ui.chat

import ai.sterling.kchat.R
import ai.sterling.kchat.android.widget.SlowScrollLayoutManager
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.UnknownHostException
import java.util.ArrayList
import java.util.Calendar

@AndroidEntryPoint
class ChatFragment : Fragment() {

    protected val navController
        get() = findNavController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun initView(savedInstanceState: Bundle?) {
//        val chatListAdapter = ChatListAdapter(this as LifecycleOwner, viewModel)
//        // Want to keep latest messages on screen, so smooth scroll to end when items change
//        // Doesn't work as well as I had hoped.
//        chatListAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
//
//            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
//                super.onItemRangeChanged(positionStart, itemCount)
//                binding.recyclerMessages.smoothScrollToPosition(chatListAdapter.itemCount - 1)
//            }
//        })
//        binding.recyclerMessages.layoutManager = SlowScrollLayoutManager(context)
//        binding.recyclerMessages.adapter = chatListAdapter
//        binding.recyclerMessages.setHasFixedSize(true)
    }
}