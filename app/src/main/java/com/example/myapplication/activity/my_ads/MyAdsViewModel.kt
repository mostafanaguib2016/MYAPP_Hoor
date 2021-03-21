package com.example.myapplication.activity.my_ads

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.models.ModelProduct
import com.example.myapplication.models.OrdersModel
import com.google.firebase.firestore.FirebaseFirestore

class MyAdsViewModel: ViewModel()
{

    val myOrdersMutableLiveData = MutableLiveData<List<OrdersModel>>()
    val myProductsMutableLiveData = MutableLiveData<List<ModelProduct>>()
    private val db = FirebaseFirestore.getInstance()

    fun myOrders(userId: String) {

        db.collection("orders").whereEqualTo("userId",userId)
                .addSnapshotListener { querySnapshot, _ ->

                    val data=ArrayList<OrdersModel>()
                    querySnapshot!!.documents.forEach {
                        data.add(it.toObject(OrdersModel::class.java)!!)
                    }

                    myOrdersMutableLiveData.value=data
                }
    }

    fun myProducts(userId: String){


        db.collection("products").whereEqualTo("userId",userId)
                .addSnapshotListener { querySnapshot, _ ->

                    val data=ArrayList<ModelProduct>()
                    querySnapshot!!.documents.forEach {
                        data.add(it.toObject(ModelProduct::class.java)!!)
                    }

                    myProductsMutableLiveData.value=data
                }
    }

}