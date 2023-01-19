package com.example.bookyourplace.model.login;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.bookyourplace.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends Fragment {
    private FirebaseAuth firebaseAuth;

    private EditText et_EmailAddress;

    private Button bt_Reset;
    private ImageButton bt_Backhome_reset;

    private ProgressBar progressBar_Reset;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_forgot_password, container, false);//This line inflates the layout file for the fragment and returns it as a view.


        initializeElements(root); //This line calls the initializeElements(root) method to initialize the elements in the layout.

        clickListener(root); //This line calls the clickListener(root) method to set onClickListeners for the buttons.


        return root;
    }

    private void initializeElements(View root) { //This method initializes the elements by connecting them to the corresponding views in the layout file.
        bt_Backhome_reset =  root.findViewById(R.id.bt_Backhome_reset);

        et_EmailAddress =  root.findViewById(R.id.et_Reset_Email);

        progressBar_Reset =  root.findViewById(R.id.progressBar_ResetEmail);
        progressBar_Reset.setVisibility(View.GONE);

        bt_Reset =  root.findViewById(R.id.bt_ResetPassword);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void clickListener(final View root) { //This method sets onClickListeners for the buttons. When the reset password button is clicked,
        // it calls the resetPassword(root) method. When the back button is clicked, it navigates the user back to the login page.
        bt_Reset.setOnClickListener(v -> resetPassword(root));

        bt_Backhome_reset.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_forgotPassword_to_login);
        });
    }



    private void resetPassword(View root) {// This method is called when the user clicks the reset password button.
        // It takes the email address entered by the user and sends a password reset email using the sendPasswordResetEmail method of FirebaseAuth.
        // If the task is successful, it shows a toast message and navigates the user back to the login page. If not, it shows an error message.
        String email = et_EmailAddress.getText().toString().trim();
        boolean error = false;

        if(email.isEmpty()){
            et_EmailAddress.setError("Email Address is required");
            et_EmailAddress.requestFocus();
            error = true;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_EmailAddress.setError("Please provide valid Email Address");
            et_EmailAddress.requestFocus();
            error = true;
        }

        if(error){
            return;
        }

        progressBar_Reset.setVisibility(View.VISIBLE);

        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> { //This code is using the sendPasswordResetEmail() method of the
            // FirebaseAuth instance to send a password reset email to the email address that the user entered.
            if(task.isSuccessful()){
                Toast.makeText(getContext(),"Check your email to reset your password!", Toast.LENGTH_LONG).show();

                Navigation.findNavController(root).navigate(R.id.action_forgotPassword_to_login);
            }
            else {
                Toast.makeText(getContext(),"Try again! Something wrong happened", Toast.LENGTH_LONG).show();
            }
        });
    }
}
