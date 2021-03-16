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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterOrder extends RecyclerView.Adapter<AdapterOrder.HolderProductUser> implements Filterable {

    private Context context;
    public ArrayList<OrdersModel> productsList, filterList;
    private FilterProductUsers filter;
    private AddProductViewModel viewModel;

    public AdapterOrder(Context context, ArrayList<OrdersModel> productList,AddProductViewModel viewModel){
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

        String orderTitle = orderProduct.getOrderTitle();
        String originalPrice = orderProduct.getOriginalPrice();
        String productTitle = orderProduct.getOrderDescription();
        String productId = orderProduct.getOrderId();
        String timestamp = orderProduct.getTimestamp();

        holder.titleTv.setText(orderTitle);
        holder.descriptionTv.setText(productTitle);
        holder.originalPriceTv.setText("$"+originalPrice);
        String ownerId = orderProduct.getUserId();

        String userId = new UserInfo(context).getuserId();

        if (!userId.equals(ownerId))
            holder.deleteIv.setVisibility(View.GONE);


        holder.addToCartTv.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("productId",productId);
                intent.putExtra("navigation","order");
                context.startActivity(intent);

            }
        });

        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CollectionReference db = FirebaseFirestore.getInstance().collection("orders");

                db.document(productId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        notifyDataSetChanged();
                        viewModel.getOrders();
                        viewModel.getOrdersLiveData().observe((LifecycleOwner) context, new Observer<List<OrdersModel>>() {
                            @Override
                            public void onChanged(List<OrdersModel> modelProducts) {
                                setData((ArrayList<OrdersModel>) modelProducts);
                            }
                        });

                    }
                });

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

        private ImageView productIconIv,deleteIv;
        private TextView titleTv,descriptionTv,addToCartTv,originalPriceTv;

        public HolderProductUser(@NonNull View itemView) {
            super(itemView);

            productIconIv = itemView.findViewById(R.id.productIconIv);
            titleTv = itemView.findViewById(R.id.titleTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            addToCartTv = itemView.findViewById(R.id.addToCartTv);
            originalPriceTv = itemView.findViewById(R.id.originalPriceTv);
            deleteIv = itemView.findViewById(R.id.deleteBtn);
        }
    }

    public void setData(ArrayList<OrdersModel> list){
        this.productsList = list;
        notifyDataSetChanged();
    }


}
