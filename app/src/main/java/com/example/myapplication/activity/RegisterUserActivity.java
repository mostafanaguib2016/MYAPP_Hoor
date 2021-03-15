package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.models.UserModel;
import com.example.myapplication.util.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;

import static android.provider.MediaStore.MediaColumns.DATA;

public class RegisterUserActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private ImageView profileIv;
    private EditText nameEt,phoneEt,emailEt,passwordEt,cPasswordEt,shopNameEt,deliverFeeEt;
    private Button registerBtn;
    private TextView registerSellerTv;

    UserInfo userInfo;

    public RegisterViewModel viewModel;

    UserModel userModel;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;


    private String[] cameraPermissions;
    private String[] storagePermissions;

    private Uri image_uri;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore.InstanceRegistry db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        userInfo = new UserInfo(this);

        backBtn = findViewById(R.id.backBtn);
        profileIv = findViewById(R.id.profileIv);
        nameEt = findViewById(R.id.nameEt);
        phoneEt = findViewById(R.id.phoneEt);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        cPasswordEt = findViewById(R.id.cPasswordEt);
        shopNameEt = findViewById(R.id.shopNameEt);
        deliverFeeEt = findViewById(R.id.deliveryFeeEt);
        registerBtn = findViewById(R.id.registerBtn);
        registerSellerTv = findViewById(R.id.registerSellerTv);

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputData();
            }
        });
//        registerSellerTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(RegisterUserActivity.this, RegisterSellerActivity.class));
//            }
//        });
    }


    private String fullName,phoneNumber, email, password, confirmPassword,shopName,deliverFee;
    private void inputData() {

        fullName = nameEt.getText().toString().trim();
        phoneNumber = phoneEt.getText().toString().trim();
        email = emailEt.getText().toString().trim();
        password = passwordEt.getText().toString().trim();
        confirmPassword = cPasswordEt.getText().toString().trim();
        shopName = shopNameEt.getText().toString().trim();
        deliverFee = deliverFeeEt.getText().toString().trim();

        if ( !(fullName.isEmpty()) && !(phoneNumber.isEmpty()) && !(confirmPassword.isEmpty())
                && !(email.isEmpty()) && !(password.isEmpty()) && !(shopName.isEmpty()) && !(deliverFee.isEmpty())){

            if (confirmPassword.equals(password))
            {
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailEt.setError("Invalid email pattern...");
                }
                else {
                    if (password.length()<6){
                        passwordEt.setError("Password must be atleast 6 characters long...");
                    }
                    else {
                        createAccount();
                    }

                }
            }
            else
                Toast.makeText(this, "Passwords are not identical", Toast.LENGTH_SHORT).show();

        }

        else {

            if (TextUtils.isEmpty(fullName)){
                nameEt.setError("Enter Your full name ..");
            }

            if (TextUtils.isEmpty(phoneNumber)){
                phoneEt.setError("Enter Phone Number...");
            }

            if (TextUtils.isEmpty(email)){
                emailEt.setError("Enter the email");
            }
            if (TextUtils.isEmpty(password)){
                passwordEt.setError("Enter the password");
            }

            if (TextUtils.isEmpty(confirmPassword)){
                cPasswordEt.setError("Enter the password confirm");
            }

            if (TextUtils.isEmpty(shopName)){
                shopNameEt.setError("Please enter the shop name");
            }

            if (TextUtils.isEmpty(deliverFee)){
                deliverFeeEt.setError("Please enter the delivery fee");
            }
        }

    }

    private void createAccount() {
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();
        saveFirebaseData();
    }

    private void saveFirebaseData() {
        progressDialog.setMessage("Saving Account Info...");

        String timestamp = ""+System.currentTimeMillis();

        if (image_uri==null){

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid",""+firebaseAuth.getUid());
            hashMap.put("email",""+email);
            hashMap.put("name",""+fullName);
            hashMap.put("phone",""+phoneNumber);
            hashMap.put("timestamp",""+timestamp);
            hashMap.put("accountType","User");
            hashMap.put("online","true");
            hashMap.put("profileImage","");

            String fireBaseToken = FirebaseInstanceId.getInstance().getToken();

            userModel = new UserModel(
              firebaseAuth.getUid(),deliverFee,email,"","",fullName,phoneNumber,"",shopName,timestamp,true,
              true, fireBaseToken
            );

            viewModel.signUp(email,password);

            viewModel.getSignUpMutableLiveData().observe(this, new Observer<Task<AuthResult>>() {
                @Override
                public void onChanged(Task<AuthResult> authResultTask) {


                    if (authResultTask.isSuccessful()){
                        Log.e("REG Activity", "onChanged: IF");

                        viewModel.setUserData(userModel);

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


                        });

                    }

                    else {
                        progressDialog.dismiss();
                        Log.e("REG Activity"
                                , "onChanged: ssss" + authResultTask.getException().getLocalizedMessage() );
                    }

                }
            });


//            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
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
//                    });
        }
        else {

            String fireBaseToken = FirebaseInstanceId.getInstance().getToken();

            userModel = new UserModel(
                    firebaseAuth.getUid(),deliverFee,email,"",
                    "",fullName,phoneNumber,image_uri.getPath(),shopName,timestamp,true,
                    true, fireBaseToken
            );


            viewModel.signUp(email,password);

            viewModel.getSignUpMutableLiveData().observe(this, new Observer<Task<AuthResult>>() {
                @Override
                public void onChanged(Task<AuthResult> authResultTask) {


                    if (authResultTask.isSuccessful()){
                        Log.e("REG Activity", "onChanged: IF");

//                        viewModel.(userModel);
//
//                        viewModel.getUserInfoMutableLiveData().observe(RegisterUserActivity.this
//                                , new Observer<Task<Void>>() {
//                                    @Override
//                                    public void onChanged(Task<Void> voidTask) {
//                                        if (voidTask.isSuccessful())
//                                        {
//                                            Intent intent = new Intent(RegisterUserActivity.this, MainUserActivity.class);
//                                            progressDialog.dismiss();
//                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//                                            userInfo.setData(userModel);
//
//                                            startActivity(intent);
//                                        }
//                                        else {
//                                            Log.e("REG Activity", "onChanged: error");
//
//                                            Log.e("REG Activity", "onChanged: else"
//                                                    + voidTask.getException().getLocalizedMessage() );
//
//                                            progressDialog.dismiss();
//                                            Toast.makeText(RegisterUserActivity.this, voidTask.getResult().toString()
//                                                    , Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//
//
//                                });

                        String filePathAndName = "profile_images/" + ""+firebaseAuth.getUid();

                        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
                        storageReference.putFile(Uri.fromFile(new File(image_uri.getPath())))
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while (!uriTask.isSuccessful());
                                        Uri downloadImageUri = uriTask.getResult();

                                        if (uriTask.isSuccessful()){

                                            userModel.setProfileImage(downloadImageUri.toString());
                                            viewModel.sendData(userModel);
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(RegisterUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                    }

                    else {
                        progressDialog.dismiss();
                        Log.e("REG Activity"
                                , "onChanged: ssss" + authResultTask.getException().getLocalizedMessage() );
                    }

                }
            });
        }

    }


    private void showImagePickDialog() {

        String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){

                            if (checkCameraPermission()){

                                pickFromCamera();
                            }
                            else {

                                requestCameraPermissions();
                            }
                        }
                        else {

                            if (checkStoragePermission()){
                                pickFromGallery();
                            }
                            else {
                                requestStoragePermissions();
                            }
                        }
                    }
                })
                .show();
    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

//        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermissions(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermissions(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        pickFromCamera();
                    }
                    else {

                        Toast.makeText(this, "Camera Permission are necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        pickFromGallery();
                    }
                    else {

                        Toast.makeText(this, "Storage Permission is necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode==RESULT_OK){

            if (requestCode == IMAGE_PICK_GALLERY_CODE){

                image_uri = data.getData();

                profileIv.setImageURI(image_uri);

            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE){

                profileIv.setImageURI(image_uri);

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
