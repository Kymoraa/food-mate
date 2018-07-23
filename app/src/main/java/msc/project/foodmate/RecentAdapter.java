package msc.project.foodmate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jackie Moraa on 7/22/2018.
 */

/*
1. Inflate the model layout into view item
2. Bind the data
 */

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.MyHolder>{

    List<RecentModel> list;
    Context context;

    public RecentAdapter(List<RecentModel> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @Override
    public RecentAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_items,parent,false);
        MyHolder myHolder = new MyHolder(view);

        return myHolder;
    }

    @Override
    public void onBindViewHolder(RecentAdapter.MyHolder holder, int position) {
        RecentModel mylist = list.get(position);

        Picasso.get()
                .load(mylist.getImageUri())
                .into(holder.ivThumbnail);
        holder.tvName.setText(mylist.getName());
        holder.tvPrice.setText(mylist.getPrice());


    }

    @Override
    public int getItemCount() {
//        int arr = 0;
//
//        try{
//            if(list.size()==0){
//
//                arr = 0;
//
//            }
//            else{
//
//                arr=list.size();
//            }
//
//
//
//        }catch (Exception e){
//
//
//
//        }
//
//        return arr;

        return Math.min(list.size(), 2);

        //return Math.min(items.size(), 10);

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


//previous tutorial
//    Context c;
//    ArrayList<RecentModel> recentModels;
//
//    public RecentAdapter(Context c, ArrayList<RecentModel> recentModels) {
//        this.c = c;
//        this.recentModels = recentModels;
//    }
//
//    @Override
//    public int getCount() {
//        return recentModels.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return recentModels.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null){
//            convertView = LayoutInflater.from(c).inflate(R.layout.recent_items,parent,false);
//        }
//
//        ImageView gvImage = (ImageView) convertView.findViewById(R.id.gvImage);
//        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
//        TextView tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
//
//        final RecentModel gvModel = (RecentModel)this.getItem(position);
//
//        Picasso.get()
//                .load(gvModel.getImageUri())
//                .into(gvImage);
//        tvName.setText(gvModel.getName());
//        tvPrice.setText(gvModel.getPrice());
//
//        //OnItemClick
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(c, gvModel.getName(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//        return convertView;
//    }
