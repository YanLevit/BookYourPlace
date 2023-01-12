package com.example.bookyourplace;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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