package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activity.chat.MessagesActivity;
import com.example.myapplication.adapters.AdapterOrder;
import com.example.myapplication.adapters.AdapterProductSeller;
import com.example.myapplication.adapters.AdapterProductUser;
import com.example.myapplication.adapters.AdapterShop;
import com.example.myapplication.models.ModelProduct;
import com.example.myapplication.models.ModelShop;
import com.example.myapplication.models.OrdersModel;
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

    private TextView nameTv, emailTv, phoneTv, tabShopsTv,tabOrdersTv;
    private RelativeLayout shopsRl, ordersRl;
    private ImageButton logoutBtn,editProfileBtn,addProductBtn,messageBtn;
    private ImageView profileIv;
    private RecyclerView shopRv,orderRv;

    AdapterOrder adapterOrder;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    UserInfo userInfo;

    AddProductViewModel viewModel;

    private static final int ADD_PRODUCT = 673;

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

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        viewModel = new ViewModelProvider(this).get(AddProductViewModel.class);

        showShopsUI();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userInfo.logOut();
                MakeMeOffline();
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

        tabShopsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addProductBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(MainUserActivity.this,AddProductActivity.class);

                        intent.putExtra("navigation","product");

                        startActivityForResult
                                (intent,ADD_PRODUCT);
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
                        Intent intent = new Intent(MainUserActivity.this,AddProductActivity.class);

                        intent.putExtra("navigation","order");

                        startActivityForResult
                                (intent,ADD_PRODUCT);
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


    private void MakeMeOffline() {
        progressDialog.setMessage("Logging Out...");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","false");

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
                        Toast.makeText(MainUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
            finish();
        }
        else {
            loadMyInfo();
        }
    }

    void getOrders(){

        orderList = new ArrayList<>();

        String id = new UserInfo(this).getuserId();

        CollectionReference reference = FirebaseFirestore.getInstance().collection("orders");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot snapshot : task.getResult().getDocuments()){
                    OrdersModel product = snapshot.toObject(OrdersModel.class);

                    orderList.add(product);
                }
                adapterOrder = new AdapterOrder(MainUserActivity.this, orderList,viewModel);
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
        }
        catch (Exception e){

            profileIv.setImageResource(R.drawable.ic_person_gray);
        }

        loadShops("");

        /*DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();
                            String email = ""+ds.child("email").getValue();
                            String phone = ""+ds.child("phone").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();
                            String accountType = ""+ds.child("accountType").getValue();
                            String city = ""+ds.child("city").getValue();



                            nameTv.setText(name);
                            emailTv.setText(email);
                            phoneTv.setText(phone);
                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(profileIv);
                            }
                            catch (Exception e){

                                profileIv.setImageResource(R.drawable.ic_person_gray);
                            }

                            loadShops(city);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/
    }

    private void loadShops(final String myCity) {

        shopList = new ArrayList<>();

        CollectionReference reference = FirebaseFirestore.getInstance().collection("products");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot snapshot : task.getResult().getDocuments()){
                    ModelProduct product = snapshot.toObject(ModelProduct.class);

                    shopList.add(product);
                }
                adapterShop = new AdapterProductUser(MainUserActivity.this, shopList,viewModel);
                adapterShop.setData(shopList);
                shopRv.setAdapter(adapterShop);
            }
        });

//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.orderByChild("accountType").equalTo("Seller")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                        shopList.clear();
//                        for (DataSnapshot ds: snapshot.getChildren()){
//                            ModelShop modelShop = ds.getValue(ModelShop.class);
//
//                            String shopCity = ""+ds.child("city").getValue();
//
//                            if (shopCity.equals(myCity)){
//                                shopList.add(modelShop);
//                            }
//
//                        }
//
//                        adapterShop = new AdapterShop(MainUserActivity.this, shopList);
//                        shopRv.setAdapter(adapterShop);
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK){

            if (requestCode == ADD_PRODUCT){
                loadShops("");
            }
        }

    }
}