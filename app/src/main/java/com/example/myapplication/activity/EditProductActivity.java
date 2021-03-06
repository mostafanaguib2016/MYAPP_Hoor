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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class EditProductActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private ImageView productIconIv;
    private EditText titleEt,descriptionEt, timeEt,priceEt;
    private TextView categoryTv,toolbarHeader;
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

    private String image_uri;
    private Uri uri;

    AddProductViewModel viewModel;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        backBtn = findViewById(R.id.backBtn);
        toolbarHeader = findViewById(R.id.toolbar_header);
        productIconIv = findViewById(R.id.productIconIv);
        titleEt = findViewById(R.id.titleEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        timeEt = findViewById(R.id.timeEt);
        priceEt = findViewById(R.id.priceEt);
        categoryTv = findViewById(R.id.categoryTv);
        updateProductBtn = findViewById(R.id.updateProductBtn);

        navigation = getIntent().getStringExtra("navigation");
        productId = getIntent().getStringExtra("productId");

        order = new OrdersModel();
        product = new ModelProduct();

        viewModel = new ViewModelProvider(this).get(AddProductViewModel.class);

        firebaseAuth = FirebaseAuth.getInstance();
        loadProductDetails();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        if (navigation.equals("order"))
        {
            toolbarHeader.setText(R.string.edit_order);
            updateProductBtn.setText(R.string.update_order);
            timeEt.setVisibility(VISIBLE);
            titleEt.setHint(R.string.project_title);
            categoryTv.setText(R.string.required_jobs);

        }
        else {
            toolbarHeader.setText(R.string.edit_product);
            updateProductBtn.setText(R.string.update_product);
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

        updateProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputData();
            }
        });

    }

    private void loadProductDetails() {

        if (navigation.equals("order")){

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
                            timeEt.setText(order.getOrderQuantity());
                            priceEt.setText(order.getOriginalPrice());

                            try {
                                String orderIcon = order.getOrderIconUrl();
                                Picasso.get().load(orderIcon).placeholder(R.drawable.ic_add_shopping_primary).into(productIconIv);
                            } catch (Exception e) {
                                productIconIv.setImageResource(R.drawable.ic_add_shopping_primary);
                            }

                            break;
                        }

                    }

                }
            });


        }

        else {

            CollectionReference reference = FirebaseFirestore.getInstance().collection("products");

            reference.get().addOnCompleteListener(task -> {

                for (DocumentSnapshot snapshot : task.getResult().getDocuments()){
                    product = snapshot.toObject(ModelProduct.class);
                    if (product.getProductId().equals(productId)){
                        titleEt.setText(product.getProductTitle());
                        descriptionEt.setText(product.getProductDescription());
                        categoryTv.setText(product.getProductCategory());
                        timeEt.setText(product.getProductQuantity());
                        priceEt.setText(product.getOriginalPrice());

                        try {
                            String orderIcon = product.getProductIconUrl();
                            Picasso.get().load(orderIcon).placeholder(R.drawable.ic_add_shopping_primary).into(productIconIv);
                        } catch (Exception e) {
                            productIconIv.setImageResource(R.drawable.ic_add_shopping_primary);
                        }

                        break;
                    }

                }

            });
        }

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

                product = new  ModelProduct(
                        productTitle,productDescription,productCategory
                        ,productQuantity,"",originalPrice,"","","",""
                );

                viewModel.updateProduct(productId,product);

                viewModel.getUpdateProductLiveData().observe(this, new Observer<Task<Void>>() {
                    @Override
                    public void onChanged(Task<Void> voidTask) {

                        if (voidTask.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(EditProductActivity.this, "Product Edited Successfully", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
            else {


                OrdersModel ordersModel = new OrdersModel(
                        productTitle,productDescription,productCategory
                        ,productQuantity, "",originalPrice,"","","",""
                );

                viewModel.updateOrder(productId,ordersModel);

                viewModel.getUpdateOrderLiveData().observe(this, new Observer<Task<Void>>() {
                    @Override
                    public void onChanged(Task<Void> voidTask) {

                        if (voidTask.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(EditProductActivity.this, "Product Edited Successfully", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }

        }
        else {

            if (navigation.equals("product")){

                product = new  ModelProduct(
                        productTitle,productDescription,productCategory
                        ,productQuantity,image_uri,originalPrice,"","","",""
                );

                viewModel.updateProduct(productId,product);

                viewModel.getUpdateProductLiveData().observe(this, new Observer<Task<Void>>() {
                    @Override
                    public void onChanged(Task<Void> voidTask) {

                        if (voidTask.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(EditProductActivity.this, "Product Edited Successfully", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
            else {


                OrdersModel ordersModel = new OrdersModel(
                        productTitle,productDescription,productCategory
                        ,productQuantity, image_uri,originalPrice,"","","",""
                );

                viewModel.updateOrder(productId,ordersModel);

                viewModel.getUpdateOrderLiveData().observe(this, new Observer<Task<Void>>() {
                    @Override
                    public void onChanged(Task<Void> voidTask) {

                        if (voidTask.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(EditProductActivity.this, "Product Edited Successfully", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }

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

        if (checkStoragePermission()){
            pickFromGallery();
        }
        else {
            requestStoragePermissions();
        }

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

                productIconIv.setImageURI(data.getData());

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}