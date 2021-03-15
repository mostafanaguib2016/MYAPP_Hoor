package com.example.myapplication.activity

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.models.UserModel
import com.example.myapplication.util.MyUtil
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class RegisterViewModel: ViewModel()
{
    val signUpMutableLiveData = MutableLiveData<Task<AuthResult>>()
    val userInfoMutableLiveData = MutableLiveData<Task<Void>>()
    val usersMutableLiveData = MutableLiveData<List<UserModel>>()

    private val mStorageRefUser = FirebaseStorage.getInstance().getReference("profile_images")

    private var mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun signUp(email: String, password: String) {

        Log.e("TAG", "signUp: " )

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    signUpMutableLiveData.value = task
                    Log.e("TAG", "signUp: Complete" )
                }
    }

    fun getAllUsers() {
        db.collection("users").get()
                .addOnCompleteListener {
                    val data = ArrayList<UserModel>()
                    it.result!!.forEach {
                        if (it != null)
                            data.add(it.toObject(UserModel::class.java))

                    }
                    Log.e("Data viewModel", data.size.toString() + "     ")
                    usersMutableLiveData.value = data
                }
    }
    fun setUserData(userRequest: UserModel) {

        if (userRequest.profileImage.isNotEmpty()) {

            val imageName = MyUtil.getRandomName() + ".jpg"
            val file: Uri = Uri.fromFile(File(userRequest.profileImage))

            mStorageRefUser.child(imageName).putFile(file)
                    .continueWithTask {
                        mStorageRefUser.child(imageName).downloadUrl
                    }.addOnSuccessListener {

                        userRequest.profileImage = imageName
                        userRequest.profileImageUrl = it.toString()
                        sendData(userRequest)
                    }
        } else {
            sendData(userRequest)
        }


    }

    fun sendData(userRequest: UserModel) {
        val id = db.collection("users").document().id
        userRequest.id = id

        Log.e("TAG ID", "sendData: $id" )

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { t ->

            userRequest.fireBaseToken = t.token

            db.collection("users").document(id).set(userRequest)
                    .addOnCompleteListener {

                        userInfoMutableLiveData.value = it
                    }.addOnFailureListener {
                        Log.e("TAG", "sendData: ${it.localizedMessage}", )
                    }
        }
    }

}