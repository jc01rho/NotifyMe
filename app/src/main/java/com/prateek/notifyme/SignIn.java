package com.prateek.notifyme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//TODO: UX corrections - Issue: form hides signup button
//TODO: UX corrections - Issue: when keyboards next tapped on SignUp, app navigates to SignIn. It should perform sign up action
//TODO: UX - Question: Should the form retain user input when we switch between Signin / Signup?
public class SignIn extends AppCompatActivity {

    //Declarations
    public static FirebaseAuth mAuth;
    private Button bt_sign_in, bt_sign_up;
    private EditText et_username, et_password, et_confirm_pass;
    private TextView tv_banner;
    private static final String TAG = "DebugMsg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        //Definitions
        bt_sign_in = (Button) findViewById(R.id.bt_signin);
        bt_sign_up = (Button) findViewById(R.id.bt_signup);

        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        et_confirm_pass = (EditText) findViewById(R.id.et_confirm_pass);

        tv_banner = (TextView) findViewById(R.id.tv_banner);

        tv_banner.setText(R.string.sign_in);
        et_confirm_pass.setVisibility(View.INVISIBLE);

        View.OnClickListener btListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()) {
                    case  R.id.bt_signin: {
                        // goto MainActivity

                        if (et_confirm_pass.getVisibility() == View.VISIBLE) {

                            tv_banner.setText(R.string.sign_in);
                            et_confirm_pass.setVisibility(View.INVISIBLE);
                        }else{
                            //TODO: Set null checks and messages
                            if(!et_username.getText().toString().isEmpty() && !et_password.getText().toString().isEmpty())
                                signIn(et_username.getText().toString(), et_password.getText().toString());
//                            startActivity(new Intent(SignIn.this, MainActivity.class));
                        }
                        break;
                    }


                    case R.id.bt_signup: {
                        // change UI to SignUp

                        if (et_confirm_pass.getVisibility() == View.VISIBLE){
                            //Call service to signUp and (default login?) and take to MainActivity
                            System.out.println("#######");
//                            System.out.println(et_password.getText().toString());
//                            System.out.println(et_confirm_pass.getText().toString());
                            Log.d(TAG, "pass: "+ et_password.getText().toString());
                            Log.d(TAG, "cnf pass: "+et_confirm_pass.getText().toString());
                            if (et_confirm_pass.getText().toString().equals(et_password.getText().toString())) {
                                if (!et_username.getText().toString().isEmpty() && !et_password.getText().toString().isEmpty())
                                    createAccount(et_username.getText().toString(), et_password.getText().toString());
                                //                            startActivity(new Intent(SignIn.this, MainActivity.class));
                            } else {
                                Toast.makeText(SignIn.this, "Password & Confirm Password do not match", Toast.LENGTH_SHORT).show();
                            }
                        }else{

                            tv_banner.setText(R.string.sign_up);
                            et_confirm_pass.setVisibility(View.VISIBLE);
                        }
                        break;
                    }
                }
            }
        };

        bt_sign_in.setOnClickListener(btListener);
        bt_sign_up.setOnClickListener(btListener);
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            System.out.println("$$$ User created");
                            Toast.makeText(SignIn.this, "Sign Up Successful",
                                    Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //TODO: Add better custom validation messages in Toast
                            System.out.println("%%% Error: User creation");
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignIn.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        System.out.println("$$$ User Signed in");
                        Toast.makeText(SignIn.this, "Sign In Successful",
                                Toast.LENGTH_LONG).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        //TODO: Add better custom validation messages in Toast
                        System.out.println("%%%  Error: User sign in");
                        Toast.makeText(SignIn.this, task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                    // ...
                }
            });
    }



    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            //TODO: Send to dashboard page
            System.out.println("### User logged in!!");
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }
        else
            System.out.println("### Need to sign in / sign up!!");
    }

    @Override
    public void onBackPressed() {
        if (et_confirm_pass.getVisibility() == View.VISIBLE) {
            tv_banner.setText(R.string.sign_in);
            et_confirm_pass.setVisibility(View.INVISIBLE);
        }else{
            super.onBackPressed();
            finish();
            overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
        }

    }
}
