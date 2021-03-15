package com.example.myapplication.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.DecimalFormat
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.net.Uri
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.util.*

class MyUtil {

    companion object {

        private val ALLOWED_CHARACTERS = "qwertyuiopasdfghjklzxcvbnm"

        @RequiresApi(Build.VERSION_CODES.N)
        fun getCurrentFullTime(): String =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().time)
                .toString()

        fun getRandomName(): String =
            getRandomString(5) + System.currentTimeMillis()

        private fun getRandomString(sizeOfRandomString: Int): String {
            val random = Random()
            val sb = StringBuilder(sizeOfRandomString)
            for (i in 0 until sizeOfRandomString)
                sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
            return sb.toString()
        }

        @RequiresApi(Build.VERSION_CODES.N)
        fun calculateDistanceByKM(location1: Location, location2: Location): Float =
            DecimalFormat("0.00").format( location1.distanceTo(location2)/1000).toFloat()

        fun bitMapToString(bitmap: Bitmap): String {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val b = baos.toByteArray()
            return Base64.encodeToString(b, Base64.DEFAULT)
        }

        fun stringToBitMap(encodedString: String?): Bitmap? {
            return try {
                val encodeByte =
                    Base64.decode(encodedString, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
            } catch (e: Exception) {
                e.message
                null
            }
        }


        fun call(mContext: Context, phone: String) {

            val i = Intent(Intent.ACTION_DIAL)
            i.data = Uri.parse("tel:$phone")
            if (i.resolveActivity(mContext.packageManager) != null)
                mContext.startActivity(i)

        }

    }
}