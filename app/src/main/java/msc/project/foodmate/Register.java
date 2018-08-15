package msc.project.foodmate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

/**
 * Created by Jackie Moraa on 30/06/2018.
 */
public class Register extends AppCompatActivity implements View.OnClickListener {

    private Button bRegister;
    private EditText etEmail, etPassword, etConfirmPassword;
    private CheckBox cbRestaurant;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private LinearLayout linearLayout;

    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9+._%-+]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
                    "(" +
                    "." +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
                    ")+"
    );

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
        etEmail.addTextChangedListener(new EmailTextWatcher(etEmail));

        etPassword = (EditText) findViewById(R.id.etPassword);
        etPassword.addTextChangedListener(new PasswordTextWatcher(etPassword));

        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        etConfirmPassword.addTextChangedListener(new ConfPasswordTextWatcher(etConfirmPassword));

        cbRestaurant = (CheckBox) findViewById(R.id.cbRestaurant);
        cbRestaurant.setChecked(false);

        cbRestaurant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // update your model (or other business logic) based on isChecked
            }
        });


    }

    @Override
    public void onClick(View view) {
        if (view == bRegister){
            registerUser();
        }
    }

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
                            finish();

                            if(email.equals("admin@restaurant.com")){
                                startActivity(new Intent(getApplicationContext(), RestaurantMain.class));
                            }else {
                                startActivity(new Intent(getApplicationContext(), Main.class));
                            }
                        }else{

                            checkUser();
                            Snackbar snackbar = Snackbar.make(linearLayout, "Registration failed. Please try again", Snackbar.LENGTH_LONG);
                            snackbar.show ();
                        }
                        progressDialog.dismiss();

                    }
                });

    }

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

    /**
     * TextWatcher - set the fonts for edit texts with hints
     * 1. email
     * 2. password
     * 3. confirm password
     */
    public class EmailTextWatcher implements TextWatcher {
        public EmailTextWatcher(EditText e) {
            etEmail = e;
            etEmail.setTypeface(Typeface.SERIF);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            etEmail.setTypeface(Typeface.SERIF);
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            etEmail.setTypeface(Typeface.SERIF);
        }

        public void afterTextChanged(Editable s) {
            if(s.length() == 0){
                etEmail.setTypeface(Typeface.SERIF);
            } else {
                etEmail.setTypeface(Typeface.SERIF);
            }

        }

    }

    public class PasswordTextWatcher implements TextWatcher {
        public PasswordTextWatcher(EditText p) {
            etPassword = p;
            etPassword.setTypeface(Typeface.SERIF);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            etPassword.setTypeface(Typeface.SERIF);
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            etPassword.setTypeface(Typeface.SERIF);
        }

        public void afterTextChanged(Editable s) {
            if(s.length() == 0){
                etPassword.setTypeface(Typeface.SERIF);
            } else {
                etPassword.setTypeface(Typeface.SERIF);
            }

        }

    }

    public class ConfPasswordTextWatcher implements TextWatcher {
        public ConfPasswordTextWatcher(EditText cp) {
            etConfirmPassword = cp;
            etConfirmPassword.setTypeface(Typeface.SERIF);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            etConfirmPassword.setTypeface(Typeface.SERIF);
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            etConfirmPassword.setTypeface(Typeface.SERIF);
        }

        public void afterTextChanged(Editable s) {
            if(s.length() == 0){
                etConfirmPassword.setTypeface(Typeface.SERIF);
            } else {
                etConfirmPassword.setTypeface(Typeface.SERIF);
            }

        }


    }


//checking for internet connectivity... *Incomplete - toast of there is no internet
    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();

    }


}