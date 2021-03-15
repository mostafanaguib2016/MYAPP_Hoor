package com.example.myapplication.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.AdapterProductUser
import com.example.myapplication.adapters.CommentsAdapter
import com.example.myapplication.databinding.ActivityProductDetailsBinding
import com.example.myapplication.models.CommentModel
import com.example.myapplication.models.ModelProduct
import com.example.myapplication.util.UserInfo
import com.google.firebase.firestore.FirebaseFirestore

class ProductDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityProductDetailsBinding
    lateinit var list: ArrayList<CommentModel>
    lateinit var commentsAdapter: CommentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details)

        val productId = intent.extras!!.getString("productId")!!

        getProduct(productId)
        commentsAdapter = CommentsAdapter()

        getComments()

        binding.commentsRv.layoutManager = LinearLayoutManager(this)
        binding.commentsRv.adapter = commentsAdapter

        binding.addCommentBtn.setOnClickListener {
            addComment(productId)
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
                    }
        }

        dialog.show()


    }

    fun getComments(){
        val reference = FirebaseFirestore.getInstance().collection("comments")

        val userId = UserInfo(this).getuserId()
        list = ArrayList()

        reference.get().addOnCompleteListener { task ->
            for (snapshot in task.result!!.documents) {
                val commentModel = snapshot.toObject(CommentModel::class.java)!!

                if (commentModel.userId.equals(userId))
                {
                    list.add(commentModel)
                }

            }
            commentsAdapter.setData(list)

        }
    }

    fun getProduct(productId: String){

        val reference = FirebaseFirestore.getInstance().collection("products")

        reference.get().addOnCompleteListener { task ->
            for (snapshot in task.result!!.documents) {
                val product = snapshot.toObject(ModelProduct::class.java)!!

                if (product.productId.equals(productId))
                {
                    binding.productTb.descriptionTv.text = product.productCategory
                    binding.productTb.originalPriceTv.text= product.originalPrice
                    binding.productTb.descriptionTv.text = product.productDescription
                    break
                }

            }
        }


    }

}