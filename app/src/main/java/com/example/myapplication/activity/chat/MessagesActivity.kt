package com.example.myapplication.activity.chat

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMessagesBinding
import com.example.myapplication.models.MessageModel
import com.example.myapplication.util.UserInfo

class MessagesActivity: AppCompatActivity()
{

    lateinit var binding: ActivityMessagesBinding
    lateinit var adapter: MessageAdapter
    lateinit var viewModel: MessagesViewModel
    lateinit var list: ArrayList<MessageModel>

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_messages)

        viewModel = ViewModelProvider.NewInstanceFactory().create(MessagesViewModel::class.java)

        val userId = UserInfo(this).getuserId()

        list = ArrayList()

        viewModel.showMessages(userId)
        viewModel.messageMutableLiveData.observe(this, Observer {

            list = it as ArrayList<MessageModel>
            adapter.setData(list)

            if (it.isEmpty())
                binding.noData.visibility = VISIBLE

        })

        adapter = MessageAdapter(this)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

    }

}