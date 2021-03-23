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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Constants;
import com.example.myapplication.R;
import com.example.myapplication.models.ModelProduct;
import com.example.myapplication.models.OrdersModel;
import com.example.myapplication.util.MyUtil;
import com.example.myapplication.util.UserInfo;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AddProductActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private ImageView productIconIv;
    private EditText titleEt,descriptionEt, timeEt,priceEt;
    private TextView categoryTv,toolbarHeader;
    private Button addProductBtn;

    AddProductViewModel viewModel;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private String[] cameraPermissions;
    ModelProduct product;
    private String[] storagePermissions;

    private String image_uri;
    private Uri uri;


    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        backBtn = findViewById(R.id.backBtn);
        productIconIv = findViewById(R.id.productIconIv);
        titleEt = findViewById(R.id.titleEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        timeEt = findViewById(R.id.timeEt);
        priceEt = findViewById(R.id.priceEt);
        categoryTv = findViewById(R.id.categoryTv);
        addProductBtn = findViewById(R.id.addProductBtn);
        toolbarHeader = findViewById(R.id.toolbar_header);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        viewModel = new ViewModelProvider(this).get(AddProductViewModel.class);

        String navigation = getIntent().getExtras().getString("navigation");


        if (navigation.equals("order"))
        {
            toolbarHeader.setText(R.string.add_order);
            addProductBtn.setText(R.string.add_order);
            timeEt.setVisibility(VISIBLE);

            titleEt.setHint(R.string.project_title);
            categoryTv.setText(R.string.required_jobs);

        }
        else {
            toolbarHeader.setText(R.string.add_product);
            addProductBtn.setText(R.string.add_product);
            timeEt.setVisibility(GONE);
            titleEt.setHint(R.string.category_name);
            categoryTv.setText(R.string.category_type);

        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        productIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showImagePickDialog();

            }
        });

        categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (navigation.equals("order"))
                {

                    if (image_uri == null) {
                        progressDialog.setMessage("Adding Order...");
                        progressDialog.show();
                        final String timestamp = ""+System.currentTimeMillis();
                        String userId = new  UserInfo(AddProductActivity.this).getuserId();
                        String userName = new  UserInfo(AddProductActivity.this).getFullName();
                        String userImage = new UserInfo(AddProductActivity.this).getImage();

                        productTitle = titleEt.getText().toString().trim();
                        productDescription = descriptionEt.getText().toString().trim();
                        productCategory = categoryTv.getText().toString().trim();
                        productQuantity = timeEt.getText().toString().trim();
                        originalPrice = priceEt.getText().toString().trim();


                        OrdersModel ordersModel = new OrdersModel(
                                productTitle,productDescription,productCategory
                                ,productQuantity, "",originalPrice,timestamp,userId,userName, userImage
                        );

                        viewModel.addOrder(ordersModel);
                        viewModel.getAddOrderLiveData().observe(AddProductActivity.this, new Observer<Task<Void>>() {
                            @Override
                            public void onChanged(Task<Void> voidTask) {
                                if (voidTask.isSuccessful())
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddProductActivity.this, "Order Added", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent();
                                    intent.putExtra("ADD_PRODUCT",673);
                                    AddProductActivity.this.setResult(RESULT_OK,intent);
                                    AddProductActivity.this.finish();
                                }

                            }
                        });
                    }
                    else {
                        progressDialog.setMessage("Adding Order...");
                        progressDialog.show();
                        final String timestamp = ""+System.currentTimeMillis();
                        String userId = new  UserInfo(AddProductActivity.this).getuserId();
                        String userName = new  UserInfo(AddProductActivity.this).getFullName();
                        String userImage = new UserInfo(AddProductActivity.this).getImage();

                        productTitle = titleEt.getText().toString().trim();
                        productDescription = descriptionEt.getText().toString().trim();
                        productCategory = categoryTv.getText().toString().trim();
                        productQuantity = timeEt.getText().toString().trim();
                        originalPrice = priceEt.getText().toString().trim();


                        OrdersModel ordersModel = new OrdersModel(
                                productTitle,productDescription,productCategory
                                ,productQuantity, image_uri,originalPrice,timestamp,userId,userName,userImage
                        );

                        viewModel.addOrder(ordersModel);
                        viewModel.getAddOrderLiveData().observe(AddProductActivity.this, new Observer<Task<Void>>() {
                            @Override
                            public void onChanged(Task<Void> voidTask) {
                                if (voidTask.isSuccessful())
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddProductActivity.this, "Order Added", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent();
                                    intent.putExtra("ADD_PRODUCT",673);
                                    AddProductActivity.this.setResult(RESULT_OK,intent);
                                    AddProductActivity.this.finish();
                                }

                                else
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddProductActivity.this, "Try again !", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }

                }
                else {

                    inputData();
                }


            }
        });

    }

    private String productTitle, productDescription, productCategory, productQuantity, originalPrice;
    private void inputData() {

        productTitle = titleEt.getText().toString().trim();
        productDescription = descriptionEt.getText().toString().trim();
        productCategory = categoryTv.getText().toString().trim();
        productQuantity = timeEt.getText().toString().trim();
        originalPrice = priceEt.getText().toString().trim();

        if (TextUtils.isEmpty(productTitle)){
            Toast.makeText(this, "Title is required...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(productCategory)){
            Toast.makeText(this, "Category is required...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(originalPrice)){
            Toast.makeText(this, "Price is required...", Toast.LENGTH_SHORT).show();
            return;
        }

        addProduct();
    }

    private void addProduct() {

        progressDialog.setMessage("Adding Product...");
        progressDialog.show();

       final String timestamp = ""+System.currentTimeMillis();

       String productId = firebaseAuth.getUid();

        if (image_uri == null){


            String userId = new  UserInfo(this).getuserId();
            String userName = new UserInfo(this).getFullName();
            String userImage = new UserInfo(AddProductActivity.this).getImage();

            product = new  ModelProduct(
                    productTitle,productDescription,productCategory
                    ,productQuantity,"",originalPrice,timestamp,userId,userName,userImage
            );

            viewModel.setProductData(product);

            viewModel.getAddProductLiveData().observe(this, new Observer<Task<Void>>() {
                @Override
                public void onChanged(Task<Void> task) {

                    if (task.isSuccessful())
                    {
                        Toast.makeText(AddProductActivity.this, "Product Added", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.putExtra("ADD_PRODUCT",673);
                        AddProductActivity.this.setResult(RESULT_OK,intent);
                        AddProductActivity.this.finish();
                    }
                    else
                        Toast.makeText(AddProductActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();


                }
            });


        }
        else {

            String userId = new  UserInfo(this).getuserId();
            String userName = new UserInfo(this).getFullName();
            String userImage = new UserInfo(AddProductActivity.this).getImage();


            product = new  ModelProduct(
                    productTitle,productDescription,productCategory
                    ,productQuantity, image_uri,originalPrice,timestamp,userId,userName,userImage
            );


            viewModel.setProductData(product);

            viewModel.getAddProductLiveData().observe(this, new Observer<Task<Void>>() {
                @Override
                public void onChanged(Task<Void> task) {

                    if (task.isSuccessful())
                    {
                        Toast.makeText(AddProductActivity.this, "Product Added", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.putExtra("ADD_PRODUCT",673);
                        AddProductActivity.this.setResult(RESULT_OK,intent);
                        AddProductActivity.this.finish();
                    }
                    else
                        Toast.makeText(AddProductActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();


                }
            });


        }
    }

    private void clearData(){

        titleEt.setText("");
        descriptionEt.setText("");
        categoryTv.setText("");
        timeEt.setText("");
        priceEt.setText("");
        productIconIv.setImageResource(R.drawable.ic_add_shopping_primary);
        image_uri = null;

    }

    private void categoryDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Category")
                .setItems(Constants.productCategories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String category = Constants.productCategories[which];

                        categoryTv.setText(category);
                    }
                })
                .show();
    }

    private void showImagePickDialog() {

        String[] options = {"Camera", "Gallery"};

        if (checkStoragePermission()) {
            pickFromGallery();
        } else {
            requestStoragePermissions();
        }

//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Pick Image")
//                .setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (which == 0){
//
//                            if (checkCameraPermission()){
//
//                                pickFromCamera();
//                            }
//                            else {
//
//                                requestCameraPermissions();
//                            }
//                        }
//                        else {
//
//                            if (checkStoragePermission()){
//                                pickFromGallery();
//                            }
//                            else {
//                                requestStoragePermissions();
//                            }
//                        }
//                    }
//                })
//                .show();
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

        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

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

                        Toast.makeText(this, "Camera & Storage Permission are required...", Toast.LENGTH_SHORT).show();
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

                try{

                    productIconIv.setImageURI(data.getData());
                    uri = data.getData();
                    String p = "";

                    Cursor cursor = getContentResolver().query(
                            uri,null,null,null,null
                    );

                    if(cursor == null) {
                        p = uri.getPath().toString();
                    } else {
                        cursor.moveToFirst();
                        int indx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        p = cursor.getString(indx);
                    }

                    image_uri = p;
                }
                catch (Exception e){

                    productIconIv.setImageBitmap((Bitmap) data.getExtras().get("data"));

                    image_uri = MyUtil.Companion.bitMapToString((Bitmap) data.getExtras().get("data"));
                }


            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE){

//                productIconIv.setImageURI(image_uri);

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}