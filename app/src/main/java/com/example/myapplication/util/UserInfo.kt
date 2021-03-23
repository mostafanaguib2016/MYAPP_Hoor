package com.example.myapplication.util

import android.content.Context
import android.location.Location
import com.example.myapplication.models.UserModel
import java.util.*

class UserInfo(var context: Context) {

    private var sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    private var editor = sharedPreferences.edit()



    fun isLogged(): Boolean {
        return getEmail().isNotEmpty()
    }

    fun setLocation(latitude: String, longitude: String) {
        editor.putString("latitude", latitude)
        editor.putString("longitude", longitude)
        editor.apply()
    }


    fun setBalance(balance: Float){
        editor.putFloat("balance", balance)
        editor.apply()
    }

    fun getBalance():Float{
        return sharedPreferences.getFloat("balance", 0f)
    }

    fun setLanguage(lang: String) {
        editor.putString("language", lang)
        editor.apply()
    }

    fun setType(type: String) {
        editor.putString("type", type)
        editor.apply()
    }

    fun setOnline(online:Boolean) {
        editor.putBoolean("online", online)
        editor.apply()
    }

    fun isOnline():Boolean = sharedPreferences.getBoolean("online",true)

    fun getLanguage(): String {

        return sharedPreferences.getString("language", Locale.getDefault().language)!!
    }

    fun setData(
        user: UserModel
    ) {

        editor.putString("id", user.id)
        editor.putString("image", user.profileImage)
        editor.putString("image_url", user.profileImageUrl)
        editor.putString("fullName", user.name)
        editor.putString("shopName", user.shopName)
        editor.putString("deliveryFee", user.deliveryFee)
        editor.putString("email", user.email)
        editor.putString("phone", user.phone)
        editor.putString("latitude", user.latitude)
        editor.putString("longitude", user.longitude)

        editor.apply()

    }

    fun updatePhoto(image: String){

        editor.putString("image", image)
        editor.commit()

    }

    fun getuserId(): String {

        return sharedPreferences.getString("id", "")!!
    }

    fun getImage(): String {

        return sharedPreferences.getString("image", "")!!
    }

    fun getImageUrl(): String {

        return sharedPreferences.getString("image_url", "")!!
    }

    fun getFireBaseToken(): String
    {
        return sharedPreferences.getString("token", "")!!
    }


    fun getFullName(): String {

        return sharedPreferences.getString("fullName", "")!!
    }

    fun getShopName(): String {

        return sharedPreferences.getString("shopName", "")!!
    }

    fun getDeliveryFee(): String {

        return sharedPreferences.getString("deliveryFee", "")!!
    }

    fun getEmail(): String {

        return sharedPreferences.getString("email", "")!!
    }

    fun getPhone(): String {

        return sharedPreferences.getString("phone", "")!!
    }

    fun getLatitude(): Double {

        return sharedPreferences.getString("latitude", "")!!.toDouble()
    }

    fun getLongitude(): Double {

        return sharedPreferences.getString("longitude", "")!!.toDouble()
    }

    fun getLocation(): Location {
        val location = Location("")
        location.latitude = sharedPreferences.getString("latitude", "")!!.toDouble()
        location.longitude = sharedPreferences.getString("longitude", "")!!.toDouble()
        return location
    }

    fun logOut() {
        editor.clear().apply()
    }
}