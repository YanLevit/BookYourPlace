package com.example.bookyourplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextInputEditText inputEmail,inputPassword;
    Button btLogin,btRegisters;
    FirebaseUser mUser;
    FirebaseAuth mAuth;
    String emailPattern = "[a-zA-Z0-9._]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
      //  FirebaseFirestore db = FirebaseFirestore.getInstance();
        btLogin= findViewById(R.id.bt_Login);
        btRegisters= findViewById(R.id.bt_Register);
        inputEmail = findViewById(R.id.etEmail_Login);
        inputPassword= findViewById(R.id.etPassword_Login);
        mAuth= FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        progressDialog= new ProgressDialog(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        btRegisters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this , Registration.class));
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefirLogin();
            }
        });


    }

    private void prefirLogin() {
        String email= inputEmail.getText().toString();
        String password= inputPassword.getText().toString();

        if (!email.matches(emailPattern)){
            inputEmail.setError("Enter connext Email");
        }else if(password.isEmpty() || password.length()<6){
            inputEmail.setError("Enter proper password");
        }else{
            progressDialog.setMessage("Please wait while Login...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
               @Override
               public void onComplete(@NonNull Task<AuthResult> task){
                   if (task.isSuccessful()){
                       progressDialog.dismiss();
                       sendUserToNextActivity();
                       Toast.makeText(MainActivity.this,"Login seccessful", Toast.LENGTH_SHORT).show();
                   }else {
                       progressDialog.dismiss();
                       Toast.makeText(MainActivity.this,""+task.getException(), Toast.LENGTH_SHORT).show();
                   }
               }
            });



        }
    }

    private void sendUserToNextActivity() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class); /// change MAINACTIVITY to home page!!!!
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}