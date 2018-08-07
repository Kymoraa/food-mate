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
import msc.project.foodmate.database.model.AllergenDB;


/**
 * Created by Jackie Moraa on 8/1/2018.
 */

public class AllergensAdapter extends RecyclerView.Adapter<AllergensAdapter.MyViewHolder> {

    private Context context;
    private List<AllergenDB> allergensList;

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


    public AllergensAdapter(Context context, List<AllergenDB> allergensList) {
        this.context = context;
        this.allergensList = allergensList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_entries_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AllergenDB allergen = allergensList.get(position);

        holder.tvEntryName.setText(allergen.getAllergenDB());

        // Displaying dot from HTML character code
        holder.dot.setText(Html.fromHtml("&#8226;"));

        // Formatting and displaying timestamp
        holder.timestamp.setText(formatDate(allergen.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return allergensList.size();
    }

    /**
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

