package msc.project.foodmate.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import msc.project.foodmate.classes.CuisineDetails;
import msc.project.foodmate.database.model.PopularModel;
import msc.project.foodmate.R;

/**
 * Created by Jackie Moraa on 7/22/2018.
 */

/*
1. Inflate the model layout into view item
2. Bind the data
 */

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.MyHolder>{

    List<PopularModel> mList;
    Context mContext;

    public PopularAdapter(List<PopularModel> list, Context context) {
        mContext = context;
        mList = list;
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_items,parent,false);
        //MyHolder myHolder = new MyHolder(view);

        return new MyHolder(view);
    }

    /*
    holder class to set the views for the popular items
     */
    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        final PopularModel mylist = mList.get(position);

        Picasso.get()
                .load(mylist.getImageUri())
                .into(holder.ivThumbnail);
        holder.tvName.setText(mylist.getName());
        holder.tvPrice.setText(mylist.getPrice());
        holder.tvDescription.setText(mylist.getDescription());
        holder.tvIngredients.setText(mylist.getIngredients());
        holder.tvDiet.setText(mylist.getDiet());

        //when item at position is clicked, send the details via intent extras to the details page
        holder.ivThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Clicked on: " + mList.get(position));
                Intent intent = new Intent(mContext, CuisineDetails.class);
                intent.putExtra("imageUrl", mylist.getImageUri());
                intent.putExtra("name", mylist.getName());
                intent.putExtra("description", mylist.getDescription());
                intent.putExtra("price", mylist.getPrice());
                intent.putExtra("ingredients", mylist.getIngredients());
                intent.putExtra("diet",mylist.getDiet());
                mContext.startActivity(intent);
            }
        });

        //when item at position is clicked, send the details via intent extras to the details page
        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Clicked on: " + mList.get(position));
                Intent intent = new Intent(mContext, CuisineDetails.class);
                intent.putExtra("imageUrl", mylist.getImageUri());
                intent.putExtra("name", mylist.getName());
                intent.putExtra("description", mylist.getDescription());
                intent.putExtra("price", mylist.getPrice());
                intent.putExtra("ingredients", mylist.getIngredients());
                intent.putExtra("diet",mylist.getDiet());
                mContext.startActivity(intent);
            }
        });

        //when item at position is clicked, send the details via intent extras to the details page
        holder.tvPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Clicked on: " + mList.get(position));
                Intent intent = new Intent(mContext, CuisineDetails.class);
                intent.putExtra("imageUrl", mylist.getImageUri());
                intent.putExtra("name", mylist.getName());
                intent.putExtra("description", mylist.getDescription());
                intent.putExtra("price", mylist.getPrice());
                intent.putExtra("ingredients", mylist.getIngredients());
                intent.putExtra("diet",mylist.getDiet());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        //only show two items in the adapter
        return Math.min(mList.size(), 2);
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        public ImageView ivThumbnail;
        public TextView tvName,tvPrice, tvDescription, tvIngredients, tvDiet;


        public MyHolder(View itemView) {
            super(itemView);

            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice= itemView.findViewById(R.id.tvPrice);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvIngredients =  itemView.findViewById(R.id.tvIngredients);
            tvDiet = itemView.findViewById(R.id.tvDiet);

        }
    }
}
