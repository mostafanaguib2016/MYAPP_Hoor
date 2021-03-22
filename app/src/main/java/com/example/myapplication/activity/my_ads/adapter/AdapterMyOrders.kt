package com.example.myapplication.activity.my_ads.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activity.EditProductActivity
import com.example.myapplication.activity.ProductDetailsActivity
import com.example.myapplication.activity.my_ads.MyAdsViewModel
import com.example.myapplication.databinding.ItemMyOrderBinding
import com.example.myapplication.models.OrdersModel
import com.example.myapplication.util.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class AdapterMyOrders(val viewModel: MyAdsViewModel, val context: Context)
    : RecyclerView.Adapter<AdapterMyOrders.ViewHolder>()
{
    var list = ArrayList<OrdersModel> ()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(
                    DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.item_my_order,
                            parent, false
                    )
            )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = list[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemMyOrderBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(currentItem: OrdersModel){

            val userId = UserInfo(context).getuserId()

            binding.addToCartTv.visibility = GONE
            binding.titleTv.text = currentItem.orderTitle
            binding.descriptionTv.text = currentItem.orderDescription
            binding.originalPriceTv.text = "$ ${currentItem.originalPrice}"
            val ownerId: String = currentItem.userId

            try {
                val orderIcon: String = currentItem.orderIconUrl!!
                Picasso.get().load(orderIcon).placeholder(R.drawable.ic_add_shopping_primary).into(binding.productIconIv)
            } catch (e: Exception) {
                binding.productIconIv.setImageResource(R.drawable.ic_add_shopping_primary)
            }

            binding.deleteBtn.setOnClickListener {

                val db = FirebaseFirestore.getInstance().collection("orders")

                db.document(currentItem.orderId).delete().addOnCompleteListener {
                    notifyDataSetChanged()
                    viewModel.myOrders(userId)
                    viewModel.myOrdersMutableLiveData.observe(context as LifecycleOwner, Observer{
                        setData(it as ArrayList<OrdersModel>)
                    })
                }


            }

            binding.root.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra("productId", currentItem.orderId)
                intent.putExtra("navigation", "order")
                context.startActivity(intent)
            }

            binding.editBtn.setOnClickListener {

                val intent = Intent(context, EditProductActivity::class.java)
                intent.putExtra("productId", currentItem.orderId)
                intent.putExtra("navigation", "order")
                context.startActivity(intent)

            }

        }

    }

    fun setData(list: ArrayList<OrdersModel>){

        this.list = list
        notifyDataSetChanged()

    }


}