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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import msc.project.foodmate.classes.CuisineDetails;
import msc.project.foodmate.database.model.FavouritesUpload;
import msc.project.foodmate.R;

/**
 * Created by Jackie Moraa on 7/12/2018.
 */

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ImageViewHolder> {

    private Context mContext;
    private List<FavouritesUpload> mFavouritesUpload;
    private DatabaseReference databaseReference;


    public FavouritesAdapter(Context context, List<FavouritesUpload> favouritesUploads){
        mContext = context;
        mFavouritesUpload = favouritesUploads;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourites_items, parent, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("favouriteCuisines");
        return  new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, final int position) {
        final FavouritesUpload uploadCurrent = mFavouritesUpload.get(position);
        Picasso.get()
                .load(uploadCurrent.getImageUri())
                .into(holder.ivCuisine);

        holder.tvCuisineName.setText(uploadCurrent.getName());
        holder.tvPrice.setText("Price: " + uploadCurrent.getPrice());
        holder.tvIngredients.setText("Ingredients: " + uploadCurrent.getIngredients());
        holder.tvDiets.setText(uploadCurrent.getDiet());
        holder.tvDescription.setText(uploadCurrent.getDescription());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Clicked on: " + mFavouritesUpload.get(position));

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
        return mFavouritesUpload.size();
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

    public void removeItem(int position) {
        mFavouritesUpload.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void getRef(DatabaseReference ref) {
        databaseReference = ref;
    }



}