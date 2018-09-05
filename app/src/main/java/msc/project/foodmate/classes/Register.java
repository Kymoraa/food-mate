package msc.project.foodmate.classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;

import java.util.regex.Pattern;

import msc.project.foodmate.R;

/**
 * Created by Jackie Moraa on 30/06/2018.
 */
/*
class to register a new user in the database
uses Firebase authentication
 */
public class Register extends AppCompatActivity implements View.OnClickListener {

    private Button bRegister;
    private EditText etEmail, etPassword, etConfirmPassword;
    private CheckBox cbRestaurant;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private LinearLayout linearLayout;

    //validate the email pattern
    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9+._%-+]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
                    "(" +
                    "." +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
                    ")+"
    );

    //password rules
    public final Pattern PASSWORD_PATTERN = Pattern.compile( "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        linearLayout = findViewById(R.id.linearLayout);
        firebaseAuth = FirebaseAuth.getInstance();
        //check if user is already logged in
        if (firebaseAuth.getCurrentUser() != null){
            //user is already logged in. start main activity
            finish();
            Intent intent = new Intent(Register.this, Main.class);
            startActivity(intent);
        }

        progressDialog = new ProgressDialog(this);

        bRegister = (Button) findViewById(R.id.bRegister);
        bRegister.setOnClickListener(this);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);

        //for users to be registered as restaurants only
        cbRestaurant = (CheckBox) findViewById(R.id.cbRestaurant);
        cbRestaurant.setChecked(false);

        cbRestaurant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // update your model (or other business logic) based on isChecked
                Snackbar snackbar = Snackbar.make(linearLayout, "Note! Only applicable to verified restaurants", Snackbar.LENGTH_LONG);
                snackbar.show ();
                cbRestaurant.setChecked(false);
            }
        });


    }

    @Override
    public void onClick(View view) {
        if (view == bRegister){
            registerUser();
        }
    }

    //reister the user
    private void registerUser(){
        final String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        //field validations
        if (TextUtils.isEmpty(email)){
            //email field is empty
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();
            //stop the function
            return;
        }

        else if(!EMAIL_ADDRESS_PATTERN.matcher(email).matches()){
            Toast.makeText(this,"Invalid Email Address",Toast.LENGTH_SHORT).show();
            //stop the function
            return;
        }

        else if (TextUtils.isEmpty(password)){
            //password field is empty
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
            //stop the function
            return;
        }else if(!PASSWORD_PATTERN.matcher(password).matches()){
            Toast.makeText(this,"Your password must:\n"+
                    "Contain at least 8 chars\n" +
                    "Contain at least one digit\n" +
                    "Contain at least one lower alpha char and one upper alpha char\n" +
                    "Contain at least one char within a set of special chars (@#%$^ etc.)",Toast.LENGTH_SHORT).show();
            //stop the function
            return;
        }

        else if (TextUtils.isEmpty(confirmPassword)){
            //confirm password field is empty
            Toast.makeText(this, "Please Confirm Your Password", Toast.LENGTH_SHORT).show();
            //stop the function
            return;
        }


        else if (!password.equals(confirmPassword)){
            Toast.makeText(this,"Passwords Not matching",Toast.LENGTH_SHORT).show();
            //stop the function
            return;
        }


        //fields are validated
        //show progress bar
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Registering User...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            //user is successfully registered
                            //open the account page to set up their profile
                            Snackbar snackbar = Snackbar.make(linearLayout, "Quickly set up your preferences in your profile and you're good to go :) ", Snackbar.LENGTH_LONG);
                            snackbar.show ();
                            finish();

                            if(email.equals("admin@restaurant.com")){
                                startActivity(new Intent(getApplicationContext(), RestaurantMain.class));
                            }else {
                                startActivity(new Intent(getApplicationContext(), Main.class));

                            }
                        }else{

                            checkUser();
                            isOnline();
                            Snackbar snackbar = Snackbar.make(linearLayout, "Registration failed. Please try again", Snackbar.LENGTH_LONG);
                            snackbar.show ();
                        }
                        progressDialog.dismiss();

                    }
                });

    }

    //check if the email already exists in the database
    public void checkUser(){
        firebaseAuth.fetchProvidersForEmail(etEmail.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {

                        boolean check = !task.getResult().getProviders().isEmpty();
                        if(!check){

                        }else{
                            Toast.makeText(Register.this, "User already exists!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

//checking for internet connectivity
public boolean isOnline() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
        return true;
    }
    else{
        Toast.makeText(Register.this, "You appear to be offline", Toast.LENGTH_SHORT).show();
    }
    return false;
    }

}