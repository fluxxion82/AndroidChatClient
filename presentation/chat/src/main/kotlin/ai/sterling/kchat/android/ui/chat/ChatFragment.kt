package ai.sterling.kchat.android.ui.chat

import ai.sterling.kchat.R
import ai.sterling.kchat.android.ui.base.BaseFragment
import ai.sterling.kchat.android.ui.chat.adapters.ChatListAdapter
import ai.sterling.kchat.android.widget.SlowScrollLayoutManager
import ai.sterling.kchat.databinding.FragmentChatBinding
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.UnknownHostException
import java.util.ArrayList
import java.util.Calendar

class ChatFragment : BaseFragment<FragmentChatBinding, ChatViewModel>() {

    override val viewModelClass = ChatViewModel::class
    override val layoutId = R.layout.fragment_chat

    override fun init(savedInstanceState: Bundle?) = Unit

    override fun initView(savedInstanceState: Bundle?) {
        val chatListAdapter = ChatListAdapter(this as LifecycleOwner, viewModel)
        // Want to keep latest messages on screen, so smooth scroll to end when items change
        // Doesn't work as well as I had hoped.
        chatListAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                binding.recyclerMessages.smoothScrollToPosition(chatListAdapter.itemCount - 1)
            }
        })
        binding.recyclerMessages.layoutManager = SlowScrollLayoutManager(context)
        binding.recyclerMessages.adapter = chatListAdapter
        binding.recyclerMessages.setHasFixedSize(true)
    }
}