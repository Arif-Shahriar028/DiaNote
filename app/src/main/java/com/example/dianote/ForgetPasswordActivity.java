package com.example.dianote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText email;
    private Button recoverPassword;
    private ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        email = (EditText) findViewById(R.id.emailId3);
        recoverPassword = (Button) findViewById(R.id.recoverPasswordId);
        progressBar = (ProgressBar) findViewById(R.id.progressBarId4);

        mAuth = FirebaseAuth.getInstance();

        recoverPassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        String mail = email.getText().toString();
        if(mail.matches(""))
        {
            Toast.makeText(ForgetPasswordActivity.this, "Enter your email address", Toast.LENGTH_SHORT).show();
           return;
        }
        else if(view.getId() == R.id.recoverPasswordId)
        {
            progressBar.setVisibility(View.VISIBLE);

            mAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBar.setVisibility(View.GONE);
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ForgetPasswordActivity.this, "Recovery email sent", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(ForgetPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}