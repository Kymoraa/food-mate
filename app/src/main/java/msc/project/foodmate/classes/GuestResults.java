package msc.project.foodmate.classes;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import msc.project.foodmate.R;
import msc.project.foodmate.database.model.CuisineUploads;
import msc.project.foodmate.view.GuestAdapter;

/*
display the results from the search conducted by a user without an account
 */
public class GuestResults extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GuestAdapter guestAdapter;
    private DatabaseReference databaseReference;
    private List<CuisineUploads> mCuisineUploads;
    private EditText etSearch;
    private RelativeLayout relativeLayout;
    private TextView tvNoEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_results);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mCuisineUploads= new ArrayList<>();
        tvNoEntries = findViewById(R.id.tvNoEntries);
        tvNoEntries.setVisibility(View.GONE);
        relativeLayout = findViewById(R.id.relativeLayout);

        //fetch the cuisines uploaded in the database and set them in the recyclerview
        //based on the results match
        databaseReference = FirebaseDatabase.getInstance().getReference("cuisineUploads");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    CuisineUploads cuisineUploads = postSnapshot.getValue(CuisineUploads.class);
                    mCuisineUploads.add(cuisineUploads);
                }

                guestAdapter = new GuestAdapter(getApplicationContext(), mCuisineUploads);
                recyclerView.setAdapter(guestAdapter);

                resultsMatch();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        //search the results based on the key words
        etSearch = findViewById(R.id.etSearch);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }

    //search method
    public void performSearch(){
        String userInput = etSearch.getText().toString().toLowerCase();
        List<CuisineUploads> newList = new ArrayList<>();

        for(CuisineUploads newCuisine : mCuisineUploads){
            if(newCuisine.getName().toLowerCase().contains(userInput)){
                newList.add(newCuisine);
            }else{
                tvNoEntries.setVisibility(View.VISIBLE);
            }

        }

        guestAdapter.searchList(newList);
    }

    //match results
    public void resultsMatch() {

        List<CuisineUploads> newList = new ArrayList<>();
        for(CuisineUploads newCuisine : mCuisineUploads) {

            String ingredients = newCuisine.getIngredients().toLowerCase();
            ArrayList<String> cuisineIngredients = new ArrayList<String>(Arrays.asList(ingredients.split(",")));


                Intent intent = getIntent();
                final String ingredient1 = intent.getStringExtra("ingredient1");
                final String ingredient2 = intent.getStringExtra("ingredient2");
                final String ingredient3 = intent.getStringExtra("ingredient3");
                final String ingredient4 = intent.getStringExtra("ingredient4");
                final String ingredient5 = intent.getStringExtra("ingredient5");

                String guestIng = ingredient1 + "," + ingredient2 + "," +
                        ingredient3 + "," + ingredient4 + "," + ingredient5;
                ArrayList<String> guestIngredients = new ArrayList<String>(Arrays.asList(guestIng.split(",")));

                //using regex to append '|' after each entry
                StringBuilder i_regex = new StringBuilder();
                boolean first = true;
                i_regex.append("^ *(");
                for (String w : guestIngredients) {
                    if (!first) {
                        i_regex.append('|');
                    } else {
                        first = false;
                    }
                    i_regex.append(w);
                }
                i_regex.append(") *$" );

                //loop through the ingredients
                Pattern i_pattern = Pattern.compile(i_regex.toString().toLowerCase());
                boolean ingredientsMatch =false;
                for (int i = 0; i < cuisineIngredients.size(); i++) {

                    if (i_pattern.matcher(cuisineIngredients.get(i)).find()) {
                        // Do something
                        ingredientsMatch = true;
                        break;
                    }
                }

            if(!ingredientsMatch){
                newList.add(newCuisine);
            }

            guestAdapter.searchList(newList);

            //toggle no results if there is no match
            if(newList.size()>0){
                tvNoEntries.setVisibility(View.GONE);
            }else{
                tvNoEntries.setVisibility(View.VISIBLE);
            }

        }

    }

    //overflow menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.guest_overflow_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.menu_settings){
            Snackbar snackbar = Snackbar.make(relativeLayout, "Settings...", Snackbar.LENGTH_LONG);
            snackbar.show ();
        }
        if(item.getItemId()== R.id.menu_about){
            Snackbar snackbar = Snackbar.make(relativeLayout, "About...", Snackbar.LENGTH_LONG);
            snackbar.show ();
        }
        if(item.getItemId()== R.id.menu_sign_in){
            startActivity(new Intent(this, Login.class));

        }
        return super.onOptionsItemSelected(item);
    }

}