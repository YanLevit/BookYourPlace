package com.example.bookyourplace;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Registration extends AppCompatActivity {

    TextInputEditText inputPhone, inputSurname,inputEmail,inputPassword, inputName;
    Button  btRegister;
    ImageButton btHome;
    FirebaseUser mUser;
    FirebaseAuth mAuth;
    String emailPattern = "[a-zA-Z0-9._]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_registration);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        inputEmail = findViewById(R.id.et_Email_Registration);
        inputPassword= findViewById(R.id.et_Password_Registration);
        inputPhone= findViewById(R.id.et_Phone_Registration);
        inputSurname= findViewById(R.id.et_Surname_Registration);
        inputName= findViewById(R.id.et_name_Registaration);
        mAuth= FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        progressDialog= new ProgressDialog(this);
        btRegister= findViewById(R.id.bt_RegistrationUser);
        btHome= findViewById(R.id.bt_Backhome_Registrationton);


        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefirRegister();
                //startActivity(new Intent(Registration.this, MainActivity.class ));
            }
        });

        btHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registration.this, MainActivity.class ));
            }
        });



    }
    private void prefirRegister() {
        String email= inputEmail.getText().toString();
        String password= inputPassword.getText().toString();

        if (!email.matches(emailPattern)){
            inputEmail.setError("Enter connext Email");
        }else if(password.isEmpty() || password.length()<6){
            inputEmail.setError("Enter proper password");
        }else{
            progressDialog.setMessage("Please wait while Registration...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();



            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                @Override
                public void onComplete(@NonNull Task<AuthResult> task){
                    if (task.isSuccessful()){
                        progressDialog.dismiss();
                        sendUserToNextActivity();
                        Toast.makeText(Registration.this,"Registration seccessful", Toast.LENGTH_SHORT).show();
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(Registration.this,""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });



        }
    }

    private void sendUserToNextActivity() {
        Intent intent = new Intent(Registration.this, MainActivity.class); /// change MAINACTIVITY to home page!!!!
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
