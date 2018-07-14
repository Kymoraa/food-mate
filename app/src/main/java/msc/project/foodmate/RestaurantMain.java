package msc.project.foodmate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;



/**
 * Created by Jackie Moraa on 01/07/2018.
 */

public class RestaurantMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
        ,RestaurantHome.OnFragmentInteractionListener,RestaurantAdd.OnFragmentInteractionListener,
        RestaurantProfile.OnFragmentInteractionListener{

    private ActionBar actionBar;
    private FirebaseAuth firebaseAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_main);

        firebaseAuth = FirebaseAuth.getInstance();
//        if(firebaseAuth.getCurrentUser()==null){
//            //no user currently logged in
//            finish();
//            startActivity(new Intent(this,Login.class));
//        }



        actionBar = getSupportActionBar();

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // load the home fragment by default
        actionBar.setTitle("Food Mate");
        loadFragment(new RestaurantHome());

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    actionBar.setTitle("Food Mate");
                    fragment = new RestaurantHome();
                    loadFragment(fragment);
                    return true;

                case R.id.nav_add_new:
                    actionBar.setTitle("Add New");
                    fragment = new RestaurantAdd();
                    loadFragment(fragment);
                    return true;

                case R.id.nav_profile:
                    actionBar.setTitle("Restaurant Profile");
                    fragment = new RestaurantProfile();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    // method to load fragments to the frame layout
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.menu_settings){
            Toast.makeText(RestaurantMain.this, "Settings... coming soon", Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId()== R.id.menu_about){
            Toast.makeText(RestaurantMain.this, "About... coming soon", Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId()== R.id.menu_sign_out){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, Login.class));

        }
        return super.onOptionsItemSelected(item);
    }
}