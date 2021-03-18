package com.example.myapplication.activity.register

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.activity.MainUserActivity
import com.example.myapplication.activity.RegisterViewModel
import com.example.myapplication.models.UserModel
import com.example.myapplication.util.MyUtil
import com.example.myapplication.util.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore.InstanceRegistry
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.*

class RegisterUserActivity: AppCompatActivity()
{

    private lateinit var backBtn: ImageButton
    private lateinit var profileIv: ImageView
    private lateinit var nameEt: EditText
    private lateinit var phoneEt:EditText
    private lateinit var emailEt:EditText
    private lateinit var passwordEt:EditText
    private lateinit var cPasswordEt:EditText
    private lateinit var shopNameEt:EditText
    private lateinit var deliverFeeEt:EditText
    private lateinit var registerBtn: Button
    private lateinit var registerSellerTv: TextView

    val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1

    val WRITE_EXTERNAL_STORAGE_PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    val CAMERA_PERMISSION = android.Manifest.permission.CAMERA
    val READ_EXTERNAL_STORAGE_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE


    lateinit var userInfo: UserInfo

    lateinit var viewModel: RegisterViewModel

    lateinit var userModel: UserModel

    private val CAMERA_REQUEST_CODE = 200
    private val STORAGE_REQUEST_CODE = 300

    private val IMAGE_PICK_GALLERY_CODE = 400
    private val IMAGE_PICK_CAMERA_CODE = 500

    val storageReference = FirebaseStorage.getInstance().getReference("/profile_images")

    private lateinit var cameraPermissions: Array<String>
    private lateinit var storagePermissions: Array<String>


    private var image_uri: String = ""
    lateinit var uri: Uri

    private var firebaseAuth: FirebaseAuth? = null
    private var progressDialog: ProgressDialog? = null
    private val firebaseStorage: FirebaseStorage? = null
    private val db: InstanceRegistry? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)
        userInfo = UserInfo(this)
        backBtn = findViewById(R.id.backBtn)
        profileIv = findViewById(R.id.profileIv)
        nameEt = findViewById(R.id.nameEt)
        phoneEt = findViewById<EditText>(R.id.phoneEt)
        emailEt = findViewById<EditText>(R.id.emailEt)
        passwordEt = findViewById<EditText>(R.id.passwordEt)
        cPasswordEt = findViewById<EditText>(R.id.cPasswordEt)
        shopNameEt = findViewById<EditText>(R.id.shopNameEt)
        deliverFeeEt = findViewById<EditText>(R.id.deliveryFeeEt)
        registerBtn = findViewById(R.id.registerBtn)
        registerSellerTv = findViewById(R.id.registerSellerTv)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Please wait")
        progressDialog!!.setCanceledOnTouchOutside(false)
        backBtn!!.setOnClickListener(View.OnClickListener { onBackPressed() })
        profileIv!!.setOnClickListener(View.OnClickListener { showImagePickDialog() })
        registerBtn!!.setOnClickListener(View.OnClickListener { inputData() })
//        registerSellerTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(RegisterUserActivity.this, RegisterSellerActivity.class));
//            }
//        });
    }


    private var fullName: String = ""
    private  var phoneNumber:String = ""
    private  var email: String = ""
    private  var password: String = ""
    private  var confirmPassword: String =""
    private  var shopName:String = ""
    private  var deliverFee:String = ""
    private fun inputData() {
        fullName = nameEt.text.toString().trim { it <= ' ' }
        phoneNumber = phoneEt.getText().toString().trim { it <= ' ' }
        email = emailEt.getText().toString().trim { it <= ' ' }
        password = passwordEt.getText().toString().trim { it <= ' ' }
        confirmPassword = cPasswordEt.getText().toString().trim { it <= ' ' }
        shopName = shopNameEt.getText().toString().trim { it <= ' ' }
        deliverFee = deliverFeeEt.getText().toString().trim { it <= ' ' }
        if (!fullName.isEmpty() && !phoneNumber.isEmpty() && !confirmPassword.isEmpty()
                && !email.isEmpty() && !password.isEmpty() && !shopName.isEmpty() && !deliverFee.isEmpty()) {
            if (confirmPassword == password) {
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEt.setError("Invalid email pattern...")
                } else {
                    if (password.length < 6) {
                        passwordEt.setError("Password must be atleast 6 characters long...")
                    } else {
                        createAccount()
                    }
                }
            } else Toast.makeText(this, "Passwords are not identical", Toast.LENGTH_SHORT).show()
        } else {
            if (TextUtils.isEmpty(fullName)) {
                nameEt.error = "Enter Your full name .."
            }
            if (TextUtils.isEmpty(phoneNumber)) {
                phoneEt.setError("Enter Phone Number...")
            }
            if (TextUtils.isEmpty(email)) {
                emailEt.setError("Enter the email")
            }
            if (TextUtils.isEmpty(password)) {
                passwordEt.setError("Enter the password")
            }
            if (TextUtils.isEmpty(confirmPassword)) {
                cPasswordEt.setError("Enter the password confirm")
            }
            if (TextUtils.isEmpty(shopName)) {
                shopNameEt.setError("Please enter the shop name")
            }
            if (TextUtils.isEmpty(deliverFee)) {
                deliverFeeEt.setError("Please enter the delivery fee")
            }
        }
    }

    private fun createAccount() {
        progressDialog!!.setMessage("Creating Account...")
        progressDialog!!.show()
        saveFirebaseData()
    }

    private fun saveFirebaseData() {
        progressDialog!!.setMessage("Saving Account Info...")
        val timestamp = "" + System.currentTimeMillis()
        if (image_uri == null) {
            val hashMap = HashMap<String, Any>()
            hashMap["uid"] = "" + firebaseAuth!!.uid
            hashMap["email"] = "" + email
            hashMap["name"] = "" + fullName
            hashMap["phone"] = "" + phoneNumber
            hashMap["timestamp"] = "" + timestamp
            hashMap["accountType"] = "User"
            hashMap["online"] = "true"
            hashMap["profileImage"] = ""
            val fireBaseToken = FirebaseInstanceId.getInstance().token
            userModel = UserModel(
                    firebaseAuth!!.uid, deliverFee, email, "", "", fullName, phoneNumber, "", shopName, timestamp, true,
                    true, fireBaseToken
            )
            viewModel.signUp(email, password)
            viewModel.signUpMutableLiveData.observe(this, Observer {
                if (it.isSuccessful) {
                    Log.e("REG Activity", "onChanged: IF")
                    viewModel.setUserData(userModel)
                    viewModel.userInfoMutableLiveData.observe(this@RegisterUserActivity, { voidTask ->
                        if (voidTask.isSuccessful) {
                            val intent = Intent(this@RegisterUserActivity, MainUserActivity::class.java)
                            progressDialog!!.dismiss()
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            userInfo.setData(userModel)
                            startActivity(intent)
                        } else {
                            Log.e("REG Activity", "onChanged: error")
                            Log.e("REG Activity", "onChanged: else"
                                    + voidTask.exception!!.localizedMessage)
                            progressDialog!!.dismiss()
                            Toast.makeText(this@RegisterUserActivity, voidTask.result.toString(), Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    progressDialog!!.dismiss()
                    Log.e("REG Activity", "onChanged: ssss" + it.exception!!.localizedMessage)
                }
            })


/*//            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//            ref.child(firebaseAuth.getUid()).setValue(hashMap)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//
//                            progressDialog.dismiss();
//                            startActivity(new Intent(RegisterUserActivity.this, MainUserActivity.class));
//                            finish();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                            progressDialog.dismiss();
//                            startActivity(new Intent(RegisterUserActivity.this, MainUserActivity.class));
//                            finish();
//                        }
//                    });*/
        } else {
            val fireBaseToken = FirebaseInstanceId.getInstance().token
            userModel = UserModel(
                    firebaseAuth!!.uid, deliverFee, email, "",
                    "", fullName, phoneNumber, image_uri, shopName, timestamp, true,
                    true, fireBaseToken
            )
            viewModel.signUp(email, password)
            viewModel.signUpMutableLiveData.observe(this, Observer {
                if (it.isSuccessful) {
                    Log.e("REG Activity", "onChanged: IF")

/*                        viewModel.(userModel);

                        viewModel.getUserInfoMutableLiveData().observe(RegisterUserActivity.this
                                , new Observer<Task<Void>>() {
                                    @Override
                                    public void onChanged(Task<Void> voidTask) {
                                        if (voidTask.isSuccessful())
                                        {
                                            Intent intent = new Intent(RegisterUserActivity.this, MainUserActivity.class);
                                            progressDialog.dismiss();
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                            userInfo.setData(userModel);

                                            startActivity(intent);
                                        }
                                        else {
                                            Log.e("REG Activity", "onChanged: error");

                                            Log.e("REG Activity", "onChanged: else"
                                                    + voidTask.getException().getLocalizedMessage() );

                                            progressDialog.dismiss();
                                            Toast.makeText(RegisterUserActivity.this, voidTask.getResult().toString()
                                                    , Toast.LENGTH_SHORT).show();
                                        }
                                    }


                                });*/


                    val imageName = MyUtil.getRandomName() + ".jpg"
                    val file: Uri = Uri.fromFile(File(image_uri))

                    storageReference.child(imageName).putFile(file)
                            .addOnSuccessListener { taskSnapshot ->
                                val uriTask = taskSnapshot.storage.downloadUrl
                                while (!uriTask.isSuccessful);
                                val downloadImageUri = uriTask.result
                                if (uriTask.isSuccessful) {
                                    userModel!!.profileImage = downloadImageUri.toString()
                                    viewModel!!.sendData(userModel!!)
                                    viewModel.userInfoMutableLiveData.observe(
                                            this, Observer {
                                                if (it.isSuccessful)
                                                {
                                                    userInfo.setData(userModel)
                                                    progressDialog!!.dismiss()
                                                    startActivity(
                                                            Intent(
                                                                    this,MainUserActivity::class.java
                                                            )
                                                    )
                                                }
                                    }
                                    )
                                }
                            }
                            .addOnFailureListener { e ->
                                progressDialog!!.dismiss()
                                Toast.makeText(this@RegisterUserActivity, "" + e.message, Toast.LENGTH_SHORT).show()
                            }
                } else {
                    progressDialog!!.dismiss()
                    Log.e("REG Activity", "onChanged: ssss" + it.exception!!.localizedMessage)
                }
            })
        }
    }


    private fun showImagePickDialog() {
        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Image")
                .setItems(options) { dialog, which ->
                    if (which == 0) {
                       isStoragePermissionGranted(CAMERA_REQUEST_CODE)
                    } else {
                        isStoragePermissionGranted(STORAGE_REQUEST_CODE)
                    }
                }
                .show()
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE)
    }

    private fun pickFromCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description")

//        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE)
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermissions() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE)
    }

    private fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        val result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
        return result && result1
    }

    private fun requestCameraPermissions() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val map = HashMap<String, Int>()

        for (i in permissions.indices)
            map[permissions[i]] = grantResults[i]

        when (requestCode) {

            STORAGE_REQUEST_CODE -> {

                when (map[Manifest.permission.READ_EXTERNAL_STORAGE]) {
                    PackageManager.PERMISSION_GRANTED ->
                        gotoImage(requestCode)
                    else ->
                        Toast.makeText(
                                applicationContext,
                                R.string.should_accept_premission,
                                Toast.LENGTH_SHORT
                        ).show()
                }
            }
            CAMERA_REQUEST_CODE -> {

                when (map[Manifest.permission.WRITE_EXTERNAL_STORAGE]) {
                    PackageManager.PERMISSION_GRANTED ->
                        pickFromCamera()
                    else ->
                        Toast.makeText(
                                applicationContext,
                                R.string.should_accept_premission,
                                Toast.LENGTH_SHORT
                        ).show()
                }
            }
        }

    }

    fun isStoragePermissionGranted(requestCode: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
            ) {
                when(requestCode)
                {
                    CAMERA_REQUEST_CODE -> {
                        pickFromCamera()
                    }
                    else->{
                        gotoImage(STORAGE_REQUEST_CODE)
                    }
                }
                true
            } else {

                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        requestCode
                )
                false
            }
        } else {

            when(requestCode)
            {
                CAMERA_REQUEST_CODE -> {
                    pickFromCamera()
                }
                else->{
                    gotoImage(STORAGE_REQUEST_CODE)
                }
            }
            return true
        }
    }

    private fun gotoImage(requestCode: Int) {
        val intent = Intent(
                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode)

    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == RESULT_OK) {
//            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
//                image_uri = data!!.data
//                profileIv!!.setImageURI(image_uri)
//            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
//                profileIv!!.setImageURI(image_uri)
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (Activity.RESULT_OK == resultCode) {

//            when (requestCode) {
//                STORAGE_REQUEST_CODE -> {
                    image_uri = try {
                        profileIv.setImageURI(data!!.data!!)
                        uri = data.data!!

                        Log.e("URI 222", uri.path.toString() + "    ")

                        val p: String
                        val cursor = contentResolver
                                .query(uri, null, null, null, null)

                        p = if (cursor == null) {
                            uri.path.toString()
                        } else {
                            cursor.moveToFirst()
                            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                            cursor.getString(idx)
                        }
                        p
                    } catch (e: Exception) {
                        profileIv.setImageBitmap(data!!.extras!!["data"] as Bitmap)
                        Log.e("URI Catch", "  " + data!!.dataString!!)
                        MyUtil.bitMapToString((data.extras!!["data"] as Bitmap))
                    }
//                }
//            }
        }

    }


}