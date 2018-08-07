package msc.project.foodmate;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class CuisineDetails extends AppCompatActivity {

    private ImageView ivCuisine, ivFavourite;
    private TextView tvCuisineName, tvRestaurantName, tvDescription, tvPrice, tvIngredients, tvDiet;
    private Button bCall, bMap;
    private LinearLayout linearLayout;

    private FirebaseAuth firebaseAuth;
    private Uri imageUri;

    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference, favReference;

    String mStoragePath = "favouriteCuisines/";
    String mDatabasePath = "favouriteCuisines";

    ProgressDialog mProgressDialog;
    private FirebaseUser firebaseUser;

    boolean isClicked = false;

    public CuisineDetails() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cuisine_details);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();


        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(mDatabasePath);
        favReference = FirebaseDatabase.getInstance().getReference(mDatabasePath);

        mProgressDialog = new ProgressDialog(this);

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

        ivFavourite = findViewById(R.id.ivFavourite);
        ivFavourite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(isClicked){
                    removeFavourite();
                    view.setBackgroundResource(R.drawable.ic_fav_outline);

                }else{

                    addFavourite();
                    view.setBackgroundResource(R.drawable.ic_fav_solid);

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

    private void isFavourite() {
        favReference = mDatabaseReference.child("name");
        final String name  = getIntent().getStringExtra("name");
        favReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    if (data.child(name).exists()) {
                        Snackbar snackbar = Snackbar.make(linearLayout, "Favourite already exists", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } else {
                        addFavourite();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void addFavourite(){
        //method to add item to favourites

        mProgressDialog.setTitle("Adding to favourites...");
        mProgressDialog.show();

        if(getIntent().hasExtra("imageUrl")&& getIntent().hasExtra("name")&& getIntent().hasExtra("price")
                && getIntent().hasExtra("description") && getIntent().hasExtra("ingredients")){

            String imageUrl  = getIntent().getStringExtra("imageUrl");
            final String name  = getIntent().getStringExtra("name");
            final String price  = getIntent().getStringExtra("price");
            final String description  = getIntent().getStringExtra("description");
            final String ingredients  = getIntent().getStringExtra("ingredients");
            final String diet = getIntent().getStringExtra("diet");

            InputStream is = getConnection(imageUrl);

            if (is != null ) {
                StorageReference sref = mStorageReference.child(mStoragePath + System.currentTimeMillis());
                //    + "." + getFileExtension(mFilePathUri));

                sref.putStream(is)

                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                CuisineUploads cuisineUploads = new CuisineUploads(name, price, description, ingredients,
                                        taskSnapshot.getStorage().getDownloadUrl().toString(), diet);


                                //hide progress dialog
                                mProgressDialog.dismiss();

                                Snackbar snackbar = Snackbar.make(linearLayout, "Successfully added to favourites", Snackbar.LENGTH_LONG);
                                snackbar.show();

                                //getting image ID
                                String imageUploadID = mDatabaseReference.push().getKey();
                                //uploading it into database reference
                                mDatabaseReference.child(imageUploadID).setValue(cuisineUploads);
                            }
                        })
                        //if something goes wrong
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mProgressDialog.dismiss();

                                //show error toast
                                Snackbar snackbar = Snackbar.make(linearLayout, "Unable to add to favourites", Snackbar.LENGTH_LONG);
                                snackbar.show();

                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {


                            }
                        });
            }else{
                Snackbar snackbar = Snackbar.make(linearLayout, "No selection made", Snackbar.LENGTH_LONG);
                snackbar.show();
            }


        }

    }


    private void removeFavourite(){
        Snackbar snackbar = Snackbar.make(linearLayout, "Removed from favourites", Snackbar.LENGTH_LONG);
        snackbar.show ();

    }

    private InputStream getConnection(String imageUrl) {
        InputStream is = null;
        try {
            URLConnection conn = new URL(imageUrl).openConnection();
            is = conn.getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
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
