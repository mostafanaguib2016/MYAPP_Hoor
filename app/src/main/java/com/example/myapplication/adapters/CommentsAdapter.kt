package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemCommentBinding
import com.example.myapplication.models.CommentModel
import com.squareup.picasso.Picasso

class CommentsAdapter: RecyclerView.Adapter<CommentsAdapter.ViewHolder>()
{
    var list = ArrayList<CommentModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(
                    DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.item_comment,
                            parent,false
                    )
            )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = list[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(var binding : ItemCommentBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(currentItem: CommentModel){
            if (currentItem.userImage.isNotEmpty())
                Picasso.get().load(currentItem.userImage).into(binding.profileImg)

            binding.postTxt.text = currentItem.comment
            binding.profileName.text = currentItem.userName
        }
    }



    fun setData(a: ArrayList<CommentModel>) {
        this.list = a
        notifyDataSetChanged()
    }

}