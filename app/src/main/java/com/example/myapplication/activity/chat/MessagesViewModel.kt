package com.example.myapplication.activity.chat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.models.MessageModel
import com.example.myapplication.models.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MessagesViewModel : ViewModel()
{
    val db = FirebaseFirestore.getInstance().collection("messages")
    val dbUsers = FirebaseFirestore.getInstance().collection("messages")

    val messageMutableLiveData = MutableLiveData<List<MessageModel>>()
    val sendMsgMutableLiveData = MutableLiveData<Task<Void>>()
    val userMutableLiveData = MutableLiveData<UserModel>()

    fun showMessages(userId: String) {
        db.orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot, _ ->

                    val messages = ArrayList<MessageModel>()

                    if (querySnapshot != null) {
                        querySnapshot.documents.forEach {
                            messages.add(it.toObject(MessageModel::class.java)!!)
                            Log.e("TAG",messages[0].message)
                        }

                        Log.e("SIZE of",messages.size.toString() + "   ")

                        val list = ArrayList<MessageModel>()

                        for (i in messages.indices){
                            if (messages[i].userId == userId || messages[i].ownerId == userId)
                                list.add(messages[i])
                        }
                        Log.e("SIZE",list.size.toString() + "   ")

                        messageMutableLiveData.value = list
                    }

                }
    }

    fun showChat(ownerId: String,userId: String){
        db.orderBy("timestamp")
                .addSnapshotListener { querySnapshot, _ ->

                    val messages = ArrayList<MessageModel>()

                    if (querySnapshot != null) {
                        querySnapshot.documents.forEach {
                            messages.add(it.toObject(MessageModel::class.java)!!)
                            Log.e("TAG",messages[0].message)
                        }

                        Log.e("SIZE of",messages.size.toString() + "   ")

                        val list = ArrayList<MessageModel>()

                        for (i in messages.indices){
                            if ((messages[i].senderId == ownerId || messages[i].receiverId == ownerId)
                                    && (messages[i].senderId == userId || messages[i].receiverId == userId))
                                list.add(messages[i])
                        }
                        Log.e("SIZE",list.size.toString() + "   ")

                        messageMutableLiveData.value = list
                    }

                }
    }

    fun addMessage(message: MessageModel) {
        val id = db.document().id
        message.id = id

        db.document(id).set(message).addOnCompleteListener {
            if (it.isSuccessful)
            {
                sendMsgMutableLiveData.value = it
                showChat(message.ownerId,message.userId)
            }
        }
    }

    fun getFireBaseToken(userId: String){


        dbUsers.whereEqualTo("userId",userId).addSnapshotListener { value, error ->

            var userModel = UserModel()

            value!!.documents.forEach {
                userModel = it.toObject(UserModel::class.java)!!
            }

            userMutableLiveData.value = userModel

        }
    }

}