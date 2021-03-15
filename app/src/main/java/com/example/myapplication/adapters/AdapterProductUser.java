package com.example.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.FilterProductUsers;
import com.example.myapplication.R;
import com.example.myapplication.activity.AddProductViewModel;
import com.example.myapplication.activity.ProductDetailsActivity;
import com.example.myapplication.models.ModelProduct;
import com.example.myapplication.models.OrdersModel;
import com.example.myapplication.util.UserInfo;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productsList, filterList;
    private FilterProductUsers filter;
    private AddProductViewModel viewModel;

    public AdapterProductUser(Context context, ArrayList<ModelProduct> productList,AddProductViewModel viewModel){
        this.context = context;
        this.productsList = productList;
        this.filterList = productList;
        this.viewModel = viewModel;

    }

    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_product_user, parent, false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {


        ModelProduct modelProduct = productsList.get(position);

        String productCategory = modelProduct.getProductCategory();
        String originalPrice = modelProduct.getOriginalPrice();
        String productDescription = modelProduct.getProductDescription();
        String productTitle = modelProduct.getProductTitle();
        String productQuantity = modelProduct.getProductQuantity();
        String productId = modelProduct.getProductId();
        String timestamp = modelProduct.getTimestamp();
        String productIcon = modelProduct.getProductIcon();

        holder.titleTv.setText(productTitle);
        holder.descriptionTv.setText(productDescription);
        holder.originalPriceTv.setText("$"+originalPrice);


        try {

            Picasso.get().load(productIcon).placeholder(R.drawable.ic_add_shopping_primary).into(holder.productIconIv);
        }
        catch (Exception e){

            holder.productIconIv.setImageResource(R.drawable.ic_add_shopping_primary);

        }

        holder.addToCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserInfo info = new UserInfo(context);

                OrdersModel model = new OrdersModel(
                        info.getDeliveryFee(),modelProduct.getUserId(),info.getuserId(),""
                        ,"",info.getFullName(),info.getPhone(),info.getShopName()
                        ,timestamp,originalPrice,productId,productTitle
                );

                viewModel.addOrder(model);
                viewModel.getAddOrderLiveData().observe((LifecycleOwner) context, new Observer<Task<Void>>() {
                    @Override
                    public void onChanged(Task<Void> voidTask) {
                        if (voidTask.isSuccessful())
                            Toast.makeText(context, "Order Added", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("productId",productId);
                context.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterProductUsers(this, filterList);
        }
        return filter;
    }

    class HolderProductUser extends RecyclerView.ViewHolder{

        private ImageView productIconIv;
        private TextView titleTv,descriptionTv,addToCartTv,originalPriceTv;

        public HolderProductUser(@NonNull View itemView) {
            super(itemView);

            productIconIv = itemView.findViewById(R.id.productIconIv);
            titleTv = itemView.findViewById(R.id.titleTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            addToCartTv = itemView.findViewById(R.id.addToCartTv);
            originalPriceTv = itemView.findViewById(R.id.originalPriceTv);
        }
    }

}
