package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditProductActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private ImageView productIconIv;
    private EditText titleEt,descriptionEt,quantityEt,priceEt;
    private TextView categoryTv;
    private Button updateProductBtn;
    private OrdersModel order ;
    private ModelProduct product;

    private String productId;
    private String navigation;


    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    private Uri image_uri;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        backBtn = findViewById(R.id.backBtn);
        productIconIv = findViewById(R.id.productIconIv);
        titleEt = findViewById(R.id.titleEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        quantityEt = findViewById(R.id.quantityEt);
        priceEt = findViewById(R.id.priceEt);
        categoryTv = findViewById(R.id.categoryTv);
        updateProductBtn = findViewById(R.id.updateProductBtn);

        navigation = getIntent().getStringExtra("navigation");
        productId = getIntent().getStringExtra("productId");

        order = new OrdersModel();
        product = new ModelProduct();

        firebaseAuth = FirebaseAuth.getInstance();
        loadProductDetails();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

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

        updateProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputData();
            }
        });

    }

    private void loadProductDetails() {

        if (navigation.equals("order")){

            String productIcon = "";

            CollectionReference reference = FirebaseFirestore.getInstance().collection("orders");

            reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    for (DocumentSnapshot snapshot : task.getResult().getDocuments()){
                        order = snapshot.toObject(OrdersModel.class);
                        if (order.getOrderId().equals(productId)){
                            titleEt.setText(order.getOrderTitle());
                            descriptionEt.setText(order.getOrderDescription());
                            categoryTv.setText(order.getOrderCategory());
                            quantityEt.setText(order.getOrderQuantity());
                            priceEt.setText(order.getOriginalPrice());
                            break;
                        }

                    }

                }
            });



            try {

                Picasso.get().load(productIcon).placeholder(R.drawable.ic_add_shopping_white).into(productIconIv);
            }
            catch (Exception e){
                productIconIv.setImageResource(R.drawable.ic_add_shopping_white);
            }

        }

        else {
            String productIcon = "";

            CollectionReference reference = FirebaseFirestore.getInstance().collection("products");

            reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    for (DocumentSnapshot snapshot : task.getResult().getDocuments()){
                        product = snapshot.toObject(ModelProduct.class);
                        if (order.getOrderId().equals(productId)){
                            titleEt.setText(product.getProductTitle());
                            descriptionEt.setText(product.getProductDescription());
                            categoryTv.setText(product.getProductCategory());
                            quantityEt.setText(product.getProductQuantity());
                            priceEt.setText(product.getOriginalPrice());
                            break;
                        }

                    }

                }
            });



            try {

                Picasso.get().load(productIcon).placeholder(R.drawable.ic_add_shopping_white).into(productIconIv);
            }
            catch (Exception e){
                productIconIv.setImageResource(R.drawable.ic_add_shopping_white);
            }

        }

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//        reference.child(firebaseAuth.getUid()).child("Products").child(productId)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                        String productId = ""+snapshot.child("productId").getValue();
//                        String productTitle = ""+snapshot.child("productTitle").getValue();
//                        String productDescription = ""+snapshot.child("productDescription").getValue();
//                        String productCategory = ""+snapshot.child("productCategory").getValue();
//                        String productQuantity = ""+snapshot.child("productQuantity").getValue();
//                        String productIcon = ""+snapshot.child("productIcon").getValue();
//                        String originalPrice = ""+snapshot.child("originalPrice").getValue();
//                        String timestamp = ""+snapshot.child("timestamp").getValue();
//                        String uid = ""+snapshot.child("uid").getValue();
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
    }


    private String productTitle, productDescription, productCategory, productQuantity, originalPrice;
    private void inputData() {

        productTitle = titleEt.getText().toString().trim();
        productDescription = descriptionEt.getText().toString().trim();
        productCategory = categoryTv.getText().toString().trim();
        productQuantity = quantityEt.getText().toString().trim();
        originalPrice = priceEt.getText().toString().trim();

        if (TextUtils.isEmpty(productTitle)){
            Toast.makeText(this, "Title is required...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(productCategory)){
            Toast.makeText(this, "Category is required...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(productDescription)){
            Toast.makeText(this, "Description is required...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(originalPrice)){
            Toast.makeText(this, "Price is required...", Toast.LENGTH_SHORT).show();
            return;
        }

        updateProduct(productId);
    }

    private void updateProduct(String productId) {

        progressDialog.setMessage("Updating product...");
        progressDialog.show();

        if (image_uri == null){


            if (navigation.equals("product")){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("productTitle", ""+productTitle);
                hashMap.put("productDescription", ""+productDescription);
                hashMap.put("productCategory", ""+productCategory);
                hashMap.put("productQuantity", ""+productQuantity);
                hashMap.put("originalPrice", ""+originalPrice);

                CollectionReference ref = FirebaseFirestore.getInstance().collection("products");

                ref.document(productId).update(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            Toast.makeText(EditProductActivity.this, "Product Edited Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("orderTitle", ""+productTitle);
                hashMap.put("orderDescription", ""+productDescription);
                hashMap.put("orderCategory", ""+productCategory);
                hashMap.put("orderQuantity", ""+productQuantity);
                hashMap.put("originalPrice", ""+originalPrice);

                CollectionReference ref = FirebaseFirestore.getInstance().collection("orders");

                ref.document(productId).update(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            Toast.makeText(EditProductActivity.this, "Product Edited Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

//            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//            reference.child(firebaseAuth.getUid()).child("Products").child(productId)
//                    .updateChildren(hashMap)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//
//                            progressDialog.dismiss();
//                            Toast.makeText(EditProductActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                            progressDialog.dismiss();
//                            Toast.makeText(EditProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
        }
        else {
            String filePathAndName = "product_image/" + "" + productId;

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("productTitle", ""+productTitle);
                                hashMap.put("productDescription", ""+productDescription);
                                hashMap.put("productCategory", ""+productCategory);
                                hashMap.put("productIcon", ""+downloadImageUri);
                                hashMap.put("productQuantity", ""+productQuantity);
                                hashMap.put("originalPrice", ""+originalPrice);

                                CollectionReference ref = FirebaseFirestore.getInstance().collection("products");

                                ref.document(productId).update(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                            Toast.makeText(EditProductActivity.this, "Product Edited Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });

//                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//                                reference.child(firebaseAuth.getUid()).child("Products").child(productId)
//                                        .updateChildren(hashMap)
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//
//                                                progressDialog.dismiss();
//                                                Toast.makeText(EditProductActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
//                                            }
//                                        })
//                                        .addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//
//                                                progressDialog.dismiss();
//                                                Toast.makeText(EditProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
                            }
                            else
                            {

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("productTitle", ""+productTitle);
                                hashMap.put("productDescription", ""+productDescription);
                                hashMap.put("productCategory", ""+productCategory);
                                hashMap.put("productIcon", ""+downloadImageUri);
                                hashMap.put("productQuantity", ""+productQuantity);
                                hashMap.put("originalPrice", ""+originalPrice);

                                CollectionReference ref = FirebaseFirestore.getInstance().collection("products");

                                ref.document(productId).update(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                            Toast.makeText(EditProductActivity.this, "Product Edited Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            Toast.makeText(EditProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

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

                image_uri = data.getData();

                productIconIv.setImageURI(image_uri);

            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE){

                productIconIv.setImageURI(image_uri);

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}