package msc.project.foodmate.classes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import msc.project.foodmate.R;


public class GuestCuisineDetails extends AppCompatActivity {

    private ImageView ivCuisine;
    private TextView tvCuisineName, tvRestaurantName, tvDescription, tvPrice, tvIngredients, tvDiet;
    private Button bCall, bMap;
    private LinearLayout linearLayout;
    private FirebaseAuth firebaseAuth;


    ProgressDialog mProgressDialog;
    private FirebaseUser firebaseUser;


    public GuestCuisineDetails() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_cuisine_details);


        linearLayout = findViewById(R.id.linearLayout);

        ivCuisine = findViewById(R.id.ivCuisine);

        tvCuisineName = findViewById(R.id.tvCuisineName);
        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        tvDescription = findViewById(R.id.tvDescription);
        tvPrice = findViewById(R.id.tvPrice);
        tvIngredients = findViewById(R.id.tvIngredients);
        tvDiet = findViewById(R.id.tvDiet);


        bCall = findViewById(R.id.bCall);
        bCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                call();

            }
        });

        bMap= findViewById(R.id.bMap);
        bMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map();
            }
        });

        getIntentExtras();

    }

    private void call() {
        String phoneNumber = "+44 777 111 1717"; //Dummy phone number
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
        startActivity(intent);

    }

    private void map() {
        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(tvRestaurantName.getText().toString()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }else{
            Snackbar snackbar = Snackbar.make(linearLayout, "Unable to launch Google Maps", Snackbar.LENGTH_LONG);
            snackbar.show ();
        }
    }

    private void getIntentExtras(){
        if(getIntent().hasExtra("imageUrl")&& getIntent().hasExtra("name")&& getIntent().hasExtra("price")
                && getIntent().hasExtra("description") && getIntent().hasExtra("ingredients")){

            String imageUrl  = getIntent().getStringExtra("imageUrl");
            String name  = getIntent().getStringExtra("name");
            String price  = getIntent().getStringExtra("price");
            String description  = getIntent().getStringExtra("description");
            String ingredients  = getIntent().getStringExtra("ingredients");
            String diet = getIntent().getStringExtra("diet");

            setIntentExtras(imageUrl, name, price, description, ingredients, diet);

        }

    }

    private void setIntentExtras( String imageUrl, String name, String price, String description, String ingredients,
                                  String diet){

        Picasso.get().load(imageUrl).into(ivCuisine);
        tvCuisineName.setText(name);
        tvDescription.setText(description);
        tvPrice.setText(price);
        tvIngredients.setText(ingredients);
        tvDiet.setText(diet);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.guest_overflow_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.menu_settings){
            startActivity(new Intent(this, SettingsActivity.class));
        }
        if(item.getItemId()== R.id.menu_about){
            startActivity(new Intent(this, About.class));
        }

        if(item.getItemId()== R.id.menu_help){
            startActivity(new Intent(this, HelpFAQs.class));
        }
        if(item.getItemId()== R.id.menu_sign_in){
            startActivity(new Intent(this, Login.class));

        }
        return super.onOptionsItemSelected(item);
    }
}
