package msc.project.foodmate.classes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

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

import msc.project.foodmate.R;
import msc.project.foodmate.database.model.FavouritesUpload;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/*
class that contains the cuisine details e.g. description and the restaurant
the intent extras are carried from the search results
contains methods to add to favourites, call the restaurant or get directions to the restaurant
 */

public class CuisineDetails extends AppCompatActivity {

    private ImageView ivCuisine, ivFavourite;
    private TextView tvCuisineName, tvRestaurantName, tvDescription, tvPrice, tvIngredients, tvDiet;
    private Button bCall, bMap;
    private LinearLayout linearLayout;
    private FirebaseAuth firebaseAuth;

    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;

    String mStoragePath = "favouriteCuisines/";
    String mDatabasePath = "favouriteCuisines";

    ProgressDialog mProgressDialog;
    private FirebaseUser firebaseUser;

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

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(mDatabasePath);

        mProgressDialog = new ProgressDialog(this);
        linearLayout = findViewById(R.id.linearLayout);
        ivCuisine = findViewById(R.id.ivCuisine);
        tvCuisineName = findViewById(R.id.tvCuisineName);
        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        tvDescription = findViewById(R.id.tvDescription);
        tvPrice = findViewById(R.id.tvPrice);
        tvIngredients = findViewById(R.id.tvIngredients);
        tvDiet = findViewById(R.id.tvDiet);

        //when call button is clicked, invoke the call() method
        bCall = findViewById(R.id.bCall);
        bCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                call();

            }
        });

        //when map button is clicked, invoke the map() method
        bMap= findViewById(R.id.bMap);
        bMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map();
            }
        });

        //the overflow dots. When clicked the popup menu is displayed
        ivFavourite = findViewById(R.id.ivFavourite);
        ivFavourite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                int position=0;
                showPopupMenu(ivFavourite, position);
            }
        });

        getIntentExtras();

    }

    //call restaurant method
    private void call() {
        String phoneNumber = "+44 777 111 1717"; //Dummy phone number
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
        startActivity(intent);

    }

    //get directions method
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

    //get the intent extras that are passed from the previous class e.g.the search result class/favourites
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

    //display the extras
    private void setIntentExtras( String imageUrl, String name, String price, String description, String ingredients,
                                  String diet){

        Picasso.get().load(imageUrl).into(ivCuisine);
        tvCuisineName.setText(name);
        tvDescription.setText(description);
        tvPrice.setText(price);
        tvIngredients.setText(ingredients);
        tvDiet.setText(diet);

    }

    /*
    pop up menu with actions of:
    1. add to favourites
    2. share the cuisine through different channels
    */

    private void showPopupMenu(View view,int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(),view );
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuItemClickListener(position));
        popup.show();
    }

    class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int position;
        public MenuItemClickListener(int position) {
            this.position=position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.menu_favourites:
                    isFavourite();
                    return true;

                case R.id.menu_share:
                    share();
                    return true;

                default:
            }
            return false;
        }
    }

    //method to check if the user already has the item in their favourites list
    private void isFavourite() {

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("favouriteCuisines");

        final String name  = getIntent().getStringExtra("name");
        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isFavourite = false;
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    String cuisineName = data.child("name").getValue(String.class);
                    String user = data.child("userId").getValue(String.class);

                    //if they have the favourites already...
                    if (name.equals(cuisineName)&&currentUser.equals(user)) {
                        isFavourite = true;
                        Snackbar snackbar = Snackbar.make(linearLayout, "Favourite already exists", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        break;
                    }
                }
                //if they do not have the item as a favourite then the add to favourites method is invoked
                if(!isFavourite) {
                    addFavourite();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //add item to the favourites table in firebase with their user id
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
            final String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            InputStream is = getConnection(imageUrl);


            if (is != null ) {
                StorageReference sref = mStorageReference.child(mStoragePath + System.currentTimeMillis());
                sref.putStream(is)

                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                FavouritesUpload favouritesUpload = new FavouritesUpload(userUid,name, price, description, ingredients,
                                        taskSnapshot.getStorage().getDownloadUrl().toString(), diet);


                                //hide progress dialog
                                mProgressDialog.dismiss();

                                Snackbar snackbar = Snackbar.make(linearLayout, "Successfully added to favourites", Snackbar.LENGTH_LONG);
                                snackbar.show();

                                //getting image ID
                                String imageUploadID = mDatabaseReference.push().getKey();
                                //uploading it into database reference
                                mDatabaseReference.child(imageUploadID).setValue(favouritesUpload);
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
                                mProgressDialog.dismiss();

                            }
                        });
            }else{
                Snackbar snackbar = Snackbar.make(linearLayout, "No selection made", Snackbar.LENGTH_LONG);
                snackbar.show();
            }


        }

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

    //share via the cuisine/dish e.g. WhatsApp, Gmail, Text
    public void share(){
        Intent sendIntent = new Intent();
        String msg = "Hey, check out this cuisine on Food Mate: " + tvCuisineName.getText()
                +". Download here: https://www.foodmate.com/downloads";
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // overflow menu items
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
        if(item.getItemId()== R.id.menu_sign_out){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, Login.class));

        }
        return super.onOptionsItemSelected(item);
    }
}
