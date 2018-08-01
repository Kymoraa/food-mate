package msc.project.foodmate;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class CuisineDetails extends AppCompatActivity {

    private ImageView ivCuisine, ivFavourite;
    private TextView tvCuisineName, tvRestaurantName, tvDescription, tvPrice, tvIngredients;
    private Button bCall, bMap;
    private LinearLayout linearLayout;

    private FirebaseAuth firebaseAuth;
    private Uri imageUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    boolean isClicked = false;

    public CuisineDetails() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cuisine_details);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("favouriteCuisines");
        databaseReference = FirebaseDatabase.getInstance().getReference("favouriteCuisines");

        linearLayout = findViewById(R.id.linearLayout);

        ivCuisine = findViewById(R.id.ivCuisine);

        tvCuisineName = findViewById(R.id.tvCuisineName);
        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        tvDescription = findViewById(R.id.tvDescription);
        tvPrice = findViewById(R.id.tvPrice);
        tvIngredients = findViewById(R.id.tvIngredients);

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

        ivFavourite = findViewById(R.id.ivFavourite);
        ivFavourite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(isClicked){
                    view.setBackgroundResource(R.drawable.ic_fav_outline);
                    Snackbar snackbar = Snackbar.make(linearLayout, "Removed from favourites", Snackbar.LENGTH_LONG);
                    snackbar.show ();
                }else{
                    addFavourite();
                    view.setBackgroundResource(R.drawable.ic_fav_solid);
                    Snackbar snackbar = Snackbar.make(linearLayout, "Added to favourites", Snackbar.LENGTH_LONG);
                    snackbar.show ();
                }

                isClicked =!isClicked;
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

            setIntentExtras(imageUrl, name, price, description, ingredients);

        }

    }

    private void setIntentExtras( String imageUrl, String name, String price, String description, String ingredients){

        Picasso.get().load(imageUrl).into(ivCuisine);
        tvCuisineName.setText(name);
        tvDescription.setText(description);
        tvPrice.setText(price);
        tvIngredients.setText(ingredients);

    }

    private void addFavourite(){
        //method to add ite to favourites

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.menu_settings){
            Snackbar snackbar = Snackbar.make(linearLayout, "Settings...", Snackbar.LENGTH_LONG);
            snackbar.show ();
        }
        if(item.getItemId()== R.id.menu_about){
            Snackbar snackbar = Snackbar.make(linearLayout, "About...", Snackbar.LENGTH_LONG);
            snackbar.show ();
        }
        if(item.getItemId()== R.id.menu_sign_out){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, Login.class));

        }
        return super.onOptionsItemSelected(item);
    }
}
