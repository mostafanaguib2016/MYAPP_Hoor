package com.example.myapplication.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.activity.chat.chat.ChatActivity
import com.example.myapplication.adapters.CommentsAdapter
import com.example.myapplication.databinding.ActivityProductDetailsBinding
import com.example.myapplication.models.CommentModel
import com.example.myapplication.models.ModelProduct
import com.example.myapplication.models.OrdersModel
import com.example.myapplication.util.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProductDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityProductDetailsBinding
    lateinit var list: ArrayList<CommentModel>
    lateinit var product: ModelProduct
    lateinit var order : OrdersModel
    lateinit var commentsAdapter: CommentsAdapter
    lateinit var userInfo: UserInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details)

        val productId = intent.extras!!.getString("productId")!!
        val navigation = intent.extras!!.getString("navigation")!!
        val userName = intent.extras!!.getString("userName")!!
        userInfo = UserInfo(this)
        val userId = userInfo.getuserId()


        getProduct(productId, navigation)
        commentsAdapter = CommentsAdapter()

        getComments(productId)

        product = ModelProduct()
        order = OrdersModel()

        binding.commentsRv.layoutManager = LinearLayoutManager(this)
        binding.commentsRv.adapter = commentsAdapter
        binding.userTv.text = userName

        binding.addCommentBtn.setOnClickListener {
            addComment(productId)
        }

        if (navigation == "product")
        {
            binding.timeTv.visibility = GONE
        }
        else
        {
            binding.timeTv.visibility = VISIBLE
        }


        binding.sendMsg.setOnClickListener {

            if (navigation.equals("product")&& product != null)
            {

                val userId = UserInfo(this).getuserId()
                val userName = UserInfo(this).getFullName()
                val userImage = UserInfo(this).getImage()
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("productId", productId)
                intent.putExtra("id", product.userId)
                intent.putExtra("ownerName", product.userName)
                intent.putExtra("ownerImage", product.userImage)
                intent.putExtra("userId",userId)
                intent.putExtra("userName",userName)
                intent.putExtra("userImage",userImage)

                startActivity(intent)
            }
            else if(navigation.equals("order")&& order != null){
                val userId = UserInfo(this).getuserId()
                val userName = UserInfo(this).getFullName()
                val userImage = UserInfo(this).getImage()
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("productId", productId)
                intent.putExtra("id", order.userId)
                intent.putExtra("ownerName", order.userName)
                intent.putExtra("ownerImage", order.userImage)
                intent.putExtra("userId",userId)
                intent.putExtra("userName",userName)
                intent.putExtra("userImage",userImage)
                startActivity(intent)
            }

        }

        binding.editProduct.setOnClickListener {
            if (navigation == "product"){
                val intent = Intent(this, EditProductActivity::class.java)
                intent.putExtra("productId", productId)
                intent.putExtra("navigation", "product")
                startActivity(intent)
            }
            else{
                val intent = Intent(this, EditProductActivity::class.java)
                intent.putExtra("productId", productId)
                intent.putExtra("navigation", "order")
                startActivity(intent)
            }
        }

    }

    fun addComment(productId: String){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.add_comment_dialog)

        val commentModel = CommentModel()
        commentModel.productId = productId
        commentModel.userId = UserInfo(this).getuserId()
        commentModel.userName = UserInfo(this).getFullName()
        commentModel.userImage = UserInfo(this).getImage()

        val addBtn = dialog.findViewById<TextView>(R.id.add_rate_btn)
        val commentEt = dialog.findViewById<EditText>(R.id.rate_long_text)

        addBtn.setOnClickListener {

            val comment = commentEt.text.toString()

            commentModel.comment = comment

            val db = FirebaseFirestore.getInstance()

            val id = db.collection("comments").document().id
            commentModel.commentId = id
            db.collection("comments").document(id).set(commentModel)
                    .addOnCompleteListener {
                        dialog.dismiss()

                        if (it.isSuccessful)
                            getComments(productId)

                    }
        }

        dialog.show()


    }

    fun getComments(productId: String){
        val reference = FirebaseFirestore.getInstance().collection("comments")

        val userId = UserInfo(this).getuserId()
        list = ArrayList()

        reference.get().addOnCompleteListener { task ->
            for (snapshot in task.result!!.documents) {
                val commentModel = snapshot.toObject(CommentModel::class.java)!!

                if (commentModel.productId.equals(productId))
                {
                    list.add(commentModel)
                }

            }
            commentsAdapter.setData(list)

        }
    }

    fun getProduct(productId: String, navigation: String){

        if (navigation.equals("product"))
        {
            val reference = FirebaseFirestore.getInstance().collection("products")

            reference.get().addOnCompleteListener { task ->
                for (snapshot in task.result!!.documents) {
                    product = snapshot.toObject(ModelProduct::class.java)!!

                    if (product.productId.equals(productId))
                    {
                        if (userInfo.getuserId() == product.userId)
                            binding.sendMsg.visibility = GONE

                        binding.titleTv.text = product.productTitle
                        binding.originalPriceTv.text= product.originalPrice
                        binding.descriptionTv.text = product.productDescription
                        binding.categoryTv.text = product.productCategory

                        val ownerID = product.userId
                        val userId = UserInfo(this).getuserId()

                        if (ownerID != userId)
                            binding.editProduct.visibility = GONE

                        try {
                            val orderIcon: String = product.productIconUrl!!
                            Picasso.get().load(orderIcon).placeholder(R.drawable.ic_add_shopping_primary).into(binding.productIconIv)
                        } catch (e: Exception) {
                            binding.productIconIv.setImageResource(R.drawable.ic_add_shopping_primary)
                        }

                        break
                    }

                }
            }
        }

        else{
            val reference = FirebaseFirestore.getInstance().collection("orders")

            reference.get().addOnCompleteListener { task ->
                for (snapshot in task.result!!.documents) {
                    order = snapshot.toObject(OrdersModel::class.java)!!

                    if (order.orderId.equals(productId))
                    {
                        binding.titleTv.text = order.orderTitle
                        binding.originalPriceTv.text= order.originalPrice
                        binding.descriptionTv.text = order.orderDescription
                        binding.timeTv.text = order.orderQuantity
                        binding.categoryTv.text = order.orderCategory

                        val ownerID = order.userId
                        val userId = UserInfo(this).getuserId()

                        if (ownerID != userId)
                            binding.editProduct.visibility = GONE

                        try {
                            val orderIcon: String = order.orderIconUrl!!
                            Picasso.get().load(orderIcon).placeholder(R.drawable.ic_add_shopping_primary).into(binding.productIconIv)
                        } catch (e: Exception) {
                            binding.productIconIv.setImageResource(R.drawable.ic_add_shopping_primary)
                        }

                        break
                    }

                }
            }
        }

    }

}