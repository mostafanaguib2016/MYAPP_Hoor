package com.example.myapplication.activity

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.adapters.AdapterProductUser
import com.example.myapplication.models.ModelProduct
import com.example.myapplication.models.OrdersModel
import com.example.myapplication.util.MyUtil
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.ArrayList

class AddProductViewModel: ViewModel()
{

    val AddProductLiveData = MutableLiveData<Task<Void>>()
    val addOrderLiveData = MutableLiveData<Task<Void>>()
    val productsLiveData = MutableLiveData<List<ModelProduct>>()
    val deleteProductLiveData = MutableLiveData<Task<Void>>()
    val ordersLiveData = MutableLiveData<List<OrdersModel>>()

    private val mStorageRefUser = FirebaseStorage.getInstance().getReference("product_image")

    private var mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    fun setProductData(modelProduct: ModelProduct) {

        if (modelProduct.productIcon.isNotEmpty()) {

            val imageName = MyUtil.getRandomName() + ".jpg"
            val file: Uri = Uri.fromFile(File(modelProduct.productIcon))

            mStorageRefUser.child(imageName).putFile(file)
                    .continueWithTask {
                        mStorageRefUser.child(imageName).downloadUrl
                    }.addOnSuccessListener {

                        modelProduct.productIcon = imageName
                        modelProduct.productIconUrl = it.toString()
                        sendData(modelProduct)
                    }
        } else {
            sendData(modelProduct)
        }


    }

    fun sendData(modelProduct: ModelProduct){

        db.collection("products").document(modelProduct.productId).set(modelProduct)
                .addOnCompleteListener {

                    AddProductLiveData.value = it
                }.addOnFailureListener {
                    Log.e("TAG", "sendData: ${it.localizedMessage}")
                }

    }

    fun addOrder(orderModel: OrdersModel){

        if (orderModel.orderIcon.isNotEmpty()) {

            val imageName = MyUtil.getRandomName() + ".jpg"
            val file: Uri = Uri.fromFile(File(orderModel.orderIcon))

            mStorageRefUser.child(imageName).putFile(file)
                    .continueWithTask {
                        mStorageRefUser.child(imageName).downloadUrl
                    }.addOnSuccessListener {

                        orderModel.orderIcon = imageName
                        orderModel.orderIconUrl = it.toString()
                        sendOrderData(orderModel)
                    }
        } else {
            sendOrderData(orderModel)
        }
    }

    fun sendOrderData(orderModel: OrdersModel){

        val db = FirebaseFirestore.getInstance()

        val id = db.collection("orders").document().id
        orderModel.orderId = id

        db.collection("orders").document(id).set(orderModel)
                .addOnCompleteListener {
                    addOrderLiveData.value = it
                }

    }

    fun deleteProduct(productId: String){

        db.collection("products").document(productId).delete().addOnCompleteListener {
            if(it.isSuccessful)
            {
                deleteProductLiveData.value = it
                getProducts()
            }
        }

    }

    fun getProducts(){
        db.collection("products").get().addOnCompleteListener { task ->
            val data =ArrayList<ModelProduct>()
            for (snapshot in task.result!!.documents) {
                data.add(snapshot.toObject(ModelProduct::class.java)!!)
            }
            productsLiveData.value = data
        }
    }

    fun getOrders(){
        db.collection("orders").get().addOnCompleteListener { task ->
            val data =ArrayList<OrdersModel>()
            for (snapshot in task.result!!.documents) {
                data.add(snapshot.toObject(OrdersModel::class.java)!!)
            }
            ordersLiveData.value = data
        }
    }


}