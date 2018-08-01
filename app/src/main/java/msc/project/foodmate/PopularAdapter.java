package msc.project.foodmate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Jackie Moraa on 7/22/2018.
 */

/*
1. Inflate the model layout into view item
2. Bind the data
 */

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.MyHolder>{

    List<PopularModel> list;
    Context context;

    public PopularAdapter(List<PopularModel> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @Override
    public PopularAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.popular_items,parent,false);
        MyHolder myHolder = new MyHolder(view);

        return myHolder;
    }

    @Override
    public void onBindViewHolder(PopularAdapter.MyHolder holder, int position) {
        PopularModel mylist = list.get(position);

        Picasso.get()
                .load(mylist.getImageUri())
                .into(holder.ivThumbnail);
        holder.tvName.setText(mylist.getName());
        holder.tvPrice.setText(mylist.getPrice());


    }

    @Override
    public int getItemCount() {

        return Math.min(list.size(), 2);

    }


    class MyHolder extends RecyclerView.ViewHolder{

        public ImageView ivThumbnail;
        public TextView tvName,tvPrice;


        public MyHolder(View itemView) {
            super(itemView);

            ivThumbnail = (ImageView) itemView.findViewById(R.id.ivThumbnail);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvPrice= (TextView) itemView.findViewById(R.id.tvPrice);


        }
    }
}
