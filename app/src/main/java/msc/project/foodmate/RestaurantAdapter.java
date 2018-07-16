package msc.project.foodmate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Jackie Moraa on 7/12/2018.
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cuisine_items, parent, false);
        return  new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        CuisineUploads uploadCurrent = mCuisineUploads.get(position);
        Picasso.get()
                .load(uploadCurrent.getImageUri())
                .resize(50,50)
                .into(holder.ivCuisine);

//        Picasso picasso = new Picasso.Builder(mContext)
//                .downloader(new )
//                .build();

//        Glide.with(mContext)
//                .load(uploadCurrent.getImageUri())
//                .into(holder.ivCuisine);


        holder.tvCuisineName.setText(uploadCurrent.getName());
        holder.tvPrice.setText(uploadCurrent.getPrice());


    }

    @Override
    public int getItemCount() {
        return mCuisineUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public ImageView ivCuisine;
        public TextView tvCuisineName, tvPrice;

        public ImageViewHolder(View itemView) {
            super(itemView);

            ivCuisine = itemView.findViewById(R.id.ivCuisine);
            tvCuisineName = itemView.findViewById(R.id.tvCuisineName);
            tvPrice = itemView.findViewById(R.id.tvPrice);

        }
    }
}