package com.example.myapplication.activity.chat.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.models.MessageModel
import com.example.myapplication.util.UserInfo

class ChatAdapter(var context: Context) : BaseAdapter()
{
    var list = ArrayList<MessageModel>()
    var userId = UserInfo(context).getuserId()

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): Any = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val currentItem = list[position]

        val view = if (currentItem.senderId == userId)
            LayoutInflater.from(context)
                    .inflate(R.layout.item_chat_back, parent, false)
        else
            LayoutInflater.from(context)
                    .inflate(R.layout.item_chat, parent, false)

        val tv_msg = view.findViewById(R.id.message_tv) as TextView

        tv_msg.text = currentItem.message

        return view
    }

    fun setData(arrayList: ArrayList<MessageModel>){
        this.list = arrayList
        notifyDataSetChanged()
    }

}