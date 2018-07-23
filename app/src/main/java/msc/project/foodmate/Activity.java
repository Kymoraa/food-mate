package msc.project.foodmate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Activity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef ;
    List<RecentModel> list;
    RecyclerView recycle;

    public Activity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        //Populate the recycler view from Firebase
        recycle = (RecyclerView) findViewById(R.id.rvRecent);


        RecyclerView.LayoutManager recyce = new GridLayoutManager(Activity.this,2);
        /// RecyclerView.LayoutManager recyce = new LinearLayoutManager(MainActivity.this);
        // recycle.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recycle.setLayoutManager(recyce);
        recycle.setItemAnimator( new DefaultItemAnimator());


        list = new ArrayList<>();

        myRef = FirebaseDatabase.getInstance().getReference("cuisineUploads");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){

                    RecentModel value = dataSnapshot1.getValue(RecentModel.class);
                    list.add(value);

                }

                RecentAdapter recentAdapter = new RecentAdapter(list,Activity.this);

                recycle.setAdapter(recentAdapter);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });






    }
}
