package com.example.dianote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText email, username, password, rePassword;
    private Button signUpButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private StorageReference userPicRef;
    String currentUserId, downloadImageUrl;
    LinearLayout signUpLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        //currentUserId = mAuth.getCurrentUser().getUid();  // this line will cause error, because no user is available and try to invoke null object
        // userRef = FirebaseDatabase.getInstance().getReference().child("UserProfile").child(currentUserId);

        email = findViewById(R.id.emailId2);
       // username = findViewById(R.id.userNameId2);
        password = findViewById(R.id.passwordId2);
        rePassword = findViewById(R.id.rePasswordId);
        signUpButton = findViewById(R.id.signUpId2);
        progressBar = findViewById(R.id.progressBarId);
        signUpLayout = (LinearLayout) findViewById(R.id.signUpLayoutId);

        password.setTransformationMethod(new PasswordTransformationMethod());  // to hide password
        rePassword.setTransformationMethod(new PasswordTransformationMethod());

        signUpButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(signUpLayout.getWindowToken(), 0); // to hide keyboard after button click

        String Email =  email.getText().toString();
        String Password = password.getText().toString();
        String RePassword = rePassword.getText().toString();
        //String userName = username.getText().toString();

        if(Email.matches(""))
        {
            Toast.makeText(SignUpActivity.this, "Enter your email", Toast.LENGTH_SHORT).show();
            return ;
        }
        else if(Password.matches(""))
        {
            Toast.makeText(SignUpActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
            return ;
        }
        else if(RePassword.matches(""))
        {
            Toast.makeText(SignUpActivity.this, "Retype password", Toast.LENGTH_SHORT).show();
            return ;
        }
        else if(!RePassword.matches(Password))
        {
            Toast.makeText(SignUpActivity.this, "Passwords not matches", Toast.LENGTH_SHORT).show();
            return ;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.INVISIBLE);
                if(task.isSuccessful()){
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                finish();
                                mAuth.signOut();  // to stop automatic login without verify
                                Toast.makeText(SignUpActivity.this, "Verify your email address and login", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(), "This email is already in use", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

   /* public void updateUserName(String userName)
    {
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("UserProfile").child(currentUserId);
        userRef.child("Name").setValue(userName);  // update user name to the database
    }*/
}

/*
finish();
                    updateUserName(userName);
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
 */


