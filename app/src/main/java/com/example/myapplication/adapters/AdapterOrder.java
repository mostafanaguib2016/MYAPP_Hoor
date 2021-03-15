package com.example.myapplication.adapters;

import android.content.Context;
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
import com.example.myapplication.models.ModelProduct;
import com.example.myapplication.models.OrdersModel;
import com.example.myapplication.util.UserInfo;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterOrder extends RecyclerView.Adapter<AdapterOrder.HolderProductUser> implements Filterable {

    private Context context;
    public ArrayList<OrdersModel> productsList, filterList;
    private FilterProductUsers filter;
    private AddProductViewModel viewModel;

    public AdapterOrder(Context context, ArrayList<OrdersModel> productList){
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


        OrdersModel orderProduct = productsList.get(position);

        String orderTitle = orderProduct.getBuyerName();
        String originalPrice = orderProduct.getOriginalPrice();
        String productTitle = orderProduct.getProductTitle();
        String productId = orderProduct.getProductId();
        String timestamp = orderProduct.getTimeStamp();

        holder.titleTv.setText(orderTitle);
        holder.descriptionTv.setText(productTitle);
        holder.originalPriceTv.setText("$"+originalPrice);


        holder.addToCartTv.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
//            filter = new FilterProductUsers(this, filterList);
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
