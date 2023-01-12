package com.example.bookyourplace.model.login;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.bookyourplace.R;
import com.example.bookyourplace.model.hotel_manager.HotelManager;
import com.example.bookyourplace.model.InternalStorage;
import com.example.bookyourplace.model.traveler.Traveler;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Login extends Fragment {
    EditText inputEmail,inputPassword;
    GoogleSignInClient googleSignInClient;
    TextView tv_Forgot_Password;
    Button btLogin, btRegisters;
    CheckBox cb_Remeber;
    //    FirebaseUser mUser;
    FirebaseAuth mAuth;
    //String emailPattern = "[a-zA-Z0-9._]+@[a-z]+\\.+[a-z]+";
    //ProgressDialog progressDialog;
    Traveler traveler;
    //HotelManager manager;
    final boolean autoLogin = true;

    String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        checkPermissions();

        initializeElements(root);

        clickListener(root);

        getRemeberData();

        return root;
    }

    private void initializeElements(View root) {
        inputEmail =  root.findViewById(R.id.etEmail_Login);
        inputPassword =  root.findViewById(R.id.etPassword_Login);

        cb_Remeber = root.findViewById(R.id.cb_Remember);

        btLogin =  root.findViewById(R.id.bt_Login);

        tv_Forgot_Password =  root.findViewById(R.id.tv_Forgot_Password);

        btRegisters =  root.findViewById(R.id.bt_Register);

        mAuth = FirebaseAuth.getInstance();
    }

    private void clickListener(View root) {

        btLogin.setOnClickListener(v -> verifyData());

        btRegisters.setOnClickListener(v -> {
            Bundle bundle = new Bundle();

            bundle.putBoolean("IsGoogle", false);
            Navigation.findNavController(root).navigate(R.id.action_login_to_registration,bundle);
        });

        tv_Forgot_Password.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_login_to_forgotPassword);
        });
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), task -> {
            if (task.isSuccessful()) {
                boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                if (isNewUser) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("IsGoogle", true);
                    Navigation.findNavController(getView()).navigate(R.id.action_login_to_registration, bundle);
                } else {
                    verifyTypeUser(mAuth.getCurrentUser().getUid());
                }
            } else {
                // If sign in fails, display a message to the user.
                Log.w("TAG", "signInWithCredential:failure", task.getException());
            }
        });
    }


    private void verifyData(){
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        boolean error = false;

        if(email.isEmpty()){
            inputEmail.setError("Email Address is required");
            inputEmail.requestFocus();
            error = true;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            inputEmail.setError("Please provide valid Email Address");
            inputEmail.requestFocus();
            error = true;
        }

        if(password.isEmpty()){
            inputPassword.setError("Password is required");
            inputPassword.requestFocus();
            error = true;
        }

        if(error){
            return;
        }


        login(email,password);
    }

    private void login(String email, String password) {

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                btLogin.setEnabled(false);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(cb_Remeber.isChecked()){
                    saveRemeberData(email,password);
                }
                else{
                    saveRemeberData("","");
                }

                if(user.isEmailVerified()){
                    verifyTypeUser(user.getUid());

                }
                else{
                    user.sendEmailVerification();
                    Toast.makeText(getContext(),"Check your email to verify your account!", Toast.LENGTH_LONG).show();
                    btLogin.setEnabled(true);
                }
            }
            else {
                Toast.makeText(getContext(),"Failed to login! Please check your credentials",Toast.LENGTH_LONG).show();
                btLogin.setEnabled(true);
            }
        });
    }

    private void verifyTypeUser(String userId){

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();


        database.child("Traveler").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                traveler = snapshot.getValue(Traveler.class);
                if(traveler != null){
                    Toast.makeText(getContext(),"Login Successful",Toast.LENGTH_LONG).show();
                     Navigation.findNavController(getView()).navigate(R.id.action_login_to_traveler_home);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR", "getTraveler:onCancelled",error.toException());
            }
        });

//        database.child("Hotel Manager").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                manager = snapshot.getValue(HotelManager.class);
//                if(manager != null){
//                    Toast.makeText(getContext(),"Login Successful",Toast.LENGTH_LONG).show();
//                    //Navigation.findNavController(getView()).navigate(R.id.action_login_to_hotel_manager_home);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("ERROR", "getManager:onCancelled",error.toException());
//            }
//        });
    }


    private void saveRemeberData(String email, String password){
        try {
            InternalStorage.writeObject(getContext(), "Email", email);
            InternalStorage.writeObject(getContext(), "Password", password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getRemeberData() {
        try {
            String email = (String) InternalStorage.readObject(getContext(), "Email");
            String password = (String) InternalStorage.readObject(getContext(), "Password");

            if(!email.isEmpty() && !password.isEmpty()){
                inputEmail.setText(email);
                inputPassword.setText(password);

                cb_Remeber.setChecked(true);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 124);
            return false;
        }
        return true;
    }






















//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case 124: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // permissions granted.
//                    Log.e("Permissions Garated", "All"); // Now you call here what ever you want :)
//                } else {
//                    String perStr = "";
//                    for (String per : permissions) {
//                        perStr += "\n" + per;
//                    }   // permissions list of don't granted permission
//                    Log.e("Permissions Denied", perStr);
//                }
//                return;
//            }
//        }
//    }
}

