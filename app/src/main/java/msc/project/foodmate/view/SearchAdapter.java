package msc.project.foodmate.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import msc.project.foodmate.classes.CuisineDetails;
import msc.project.foodmate.R;
import msc.project.foodmate.database.model.CuisineUploads;

/**
 * Created by Jackie Moraa on 7/12/2018.
 */

/*
search adapter
where the search is done, cuisines will be displayed here from the database
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ImageViewHolder> {

    private Context mContext;
    private List<CuisineUploads> mCuisineUploads;


    public SearchAdapter(Context context, List<CuisineUploads> cuisineUploads){
        mContext = context;
        mCuisineUploads = cuisineUploads;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       //inflate the views
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_items, parent, false);


        return  new ImageViewHolder(v);
    }

    /*
holder class to set the views for the popular items
 */
    @Override
    public void onBindViewHolder(ImageViewHolder holder, final int position) {
        final CuisineUploads uploadCurrent = mCuisineUploads.get(position);
        Picasso.get()
                .load(uploadCurrent.getImageUri())
                .into(holder.ivCuisine);

        holder.tvCuisineName.setText(uploadCurrent.getName());
        holder.tvPrice.setText("Price: " + uploadCurrent.getPrice());
        holder.tvIngredients.setText("Ingredients: " + uploadCurrent.getIngredients());
        holder.tvDiets.setText(uploadCurrent.getDiet());
        holder.tvDescription.setText(uploadCurrent.getDescription());

        //when item at position is clicked, send the details via intent extras to the details page
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Clicked on: " + mCuisineUploads.get(position));

                Intent intent = new Intent(mContext, CuisineDetails.class);
                intent.putExtra("imageUrl", uploadCurrent.getImageUri());
                intent.putExtra("name", uploadCurrent.getName());
                intent.putExtra("description", uploadCurrent.getDescription());
                intent.putExtra("price", uploadCurrent.getPrice());
                intent.putExtra("ingredients", uploadCurrent.getIngredients());
                intent.putExtra("diet",uploadCurrent.getDiet());
                mContext.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return mCuisineUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public ImageView ivCuisine;
        public TextView tvCuisineName, tvPrice, tvIngredients, tvDiets, tvDescription;
        private LinearLayout linearLayout;

        public ImageViewHolder(View itemView) {
            super(itemView);

            ivCuisine = itemView.findViewById(R.id.ivCuisine);
            tvCuisineName = itemView.findViewById(R.id.tvCuisineName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvIngredients = itemView.findViewById(R.id.tvIngredients);
            tvDiets = itemView.findViewById(R.id.tvDiets);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            linearLayout = itemView.findViewById(R.id.linearLayout);

        }
    }

    public void searchList(List<CuisineUploads> newList){
        mCuisineUploads = new ArrayList<>();
        mCuisineUploads.addAll(newList);
        notifyDataSetChanged();

    }


}