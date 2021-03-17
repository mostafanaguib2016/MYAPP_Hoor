package com.example.myapplication.activity.chat.chat

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.activity.chat.MessagesViewModel
import com.example.myapplication.databinding.ActivityChatBinding
import com.example.myapplication.models.MessageModel
import com.example.myapplication.util.UserInfo

class ChatActivity: AppCompatActivity()
{

    lateinit var binding: ActivityChatBinding
    lateinit var chatAdapter: ChatAdapter
    lateinit var viewModel: MessagesViewModel
    lateinit var list: ArrayList<MessageModel>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)

        val ownerId = intent.extras!!.getString("id")!!
        val ownerName = intent.extras!!.getString("ownerName")!!
        val userId = intent.extras!!.getString("userId")!!
        val userName = intent.extras!!.getString("userName")

        Log.e("TAG IDs", "onCreate: $ownerId  $userId")

        chatAdapter = ChatAdapter(this)
        viewModel = ViewModelProvider.NewInstanceFactory().create(MessagesViewModel::class.java)
        list = ArrayList()

        viewModel.showChat(userId, ownerId)
        viewModel.messageMutableLiveData.observe(this, Observer {
            chatAdapter.setData(it as java.util.ArrayList<MessageModel>)
        })

        binding.btnSend.setOnClickListener {

            val msgModel = MessageModel()

            val timestamp = "" + System.currentTimeMillis()

            if (ownerId == UserInfo(this).getuserId())
            {
                msgModel.senderId = ownerId
                msgModel.senderName = ownerName
                msgModel.receiverId = userId
                msgModel.receiverName = userName
            }
            else
            {
                msgModel.senderId = userId
                msgModel.senderName = userName
                msgModel.receiverId = ownerId
                msgModel.receiverName = ownerName
            }

            msgModel.message = binding.etMessage.text.toString()
            msgModel.ownerId = ownerId
            msgModel.ownerName = ownerName
            msgModel.userId = userId
            msgModel.userName = userName
            msgModel.timestamp = timestamp
            msgModel.userImage = ""

            viewModel.addMessage(msgModel)

            viewModel.sendMsgMutableLiveData.observe(this, Observer {
                if (it.isSuccessful) {
                    viewModel.showChat(userId, ownerId)
                    viewModel.messageMutableLiveData.observe(this, Observer {
                        chatAdapter.setData(it as java.util.ArrayList<MessageModel>)
                        chatAdapter.notifyDataSetChanged()
                    })
                } else
                    Log.e("TAG", "onCreate: ${it.toString()}", )
            })

            binding.etMessage.text.clear()

        }

        binding.lvChat.adapter = chatAdapter

    }

}