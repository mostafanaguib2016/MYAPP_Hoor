package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activity.chat.MessagesActivity;
import com.example.myapplication.activity.my_ads.MyAdsActivity;
import com.example.myapplication.adapters.AdapterOrder;
import com.example.myapplication.adapters.AdapterProductSeller;
import com.example.myapplication.adapters.AdapterProductUser;
import com.example.myapplication.adapters.AdapterShop;
import com.example.myapplication.models.ModelProduct;
import com.example.myapplication.models.ModelShop;
import com.example.myapplication.models.OrdersModel;
import com.example.myapplication.util.MyUtil;
import com.example.myapplication.util.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MainUserActivity extends AppCompatActivity {

    private TextView nameTv, emailTv, phoneTv, tabShopsTv, tabOrdersTv, myAdsTv;
    private RelativeLayout shopsRl, ordersRl;
    private ImageButton logoutBtn, editProfileBtn, addProductBtn, messageBtn;
    private ImageView profileIv;
    private RecyclerView shopRv, orderRv;

    AdapterOrder adapterOrder;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private String[] cameraPermissions;
    ModelProduct product;
    private String[] storagePermissions;

    UserInfo userInfo;

    AddProductViewModel viewModel;
    RegisterViewModel registerViewModel;

    private static final int ADD_PRODUCT = 673;


    private String image_uri;
    private Uri uri;

    private ArrayList<ModelProduct> shopList;
    private ArrayList<OrdersModel> orderList;
    private AdapterProductUser adapterShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        userInfo = new UserInfo(this);

        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        shopsRl = findViewById(R.id.shopsRl);
        ordersRl = findViewById(R.id.orderRl);

        logoutBtn = findViewById(R.id.logoutBtn);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        profileIv = findViewById(R.id.profileIv);
        phoneTv = findViewById(R.id.phoneTv);
        tabShopsTv = findViewById(R.id.tabShopsTv);
        tabOrdersTv = findViewById(R.id.tabOrdersTv);
        shopRv = findViewById(R.id.shopRv);
        orderRv = findViewById(R.id.orderRv);
        addProductBtn = findViewById(R.id.addProductBtn);
        messageBtn = findViewById(R.id.messageBtn);
        myAdsTv = findViewById(R.id.myAdsTv);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        myAdsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(
                        new Intent(MainUserActivity.this, MyAdsActivity.class)
                );
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        viewModel = new ViewModelProvider(this).get(AddProductViewModel.class);
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        showShopsUI();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userInfo.logOut();
                MakeMeOffline();
            }
        });

        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changePhotoDialog();

            }
        });

        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainUserActivity.this, MessagesActivity.class));
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainUserActivity.this, ProfileEditUserActivity.class));

            }
        });

        tabShopsTv.setText("Products");

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainUserActivity.this, AddProductActivity.class);

                intent.putExtra("navigation", "product");

                startActivityForResult
                        (intent, ADD_PRODUCT);
            }
        });

        tabShopsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addProductBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(MainUserActivity.this, AddProductActivity.class);

                        intent.putExtra("navigation", "product");

                        startActivityForResult
                                (intent, ADD_PRODUCT);
                    }
                });

                showShopsUI();
            }
        });
        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addProductBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainUserActivity.this, AddProductActivity.class);

                        intent.putExtra("navigation", "order");

                        startActivityForResult
                                (intent, ADD_PRODUCT);
                    }
                });

                showOrdersUI();
            }
        });

    }

    private void showShopsUI() {
        shopsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabShopsTv.setTextColor(getResources().getColor(R.color.black));
        tabShopsTv.setBackgroundResource(R.drawable.shape_rect04);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.white));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        getOrders();

    }

    private void showOrdersUI() {
        shopsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tabShopsTv.setTextColor(getResources().getColor(R.color.white));
        tabShopsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabOrdersTv.setTextColor(getResources().getColor(R.color.black));
        tabOrdersTv.setBackgroundResource(R.drawable.shape_rect04);

    }


    private void changePhotoDialog(){

        String[] options = {"Change photo", "Delete photo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Or Delete ?")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            showImagePickDialog();
                        } else {
                            registerViewModel.updatePhoto("",userInfo.getuserId(),userInfo);
                            registerViewModel.getUpdateUserLiveData().observe(MainUserActivity.this, new Observer<Task<Void>>() {
                                @Override
                                public void onChanged(Task<Void> voidTask) {

                                    if (voidTask.isSuccessful())
                                        profileIv.setImageResource(R.drawable.ic_person_gray);

                                }
                            });
                        }
                    }
                })
                .show();

    }



    private void showImagePickDialog() {
        if (checkStoragePermission()) {
            pickFromGallery();
        } else {
            requestStoragePermissions();
        }
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, 500);

    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermissions() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermissions() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    } else {

                        Toast.makeText(this, "Camera & Storage Permission are required...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickFromGallery();
                    } else {

                        Toast.makeText(this, "Storage Permission is necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void MakeMeOffline() {
        progressDialog.setMessage("Logging Out...");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "false");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressDialog.dismiss();
                        Toast.makeText(MainUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
            finish();
        } else {
            loadMyInfo();
        }
    }

    void getOrders() {

        orderList = new ArrayList<>();

        String id = new UserInfo(this).getuserId();

        CollectionReference reference = FirebaseFirestore.getInstance().collection("orders");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                    OrdersModel product = snapshot.toObject(OrdersModel.class);

                    orderList.add(product);
                }
                adapterOrder = new AdapterOrder(MainUserActivity.this, orderList, viewModel);
                orderRv.setAdapter(adapterOrder);
            }
        });

    }

    private void loadMyInfo() {

        nameTv.setText(userInfo.getFullName());
        emailTv.setText(userInfo.getEmail());
        phoneTv.setText(userInfo.getPhone());
        try {
            Picasso.get().load(userInfo.getImage()).placeholder(R.drawable.ic_person_gray).into(profileIv);
        } catch (Exception e) {

            profileIv.setImageResource(R.drawable.ic_person_gray);
        }

        loadShops();
    }

    private void loadShops() {

        shopList = new ArrayList<>();

        CollectionReference reference = FirebaseFirestore.getInstance().collection("products");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                    ModelProduct product = snapshot.toObject(ModelProduct.class);

                    shopList.add(product);
                }
                adapterShop = new AdapterProductUser(MainUserActivity.this, shopList, viewModel);
                adapterShop.setData(shopList);
                shopRv.setAdapter(adapterShop);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == ADD_PRODUCT) {
                loadShops();
                getOrders();
            }

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {

                try {

                    profileIv.setImageURI(data.getData());
                    uri = data.getData();
                    String p = "";

                    Cursor cursor = getContentResolver().query(
                            uri, null, null, null, null
                    );

                    if (cursor == null) {
                        p = uri.getPath().toString();
                    } else {
                        cursor.moveToFirst();
                        int indx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        p = cursor.getString(indx);
                    }

                    image_uri = p;

                    registerViewModel.updatePhoto(image_uri,userInfo.getuserId(),userInfo);


                } catch (Exception e) {

                    profileIv.setImageBitmap((Bitmap) data.getExtras().get("data"));

                    image_uri = MyUtil.Companion.bitMapToString((Bitmap) data.getExtras().get("data"));
                }


            }

        }
    }
}