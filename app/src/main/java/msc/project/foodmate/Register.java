package msc.project.foodmate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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


    }

    @Override
    public void onClick(View view) {
        if (view == bRegister){
            registerUser();
        }
    }

    private void registerUser(){

        String email = etEmail.getText().toString().trim();
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
                            startActivity(new Intent(getApplicationContext(), Main.class));
                        }else{
                            Toast.makeText(Register.this, "Registration failed. Please try again", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();

                    }
                });

    }


}