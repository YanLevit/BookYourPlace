package com.example.bookyourplace.model;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.bookyourplace.R;
import com.example.bookyourplace.model.hotel_manager.HotelManager;
import com.example.bookyourplace.model.traveler.Traveler;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class Registration extends Fragment {

    EditText inputPhone, inputSurname, inputEmail, inputPassword, inputName;
    Button btRegister;
    ImageButton btHome;
    FirebaseUser mUser;
    FirebaseAuth mAuth;
    ProgressBar progressBar_Register;
    MaterialCardView cw_chooseHotelmanager, cw_chooseTraveler;
    TextInputLayout tilEmail_Registration;
    LinearLayout ll_Password;
    FirebaseFirestore db;
    TextView tv_passwordstrength_registration;
    ProgressBar progressBar_passwordstrength_registration;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_registration, container, false);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        initializeElements(root);

        clickListeners(root);

        return root;
    }

    private void initializeElements(View root) {
        btHome = root.findViewById(R.id.bt_Backhome_Registrationton);

        cw_chooseHotelmanager = root.findViewById(R.id.choose_hotelmanager);
        cw_chooseTraveler = root.findViewById(R.id.choose_traveler);
        cw_chooseTraveler.setChecked(true);

        inputName = root.findViewById(R.id.et_name_Registaration);
        inputSurname = root.findViewById(R.id.et_Surname_Registration);

        inputPhone = root.findViewById(R.id.et_Phone_Registration);

        tilEmail_Registration = root.findViewById(R.id.tilEmail_Registration);
        inputEmail = root.findViewById(R.id.et_Email_Registration);

        ll_Password = root.findViewById(R.id.ll_Password);
        inputPassword = root.findViewById(R.id.et_Password_Registration);

        progressBar_passwordstrength_registration = root.findViewById(R.id.progressBar_PasswordStrength_Registration);
        tv_passwordstrength_registration = root.findViewById(R.id.tv_PasswordStrength_Registration);


        progressBar_Register = root.findViewById(R.id.progressBar_RegistrationUser);
        progressBar_Register.setVisibility(View.GONE);

        btRegister = root.findViewById(R.id.bt_RegistrationUser);


    }


    private void clickListeners(final View root) {

        btHome.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_registration_to_login);
        });


        btRegister.setOnClickListener(v -> verifyData());

        inputPassword.addTextChangedListener(new TextWatcher() {
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

        cw_chooseHotelmanager.setOnClickListener(v -> {
            cw_chooseHotelmanager.setChecked(true);
            cw_chooseHotelmanager.setCardBackgroundColor(Color.parseColor("#FF3F51B5"));

            cw_chooseTraveler.setChecked(false);
            cw_chooseTraveler.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        });

        cw_chooseTraveler.setOnClickListener(v -> {
            cw_chooseTraveler.setChecked(true);
            cw_chooseTraveler.setCardBackgroundColor(Color.parseColor("#FF3F51B5"));

            cw_chooseHotelmanager.setChecked(false);
            cw_chooseHotelmanager.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        });
    }

    private void verifyData() {

        String name = inputName.getText().toString().trim();
        String surname = inputSurname.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        boolean error = false;

        if (name.isEmpty()) {
            inputName.setError("Name is required");
            inputName.requestFocus();
            error = true;
        }

        if (surname.isEmpty()) {
            inputSurname.setError("Surname is required");
            inputSurname.requestFocus();
            error = true;
        }

        if (phone.isEmpty()) {
            inputPhone.setError("Phone is required or is not valid");
            inputPhone.requestFocus();
            error = true;
        }

        if (email.isEmpty()) {
            inputEmail.setError("Email address is required");
            inputEmail.requestFocus();
            error = true;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Please provide a valid email address");
            inputEmail.requestFocus();
            error = true;
        }


        if (password.isEmpty() || password.length() <= 6) {
            inputPassword.setError("Password strength error");
            inputPassword.requestFocus();
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("Password strength error");
            String mensage = "Your password needs to:" +
                    "\n\tInclude both lower and upper case characters" +
                    "\n\tInclude at least one number and symbol" +
                    "\n\tBe at least 8 characters long";

            dialog.setMessage(mensage);
            dialog.setNegativeButton("Confirm", (dialogInterface, which) -> dialogInterface.dismiss());
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
            error = true;
        }

        if (error) {
            return;
        }

        registerUser(name, surname, phone, email, password);
    }

    private void registerUser(String name, String surname, String phone, String email, String password) {
        progressBar_Register.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mUser = mAuth.getCurrentUser();
                        Toast.makeText(getContext(), "An email has been sent to activate your account!", Toast.LENGTH_LONG).show();
                        mUser.sendEmailVerification();
                        registerInFirebase(name, surname, phone, email, password, mUser.getUid());
                    } else {
                        Toast.makeText(getContext(), "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                        return;
                    }
                });
    }

    private void registerInFirebase(String name, String surname, String phone, String email, String password, String userID) {
        Object newuser = null;
        String path = "";

        if(cw_chooseTraveler.isChecked()){
            newuser = new Traveler(name, surname, email, phone, password);
            path = "Traveler";
        }

        if(cw_chooseHotelmanager.isChecked()){
            newuser = new HotelManager(name, surname, email, phone, password);
            path = "Hotel Manager";
        }

        db.collection(path).document(userID).set(newuser)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(),"User has been registered successfully!", Toast.LENGTH_LONG).show();
                    progressBar_Register.setVisibility(View.GONE);

                    Navigation.findNavController(requireView()).navigate(R.id.action_registration_to_login);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),"Failed to register! Try again!", Toast.LENGTH_LONG).show();
                    progressBar_Register.setVisibility(View.GONE);
                });

    }

    private void calculatePasswordStrength(String str) {

        PasswordStrength passwordStrength = PasswordStrength.calculate(str);
        progressBar_passwordstrength_registration.setProgressTintList(ColorStateList.valueOf(passwordStrength.getColor()));
        progressBar_passwordstrength_registration.setProgress(passwordStrength.getStrength());
        tv_passwordstrength_registration.setText(passwordStrength.getMsg());

    }
}


