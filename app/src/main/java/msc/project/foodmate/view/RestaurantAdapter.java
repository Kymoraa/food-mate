package msc.project.foodmate.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import msc.project.foodmate.database.model.CuisineUploads;
import msc.project.foodmate.R;

/**
 * Created by Jackie Moraa on 7/12/2018.
 */

/*
restaurant adapter
where the cuisines will be displayed from the database
 */
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ImageViewHolder> {

    private Context mContext;
    private List<CuisineUploads> mCuisineUploads;

    public RestaurantAdapter(Context context, List<CuisineUploads> cuisineUploads){
        mContext = context;
        mCuisineUploads = cuisineUploads;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cuisine_items, parent, false);

        return  new ImageViewHolder(v);
    }

    /*
holder class to set the views for the popular items
 */

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final CuisineUploads uploadCurrent = mCuisineUploads.get(position);
        Picasso.get()
                .load(uploadCurrent.getImageUri())
                .into(holder.ivCuisine);

        holder.tvCuisineName.setText(uploadCurrent.getName());
        holder.tvPrice.setText("Price: " + uploadCurrent.getPrice());
        holder.tvIngredients.setText("Ingredients: " + uploadCurrent.getIngredients());
        holder.tvDiets.setText("Diet: " + uploadCurrent.getDiet());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Clicked on: " + mCuisineUploads.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mCuisineUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public ImageView ivCuisine;
        private TextView tvCuisineName, tvPrice, tvIngredients, tvDiets;
        private LinearLayout linearLayout;

        private ImageViewHolder(View itemView) {
            super(itemView);

            //find the views to set the data
            ivCuisine = itemView.findViewById(R.id.ivCuisine);
            tvCuisineName = itemView.findViewById(R.id.tvCuisineName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvIngredients = itemView.findViewById(R.id.tvIngredients);
            tvDiets = itemView.findViewById(R.id.tvDiets);
            linearLayout = itemView.findViewById(R.id.linearLayout);

        }

    }

}