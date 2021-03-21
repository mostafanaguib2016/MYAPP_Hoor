package com.example.myapplication.activity.chat

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activity.chat.chat.ChatActivity
import com.example.myapplication.databinding.ItemMessagesBinding
import com.example.myapplication.models.MessageModel
import com.example.myapplication.util.UserInfo
import com.squareup.picasso.Picasso

class MessageAdapter(val context: Context): RecyclerView.Adapter<MessageAdapter.ViewHolder>()
{

    var list = ArrayList<MessageModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
            ViewHolder(
                    DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.item_messages,
                            parent,false
                    )
            )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem = list[position]
        holder.bind(currentItem)

    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemMessagesBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(currentItem: MessageModel){

            if (currentItem.userImage.isNotEmpty())
                Picasso.get().load(currentItem.userImage).into(binding.userImg)

            binding.userName.text = currentItem.userName
            binding.message.text = currentItem.message



            binding.root.setOnClickListener{

                val userId = currentItem.userId
                val userName = currentItem.userName
                val ownerId = currentItem.ownerId
                val bundle = Intent(context,ChatActivity::class.java)
                bundle.putExtra("id",ownerId)
                bundle.putExtra("ownerName",currentItem.ownerName)
                bundle.putExtra("userId",userId)
                bundle.putExtra("userName",userName)



                context.startActivity(bundle)
            }

            binding.deleteBtn.setOnClickListener {



            }

        }


    }

    fun setData(arrayList: java.util.ArrayList<MessageModel>) {
        this.list = arrayList
        notifyDataSetChanged()
    }



}