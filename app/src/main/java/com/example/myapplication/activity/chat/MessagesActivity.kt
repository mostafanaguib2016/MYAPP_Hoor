package com.example.myapplication.activity.chat

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
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
import com.squareup.picasso.Picasso

class MessagesActivity: AppCompatActivity()
{

    lateinit var binding: ActivityMessagesBinding
    lateinit var adapter: MessageAdapter
    lateinit var viewModel: MessagesViewModel
    lateinit var list: ArrayList<MessageModel>
    lateinit var userInfo: UserInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_messages)

        viewModel = ViewModelProvider.NewInstanceFactory().create(MessagesViewModel::class.java)

        userInfo = UserInfo(this)
        val userId = userInfo.getuserId()

        loadMyInfo()
        list = ArrayList()

        viewModel.showMessages(userId)
        viewModel.messageMutableLiveData.observe(this, Observer {


            Log.e("TAG", "onCreateMesage: ${it.size}")



            if (it.isEmpty())
                binding.noData.visibility = VISIBLE
            else
            {
                val size = it.size-2

                list.add(it[0])
                for (i in 0..size)
                {
                    if (it[i+1].userId != it[i].userId)
                        list.add(it[i])
                }

                Log.e("MESSAGES s", "${list[list.size-1].toString()}    ")
                adapter.setData(list)
            }

        })

        adapter = MessageAdapter(this)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

    }

    fun loadMyInfo()
    {

        binding.nameTv.setText(userInfo.getFullName())
        binding.emailTv.setText(userInfo.getEmail())
        binding.phoneTv.setText(userInfo.getPhone())
        try {
            Picasso.get().load(userInfo.getImage()).placeholder(R.drawable.ic_person_gray).into(binding.profileIv)
        } catch (e: Exception) {
            binding.profileIv.setImageResource(R.drawable.ic_person_gray)
        }
    }

}