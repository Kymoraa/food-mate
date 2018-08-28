package msc.project.foodmate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * Created by Jackie Moraa on 01/07/2018.
 */

public class Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
        ,MainHome.OnFragmentInteractionListener, MainFavourites.OnFragmentInteractionListener, MainProfile.OnFragmentInteractionListener,
        SearchResults.OnFragmentInteractionListener, Diets.OnFragmentInteractionListener,
        Ingredients.OnFragmentInteractionListener, Allergens.OnFragmentInteractionListener{

    private ActionBar actionBar;
    private FirebaseAuth firebaseAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            //no user currently logged in
            finish();
            startActivity(new Intent(this,Login.class));
        }



        // load the home fragment by default
        actionBar = getSupportActionBar();
        actionBar.setTitle("Food Mate");
        loadFragment(new MainHome());

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior());
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);




    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    actionBar.setTitle("Food Mate");
                    fragment = new MainHome();
                    loadFragment(fragment);
                    return true;

                case R.id.nav_favourite:
                    actionBar.setTitle("Favourites");
                    fragment = new MainFavourites();
                    loadFragment(fragment);
                    return true;

                case R.id.nav_profile:
                    actionBar.setTitle("My Profile");
                    fragment = new MainProfile();
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
            //Toast.makeText(Main.this, "Settings... coming soon", Toast.LENGTH_SHORT).show();
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
