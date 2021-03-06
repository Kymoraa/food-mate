package msc.project.foodmate.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import msc.project.foodmate.R;
import msc.project.foodmate.database.model.IngredientDB;

/**
 * Created by Jackie Moraa on 8/1/2018.
 */

/*
ingredients adapter
where the ingredients will be displayed from the database
 */
public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.MyViewHolder> {

    private Context context;
    private List<IngredientDB> ingredientsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEntryName;
        public TextView dot;
        public TextView timestamp;

        public MyViewHolder(View view) {
            super(view);
            tvEntryName = view.findViewById(R.id.tvEntryName);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }


    public IngredientsAdapter(Context context, List<IngredientDB> ingredientsList) {
        this.context = context;
        this.ingredientsList = ingredientsList;
    }

    //view holder
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_entries_list_row, parent, false); //check on this

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        IngredientDB ingredient = ingredientsList.get(position);

        holder.tvEntryName.setText(ingredient.getIngredientDB());

        // Displaying dot from HTML character code
        holder.dot.setText(Html.fromHtml("&#8226;"));

        // Formatting and displaying timestamp
        holder.timestamp.setText(formatDate(ingredient.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }

    /*
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
}

