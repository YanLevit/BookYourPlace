package com.example.bookyourplace.model;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.bookyourplace.R;
import com.example.bookyourplace.model.HotelManager;
import com.example.bookyourplace.model.Traveler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class Registration extends Fragment {

    EditText inputPhone, inputSurname,inputEmail,inputPassword, inputName;
    Button  btRegister;
    ImageButton btHome;
    FirebaseUser mUser;
    FirebaseAuth mAuth;
    ProgressBar progressBar_Register;
    String emailPattern = "[a-zA-Z0-9._]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    MaterialCardView cw_chooseHotelmanager, cw_chooseTraveler;
    TextInputLayout tilEmail_Registration;
    LinearLayout ll_Password;
    TextView tv_passwordstrength_registration;
    ProgressBar progressBar_passwordstrength_registration;
    boolean googleRegistration;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_registration, container, false);

        mAuth = FirebaseAuth.getInstance();

        initializeElements(root);

        clickListeners(root);

        return root;
    }

    private void initializeElements(View root) {
        btHome =  root.findViewById(R.id.bt_Backhome_Registrationton);

        cw_chooseHotelmanager = root.findViewById(R.id.choose_hotelmanager);
        cw_chooseTraveler = root.findViewById(R.id.choose_traveler);
        cw_chooseTraveler.setChecked(true);

        inputName = root.findViewById(R.id.et_name_Registaration);
        inputSurname = root.findViewById(R.id.et_Surname_Registration);

        inputPhone =  root.findViewById(R.id.et_Phone_Registration);

        tilEmail_Registration = root.findViewById(R.id.tilEmail_Registration);
        inputEmail = root.findViewById(R.id.et_Email_Registration);

        ll_Password  = root.findViewById(R.id.ll_Password);
        inputPassword = root.findViewById(R.id.et_Password_Registration);

        progressBar_passwordstrength_registration = root.findViewById(R.id.progressBar_PasswordStrength_Registration);
        tv_passwordstrength_registration = root.findViewById(R.id.tv_PasswordStrength_Registration);


        progressBar_Register =  root.findViewById(R.id.progressBar_RegistrationUser);
        progressBar_Register.setVisibility(View.GONE);

        btRegister =  root.findViewById(R.id.bt_RegistrationUser);

       // verifyIsGoogle();
    }

//    private void verifyIsGoogle() {
//        if(!getArguments().isEmpty()){
//            googleRegistration = getArguments().getBoolean("IsGoogle",false);
//        }
//        else{
//            googleRegistration = false;
//        }
//
//        if(googleRegistration){
//            tilEmail_Registration.setVisibility(View.GONE);
//            ll_Password.setVisibility(View.GONE);
//        }
//    }

    private void clickListeners(final View root) {

        btHome.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_registration_to_login);
        });


        btRegister.setOnClickListener(v -> verifyData());

//        inputPassword.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                calculatePasswordStrength(s.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
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

    private void verifyData(){

        String name = inputName.getText().toString().trim();
        String surname = inputSurname.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        boolean error = false;

        if(name.isEmpty()){
            inputName.setError("Name is required");
            inputName.requestFocus();
            error = true;
        }

        if(surname.isEmpty()){
            inputSurname.setError("Surname is required");
            inputSurname.requestFocus();
            error = true;
        }

       /* if(inputPhone.){
            inputPhone.setError("Phone is required or is not valid");
            inputPhone.requestFocus();
            error = true;
        }*/

        if(!googleRegistration){
            if(email.isEmpty()){
                inputEmail.setError("Email address is required");
                inputEmail.requestFocus();
                error = true;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                inputEmail.setError("Please provide a valid email address");
                inputEmail.requestFocus();
                error = true;
            }


            if(password.isEmpty() || password.length() <= 6){
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

        }
//        else{
//            email = mAuth.getCurrentUser().getEmail();
//        }

        if(error){
            return;
        }

        registerUser(name,surname,phone,email,password);
    }

    private void registerUser(String name, String surname, String phone, String email, String password) {
        progressBar_Register.setVisibility(View.VISIBLE);
        if(!googleRegistration){
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            mUser = mAuth.getCurrentUser();
                            Toast.makeText(getContext(),"An email has been sent to activate your account!", Toast.LENGTH_LONG).show();
                            mUser.sendEmailVerification();
                            Navigation.findNavController(getView()).navigate(R.id.action_registration_to_login);
                            registerInFirebase(name,surname,phone,email,password,mUser.getUid());
                        }
                        else {
                            Toast.makeText(getContext(),"Failed to register! Try again!", Toast.LENGTH_LONG).show();
                            return;
                        }
                    });
        }
       /* else {
            mUser = mAuth.getCurrentUser();
            registerInFirebase(name,surname,phone,email,password,mUser.getUid());
        }*/
//        mUser = mAuth.getCurrentUser();
//        registerInFirebase(name,surname,phone,email,password,mUser.getUid());
    }

    private void registerInFirebase(String name, String surname, String phone, String email, String password, String userID) {
        Object newuser = null;
        String path = "";

        if(cw_chooseTraveler.isChecked()){
            newuser = googleRegistration ?  new Traveler(name, surname, phone, email,true) : new Traveler(name, surname, email, phone, password);
            path = "Traveler";
        }

        if(cw_chooseHotelmanager.isChecked()){
            newuser = googleRegistration ?   new HotelManager(name, surname, phone, email,true) :  new HotelManager(name, surname, email, phone, password);
            path = "Hotel Manager";
        }

        FirebaseDatabase.getInstance().getReference(path)
                .child(userID)
                .setValue(newuser).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(),"User has ben registered successfully!", Toast.LENGTH_LONG).show();
                        progressBar_Register.setVisibility(View.GONE);

                        FirebaseAuth.getInstance().signOut();
                        Navigation.findNavController(getView()).navigate(R.id.action_registration_to_login);
                    }
                    else {
                        Toast.makeText(getContext(),"Failed to register! Try again!", Toast.LENGTH_LONG).show();
                        progressBar_Register.setVisibility(View.GONE);
                    }
                });
    }

//    private void calculatePasswordStrength(String str) {
//        PasswordStrength passwordStrength = PasswordStrength.calculate(str);
//
//        progressBar_passwordstrength_registration.setProgressTintList(ColorStateList.valueOf(passwordStrength.getColor()));
//        progressBar_passwordstrength_registration.setProgress(passwordStrength.getStrength());
//        tv_passwordstrength_registration.setText(passwordStrength.getMsg());
//
//    }
//    @Override
//
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_registration);
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        inputEmail = findViewById(R.id.et_Email_Registration);
//        inputPassword= findViewById(R.id.et_Password_Registration);
//        inputPhone= findViewById(R.id.et_Phone_Registration);
//        inputSurname= findViewById(R.id.et_Surname_Registration);
//        inputName= findViewById(R.id.et_name_Registaration);
//        mAuth= FirebaseAuth.getInstance();
//        mUser = mAuth.getCurrentUser();
//        progressDialog= new ProgressDialog(this);
//        btRegister= findViewById(R.id.bt_RegistrationUser);
//        btHome= findViewById(R.id.bt_Backhome_Registrationton);
//
//
//        btRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                prefirRegister();
//                //startActivity(new Intent(Registration.this, MainActivity.class ));
//            }
//        });
//
//        btHome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(Registration.this, MainActivity.class ));
//            }
//        });
//
//
//
//    }
//    private void prefirRegister() {
//        String email= inputEmail.getText().toString();
//        String password= inputPassword.getText().toString();
//
//        if (!email.matches(emailPattern)){
//            inputEmail.setError("Enter connext Email");
//        }else if(password.isEmpty() || password.length()<6){
//            inputEmail.setError("Enter proper password");
//        }else{
//            progressDialog.setMessage("Please wait while Registration...");
//            progressDialog.setTitle("Registration");
//            progressDialog.setCanceledOnTouchOutside(false);
//            progressDialog.show();
//
//
//
//            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task){
//                    if (task.isSuccessful()){
//                        progressDialog.dismiss();
//                        sendUserToNextActivity();
//                        Toast.makeText(Registration.this,"Registration seccessful", Toast.LENGTH_SHORT).show();
//                    }else {
//                        progressDialog.dismiss();
//                        Toast.makeText(Registration.this,""+task.getException(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//
//
//
//        }
//    }
//
//    private void sendUserToNextActivity() {
//        Intent intent = new Intent(Registration.this, MainActivity.class); /// change MAINACTIVITY to home page!!!!
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }
}
