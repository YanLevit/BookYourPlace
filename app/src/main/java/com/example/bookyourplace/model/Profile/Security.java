package com.example.bookyourplace.model.Profile;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.bookyourplace.R;
import com.example.bookyourplace.model.InternalStorage;
import com.example.bookyourplace.model.PasswordStrength;
import com.example.bookyourplace.model.User;
import com.example.bookyourplace.model.hotel_manager.HotelManager;
import com.example.bookyourplace.model.traveler.Traveler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

public class Security extends Fragment {


    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    DocumentReference documentReference;

    User user;
    String path;
    LinearLayout ll_ChangePassword;
    EditText et_Old_Password;
    EditText et_New_Password;
    TextView tv_passwordstrength_change;
    ProgressBar progressBar_passwordstrength_change;
    Button bt_reset_password;
    Button bt_delete_account;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile_security, container, false);


        readUserData();

        initializeElements(root);

        clickListeners(root);

        return root;
    }


    private void readUserData() {
        try {
            user = (User) InternalStorage.readObject(getContext(), "User");
            if(user instanceof Traveler){
                path = "Traveler";
            }

            if(user instanceof HotelManager){
                path = "Hotel Manager";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void initializeElements(View root) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        documentReference = db.collection(path).document(firebaseUser.getUid());

        ll_ChangePassword = root.findViewById(R.id.ll_ChangePassword);

        et_Old_Password = root.findViewById(R.id.et_Old_Password);
        et_New_Password = root.findViewById(R.id.et_New_Password);

        progressBar_passwordstrength_change = root.findViewById(R.id.progressBar_passwordstrength_change);
        tv_passwordstrength_change = root.findViewById(R.id.tv_passwordstrength_change);

        bt_reset_password = root.findViewById(R.id.bt_reset_password);


        bt_delete_account = root.findViewById(R.id.bt_delete_account);

    }


    private void clickListeners(View root) {
        et_New_Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculatePasswordStrength(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        bt_reset_password.setOnClickListener(v -> {
            String password = et_New_Password.getText().toString().trim();
            PasswordStrength passwordStrength = PasswordStrength.calculate(password);
            if(password.isEmpty() || passwordStrength.getStrength() <= 1){
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("Password Strength Error");
                String mensage = "Your password needs to:" +
                        "\n\tInclude both lower and upper case characters" +
                        "\n\tInclude at least one number and symbol" +
                        "\n\tBe at least 8 characters long";

                dialog.setMessage(mensage);
                dialog.setNegativeButton("Confirm", (dialogInterface, which) -> dialogInterface.dismiss());
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
            else{
                if(! user.getPassword().equals(et_Old_Password.getText().toString())){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle("Password Error");
                    String mensage = "Old password is incorrect, please enter your current password";

                    dialog.setMessage(mensage);
                    dialog.setNegativeButton("Confirm", (dialogInterface, which) -> dialogInterface.dismiss());
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                    et_Old_Password.setError("Password is incorrect");
                }
                else {
                    user.setPassword(password);
                    documentReference.set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG", "User data updated successfully in Firestore");
                                    firebaseUser.updatePassword(password)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("TAG", "User password updated successfully in Firebase Authentication");
                                                        Toast.makeText(getContext(),"Password changed successfully!", Toast.LENGTH_LONG).show();
                                                        et_Old_Password.setText("");
                                                        et_New_Password.setText("");
                                                        calculatePasswordStrength("");
                                                    } else {
                                                        Log.d("TAG", "Error updating password in Firebase Authentication: " + task.getException());
                                                    }
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG", "User data update failed: " + e.getMessage());
                                }
                            });
                }

            }
        });

        bt_delete_account.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("Are you sure?");
            dialog.setMessage("Deleting this account will result in completely removing your account and you won't be able to access the app again.");
            dialog.setPositiveButton("Delete", (dialogInterface, which) -> {
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "User account deleted successfully from Firebase Firestore.");
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("TAG", "User account deleted from Firebase Authentication.");
                                  /* Navigation.findNavController(root).navigate(R.id.action_security_to_login);*/
                                   Navigation.findNavController(root).navigate(R.id.action_profile_to_login);
                                } else {
                                    Log.d("TAG", "User account deletion failed from Firebase Authentication: " + task.getException());
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "User account deletion failed from Firebase Firestore: " + e.getMessage());
                    }
                });

            });
            dialog.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss());
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        });

    }

    private void calculatePasswordStrength(String str) {
        // Now, we need to define a PasswordStrength enum
        // with a calculate static method returning the password strength
        PasswordStrength passwordStrength = PasswordStrength.calculate(str);

        progressBar_passwordstrength_change.setProgressTintList(ColorStateList.valueOf(passwordStrength.getColor()));
        progressBar_passwordstrength_change.setProgress(passwordStrength.getStrength());
        tv_passwordstrength_change.setText(passwordStrength.getMsg());

    }

}
