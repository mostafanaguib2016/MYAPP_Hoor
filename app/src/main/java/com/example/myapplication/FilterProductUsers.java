package com.example.myapplication;

import android.widget.Filter;

import com.example.myapplication.adapters.AdapterProductSeller;
import com.example.myapplication.adapters.AdapterProductUser;
import com.example.myapplication.models.ModelProduct;

import java.util.ArrayList;

public class FilterProductUsers extends Filter {

    private AdapterProductUser adapter;
    private ArrayList<ModelProduct> filterList;

    public FilterProductUsers(AdapterProductUser adapter, ArrayList<ModelProduct> filterList){
        this.adapter = adapter;
        this.filterList = filterList;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0){



            constraint = constraint.toString().toUpperCase();

            ArrayList<ModelProduct> filteredModels = new ArrayList<>();
            for (int i=0; i<filterList.size(); i++){

                if (filterList.get(i).getProductTitle().toUpperCase().contains(constraint) ||
                        filterList.get(i).getProductCategory().toUpperCase().contains(constraint) ){

                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else {


            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.productsList = (ArrayList<ModelProduct>) results.values;

        adapter.notifyDataSetChanged();
    }
}
