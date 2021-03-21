package com.example.myapplication.activity.my_ads

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activity.AddProductActivity
import com.example.myapplication.activity.LoginActivity
import com.example.myapplication.activity.ProfileEditUserActivity
import com.example.myapplication.activity.chat.MessagesActivity
import com.example.myapplication.activity.my_ads.adapter.AdapterMyOrders
import com.example.myapplication.activity.my_ads.adapter.AdapterMyProducts
import com.example.myapplication.models.ModelProduct
import com.example.myapplication.models.OrdersModel
import com.example.myapplication.util.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class MyAdsActivity : AppCompatActivity() {

    private lateinit var nameTv: TextView
    private lateinit var emailTv:TextView
    private lateinit var phoneTv:TextView
    private lateinit var tabShopsTv:TextView
    private lateinit var tabOrdersTv:TextView
    private lateinit var myAdsTv:TextView
    private lateinit var shopsRl: RelativeLayout
    private lateinit var ordersRl:RelativeLayout

    private lateinit var logoutBtn: ImageButton
    private lateinit var editProfileBtn:ImageButton
    private lateinit var addProductBtn:ImageButton
    private lateinit var messageBtn:ImageButton
    private lateinit var profileIv: ImageView
    private lateinit var productRv: RecyclerView
    private lateinit var orderRv:RecyclerView

    lateinit var viewModel: MyAdsViewModel
    lateinit var myOrdersAdapter: AdapterMyOrders
    lateinit var myProductsAdapter: AdapterMyProducts

    private val ADD_PRODUCT = 673
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog


    lateinit var userInfo: UserInfo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_ads)

        userInfo = UserInfo(this)
        viewModel = ViewModelProvider(this).get(MyAdsViewModel::class.java)

        myOrdersAdapter = AdapterMyOrders(viewModel, this)
        myProductsAdapter = AdapterMyProducts(viewModel, this)

        nameTv = findViewById(R.id.nameTv)
        emailTv = findViewById(R.id.emailTv)
        shopsRl = findViewById(R.id.shopsRl)
        ordersRl = findViewById(R.id.orderRl)

        logoutBtn = findViewById(R.id.logoutBtn)
        editProfileBtn = findViewById(R.id.editProfileBtn)
        profileIv = findViewById(R.id.profileIv)
        phoneTv = findViewById(R.id.phoneTv)
        tabShopsTv = findViewById(R.id.tabShopsTv)
        tabOrdersTv = findViewById(R.id.tabOrdersTv)
        productRv = findViewById<RecyclerView>(R.id.shopRv)
        orderRv = findViewById(R.id.orderRv)
        addProductBtn = findViewById(R.id.addProductBtn)
        messageBtn = findViewById(R.id.messageBtn)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)
        firebaseAuth = FirebaseAuth.getInstance()

        showShopsUI()

        logoutBtn.setOnClickListener {
            userInfo.logOut()
            MakeMeOffline()
        }



        messageBtn.setOnClickListener {
            startActivity(Intent(this@MyAdsActivity, MessagesActivity::class.java))
        }

        editProfileBtn.setOnClickListener {
            startActivity(Intent(this@MyAdsActivity, ProfileEditUserActivity::class.java))
        }

        addProductBtn.setOnClickListener {
            val intent = Intent(this@MyAdsActivity, AddProductActivity::class.java)
            intent.putExtra("navigation", "product")
            startActivityForResult(intent, ADD_PRODUCT)

        }

        tabShopsTv.setOnClickListener {
            addProductBtn.setOnClickListener {
                val intent = Intent(this@MyAdsActivity, AddProductActivity::class.java)
                intent.putExtra("navigation", "product")
                startActivityForResult(intent, ADD_PRODUCT)
            }
            showShopsUI()
        }

        tabOrdersTv.setOnClickListener {
            addProductBtn.setOnClickListener {
                val intent = Intent(this@MyAdsActivity, AddProductActivity::class.java)
                intent.putExtra("navigation", "order")
                startActivityForResult(intent, ADD_PRODUCT)
            }
            showOrdersUI()
        }


    }

    private fun showShopsUI() {
        shopsRl.visibility = View.VISIBLE
        ordersRl.visibility = View.GONE
        tabShopsTv.setTextColor(resources.getColor(R.color.black))
        tabShopsTv.setBackgroundResource(R.drawable.shape_rect04)
        tabOrdersTv.setTextColor(resources.getColor(R.color.white))
        tabOrdersTv.setBackgroundColor(resources.getColor(android.R.color.transparent))

        viewModel.myProducts(userInfo.getuserId())

        viewModel.myProductsMutableLiveData.observe(this, {

            myProductsAdapter.setData(it as ArrayList<ModelProduct>)

        })

        productRv.layoutManager = LinearLayoutManager(this)

        productRv.adapter = myProductsAdapter

    }

    private fun showOrdersUI() {
        shopsRl.visibility = View.GONE
        ordersRl.visibility = View.VISIBLE
        tabShopsTv.setTextColor(resources.getColor(R.color.white))
        tabShopsTv.setBackgroundColor(resources.getColor(android.R.color.transparent))
        tabOrdersTv.setTextColor(resources.getColor(R.color.black))
        tabOrdersTv.setBackgroundResource(R.drawable.shape_rect04)

        viewModel.myOrders(userInfo.getuserId())

        viewModel.myOrdersMutableLiveData.observe(this, {

            myOrdersAdapter.setData(it as ArrayList<OrdersModel>)

        })

        orderRv.layoutManager = LinearLayoutManager(this)

        orderRv.adapter = myOrdersAdapter


    }


    private fun MakeMeOffline() {
        progressDialog.setMessage("Logging Out...")
        val hashMap = HashMap<String, Any>()
        hashMap["online"] = "false"
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).updateChildren(hashMap)
                .addOnSuccessListener {
                    firebaseAuth.signOut()
                    checkUser()
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(this@MyAdsActivity, "" + e.message, Toast.LENGTH_SHORT).show()
                }
    }

    private fun checkUser() {
        val user = firebaseAuth.currentUser
        if (user == null) {
            startActivity(Intent(this@MyAdsActivity, LoginActivity::class.java))
            finish()
        } else {
            loadMyInfo()
        }
    }

    private fun loadMyInfo() {
        nameTv.text = userInfo.getFullName()
        emailTv.text = userInfo.getEmail()
        phoneTv.text = userInfo.getPhone()
        try {
            Picasso.get().load(userInfo.getImage()).placeholder(R.drawable.ic_person_gray).into(profileIv)
        } catch (e: Exception) {
            profileIv.setImageResource(R.drawable.ic_person_gray)
        }
        viewModel.myProducts(userInfo.getuserId())

        viewModel.myProductsMutableLiveData.observe(this, {

            myProductsAdapter.setData(it as ArrayList<ModelProduct>)

        })

        productRv.layoutManager = LinearLayoutManager(this)

        productRv.adapter = myProductsAdapter

    }

}