package msc.project.foodmate;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


/**
 * Created by Jackie Moraa on 01/07/2018.
 */

public class Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
        ,MainHome.OnFragmentInteractionListener,MainHistory.OnFragmentInteractionListener,
        MainFavourites.OnFragmentInteractionListener, MainProfile.OnFragmentInteractionListener{

    private ActionBar actionBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        actionBar = getSupportActionBar();

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // load the home fragment by default
        actionBar.setTitle("Food Mate");
        loadFragment(new MainHome());

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

                case R.id.nav_history:
                    actionBar.setTitle("History");
                    fragment = new MainHistory();
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
}
