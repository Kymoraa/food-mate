package msc.project.foodmate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;


public class Login extends AppCompatActivity implements View.OnClickListener{

    private Button bSignin;
    private EditText etEmail, etPassword;
    private TextView tvRegister, tvGuest;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        linearLayout = findViewById(R.id.linearLayout);

        firebaseAuth = FirebaseAuth.getInstance();

        //check if user is already logged in
        if (firebaseAuth.getCurrentUser() != null){
            //user is already logged in. start main activity
            finish();
            if(firebaseAuth.getCurrentUser().getEmail().equals("admin@restaurant.com")){
                startActivity(new Intent(getApplicationContext(), RestaurantMain.class));
            }else {
                startActivity(new Intent(getApplicationContext(), Main.class));
            }
        }


        progressDialog = new ProgressDialog(this);

        bSignin = (Button) findViewById(R.id.bSignin);
        bSignin.setOnClickListener(this);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etEmail.addTextChangedListener(new EmailTextWatcher(etEmail));

        etPassword = (EditText) findViewById(R.id.etPassword);
        etPassword.addTextChangedListener(new PasswordTextWatcher(etPassword));


        tvRegister = (TextView) findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(this);

        tvGuest = (TextView) findViewById(R.id.tvGuest);
        tvGuest.setOnClickListener(this);

    }

    /**
     * clicking the sign in button
     * clicking the register, guest links
     * @param view
     */

    @Override
    public void onClick(View view) {
        if (view == bSignin){

            loginUser();
        }
        if (view == tvRegister){
            //open the activity to register a new user
            finish();
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        }

        if (view == tvGuest){
            //open the activity to proceed as guest
        }

    }

    //method to log in the users already registered
    private void loginUser(){
        final String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

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

        //fields are validated
        //show progress bar
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Logging in...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()){
                            //user is successfully registered
                            finish();
                            if(email.equals("admin@restaurant.com")){
                                startActivity(new Intent(getApplicationContext(), RestaurantMain.class));
                            }else {
                                startActivity(new Intent(getApplicationContext(), Main.class));
                            }
                        }else{
                            //Toast.makeText(Login.this, "Login failed. Please try again", Toast.LENGTH_SHORT).show();
                            Snackbar snackbar = Snackbar.make(linearLayout, "Login failed. Please try again", Snackbar.LENGTH_LONG);
                            snackbar.show ();
                        }

                    }
                });

    }

    /**
     * TextWatcher - set the fonts for edit texts with hints
     * 1. email
     * 2. password
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


}
