package com.example.myapplication.activity

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.models.ModelProduct
import com.example.myapplication.models.OrdersModel
import com.example.myapplication.util.MyUtil
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.*

class AddProductViewModel: ViewModel()
{

    val AddProductLiveData = MutableLiveData<Task<Void>>()
    val addOrderLiveData = MutableLiveData<Task<Void>>()
    val productsLiveData = MutableLiveData<List<ModelProduct>>()
    val deleteProductLiveData = MutableLiveData<Task<Void>>()
    val ordersLiveData = MutableLiveData<List<OrdersModel>>()
    val updateOrderLiveData = MutableLiveData<Task<Void>>()
    val updateProductLiveData = MutableLiveData<Task<Void>>()

    private val mStorageRefUser = FirebaseStorage.getInstance().getReference("products")
    private val mStorageRefOrder = FirebaseStorage.getInstance().getReference("orders")

    private var mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    fun setProductData(modelProduct: ModelProduct) {

        val db = FirebaseFirestore.getInstance()

        val id = db.collection("products").document().id
        modelProduct.productId = id


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
                        Log.e("TAG Order", "addOrder: ${mStorageRefUser.child(imageName).downloadUrl}")
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

    fun updateProduct(productId: String,product: ModelProduct)
    {

        if (product.productIcon.isNotEmpty()){

            val imageName = MyUtil.getRandomName() + ".jpg"
            val file: Uri = Uri.fromFile(File(product.productIcon))

            mStorageRefUser.child(imageName).putFile(file)
                    .continueWithTask {
                        mStorageRefUser.child(imageName).downloadUrl
                    }.addOnSuccessListener {

                        product.productIcon = imageName
                        product.productIconUrl = it.toString()
                        val hashMap = hashMapOf<String, Any>(
                                "productTitle" to product.productTitle,
                                "productDescription" to product.productDescription,
                                "productCategory" to product.productCategory,
                                "productIcon" to product.productIcon,
                                "productIconUrl" to product.productIconUrl,
                                "productQuantity" to product.productQuantity,
                                "originalPrice" to product.originalPrice
                        )

                        db.collection("products").document(productId)
                                .update(hashMap).addOnCompleteListener {
                                    updateProductLiveData.value = it
                                }

                    }

        }
        else{
            val hashMap = hashMapOf<String, Any>(
                    "productTitle" to product.productTitle,
                    "productDescription" to product.productDescription,
                    "productCategory" to product.productCategory,
                    "productQuantity" to product.productQuantity,
                    "originalPrice" to product.originalPrice
            )

            db.collection("products").document(productId)
                    .update(hashMap).addOnCompleteListener {
                        updateProductLiveData.value = it
                    }

        }

    }

    fun updateOrder(orderId: String,order: OrdersModel){

        if (order.orderIcon.isNotEmpty()){

            val imageName = MyUtil.getRandomName() + ".jpg"
            val file: Uri = Uri.fromFile(File(order.orderIcon))

            mStorageRefUser.child(imageName).putFile(file)
                    .continueWithTask {
                        mStorageRefUser.child(imageName).downloadUrl
                    }.addOnSuccessListener {

                        order.orderIcon = imageName
                        order.orderIconUrl = it.toString()
                        val hashMap = hashMapOf<String, Any>(
                                "orderTitle" to order.orderTitle,
                                "orderDescription" to order.orderDescription,
                                "orderCategory" to order.orderCategory,
                                "orderQuantity" to order.orderQuantity,
                                "originalPrice" to order.originalPrice,
                                "orderIcon" to order.orderIcon,
                                "orderIconUrl" to order.orderIconUrl
                        )

                        db.collection("orders").document(orderId)
                                .update(hashMap).addOnCompleteListener {
                                    updateOrderLiveData.value = it
                                }

                    }

        }
        else{
            val hashMap = hashMapOf<String, Any>(
                    "orderTitle" to order.orderTitle,
                    "orderDescription" to order.orderDescription,
                    "orderCategory" to order.orderCategory,
                    "orderQuantity" to order.orderQuantity,
                    "originalPrice" to order.originalPrice
            )

            db.collection("orders").document(orderId)
                    .update(hashMap).addOnCompleteListener {
                        updateOrderLiveData.value = it
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