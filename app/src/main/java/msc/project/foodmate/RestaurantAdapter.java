package msc.project.foodmate;

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
    public void onBindViewHolder(ImageViewHolder holder, final int position) {
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

                Intent intent = new Intent(mContext, CuisineDetails.class);
                intent.putExtra("imageUrl", uploadCurrent.getImageUri());
                intent.putExtra("cuisineName", uploadCurrent.getName());
                mContext.startActivity(intent);


//
//                Bundle args = new Bundle();
//                args.putParcelable("my_custom_object", myObject);
//                MyFragment fragment = new MyFragment();
//                fragment.setArguments(args);
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment_container, fragment);
//                transaction.commit()

//                SearchResults searchResults = new SearchResults();
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.frameLayout, searchResults,"Find This Fragment")
//                        .addToBackStack(null)
//                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mCuisineUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public ImageView ivCuisine;
        public TextView tvCuisineName, tvPrice, tvIngredients, tvDiets;
        private LinearLayout linearLayout;

        public ImageViewHolder(View itemView) {
            super(itemView);

            ivCuisine = itemView.findViewById(R.id.ivCuisine);
            tvCuisineName = itemView.findViewById(R.id.tvCuisineName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvIngredients = itemView.findViewById(R.id.tvIngredients);
            tvDiets = itemView.findViewById(R.id.tvDiets);
            linearLayout = itemView.findViewById(R.id.linearLayout);

        }
    }
}