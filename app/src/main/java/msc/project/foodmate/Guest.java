package msc.project.foodmate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class Guest extends AppCompatActivity {
    private EditText etEntry1, etEntry2, etEntry3, etEntry4, etEntry5;
    private FloatingActionButton fab;
    private ProgressDialog progressDialog;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest);

        etEntry1 = findViewById(R.id.etEntry1);
        etEntry2 = findViewById(R.id.etEntry2);
        etEntry3 = findViewById(R.id.etEntry3);
        etEntry4 = findViewById(R.id.etEntry4);
        etEntry5 = findViewById(R.id.etEntry5);

        progressDialog = new ProgressDialog(this);
        relativeLayout = findViewById(R.id.relativeLayout);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Loading...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                Runnable progressRunnable = new Runnable() {

                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                };

                Handler pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 3000);

                Intent intent = new Intent(Guest.this, GuestResults.class);
                intent.putExtra("ingredient1", etEntry1.getText().toString().trim());
                intent.putExtra("ingredient2", etEntry2.getText().toString().trim());
                intent.putExtra("ingredient3", etEntry3.getText().toString().trim());
                intent.putExtra("ingredient4", etEntry4.getText().toString().trim());
                intent.putExtra("ingredient5", etEntry5.getText().toString().trim());
                startActivity(intent);


            }
        });



    }

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
