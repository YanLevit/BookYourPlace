package com.example.bookyourplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookyourplace.model.HotelManager;
import com.example.bookyourplace.model.InternalStorage;
import com.example.bookyourplace.model.Traveler;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
  /*  @Override
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
    }*/


}