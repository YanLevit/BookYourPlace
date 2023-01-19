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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Login extends Fragment {

    EditText inputEmail,inputPassword;
    TextView tv_Forgot_Password;
    Button btLogin, btRegisters;
    CheckBox cb_Remeber;
    FirebaseAuth mAuth;

    String[] permissions = new String[]{ //This declares a string array that contains a list of permissions that the app will request from the user.
            // These permissions are used by the app to access specific features or data on the device.
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);//This method is called when the fragment is first created.
        // It inflates the fragment_login layout and returns the root view of the layout.


        checkPermissions(); // is used to check if the app has the required permissions.

        initializeElements(root); // is used to initialize all the UI elements.

        clickListener(root); //s used to set the click listeners on the buttons and text view.

        getRemeberData(); //is used to get the previously saved login information.

        return root; //returns the root view of the layout.
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
        //This method sets click listeners on the login button, register button, and forgot password text view.
        // When the login button is clicked, it calls the verifyData() method, when the register
        // button is clicked it navigates to the registration screen and when the forgot password text view is clicked it navigates to the forgot password screen

        btLogin.setOnClickListener(v -> verifyData());

        btRegisters.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_login_to_registration);
        });

        tv_Forgot_Password.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_login_to_forgotPassword);
        });
    }


    private void verifyData(){
        //This method is called when the login button is clicked. It gets the text from the email and password fields,
        // trims any leading/trailing whitespaces and then checks if they are empty or not. It also checks if the email is in a valid format using the EMAIL_ADDRESS pattern.
        // If any errors are found,
        // it sets the error message on the corresponding input field and returns without logging in. If there are no errors, it calls the login(email,password) method.
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
    //is used to sign in to the app using the provided email and password. It calls the signInWithEmailAndPassword method on the mAuth instance,
    // and sets a listener to check if the login is successful or not. If the login is successful and the checkbox is checked,
    // it calls the saveRemeberData(email,password) method to save the email and password. If the user's email is not verified,
    // it sends an email verification and prompts the user to check their email. If the email is verified it calls the verifyTypeUser(user.getUid())
    // method to verify the user's account type.

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
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firestore.collection("Hotel Manager").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            ////This method is used to verify the type of user. It starts by creating an instance of FirebaseFirestore
            //and creating a reference to the "Hotel Manager" collection and the document with the user's ID.
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        HotelManager manager = snapshot.toObject(HotelManager.class);
                        Toast.makeText(getContext(),"Login Successful",Toast.LENGTH_LONG).show();
                        Navigation.findNavController(getView()).navigate(R.id.action_login_to_hotel_manager_home);
                        //his line retrieves the document from the "Hotel Manager" collection. If the task is successful and the document exists,
                        // it converts the document to an object of the HotelManager class, shows a toast message, and navigates to the hotel manager home screen.
                    }else{
                        DocumentReference docRef = firestore.collection("Traveler").document(userId);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot snapshot = task.getResult();
                                    if (snapshot.exists()) {
                                        Traveler traveler = snapshot.toObject(Traveler.class);
                                        Toast.makeText(getContext(),"Login Successful",Toast.LENGTH_LONG).show();
                                        Navigation.findNavController(getView()).navigate(R.id.action_login_to_traveler_home);
                                    }
                                } else {
                                    Log.d("ERROR", "get failed with ", task.getException());//This method is used to determine whether the user is a Hotel Manager or
                                    // a Traveler and navigate them to the appropriate home screen.
                                }
                            }
                        });
                    }
                } else { // This method is used to determine whether the user is a Hotel Manager or a Traveler and navigate them to the appropriate home screen.
                    Log.d("ERROR", "get failed with ", task.getException());
                }
            }
        });
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

