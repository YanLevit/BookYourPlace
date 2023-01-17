package com.example.bookyourplace.model.Profile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bookyourplace.R;
import com.example.bookyourplace.model.InternalStorage;
import com.example.bookyourplace.model.User;
import com.example.bookyourplace.model.hotel_manager.HotelManager;
import com.example.bookyourplace.model.traveler.Traveler;
import com.example.bookyourplace.model.Address;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Calendar;

public class PersonalData extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    DocumentReference documentReference;

    User user;
    String path;
    EditText et_Name, et_Surname, et_Age,et_EmailAddress;
    EditText et_City,et_Address,et_ZipCode;
    EditText et_Phone;
    Button bt_save_preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile_personal_data, container, false);

        readUserData();

        initializeElements(root);

        loadDatatoElements();

        clickListeners();

        return root;
    }

    private void initializeElements(View root){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        documentReference = db.collection(path).document(firebaseUser.getUid());


        et_Name = root.findViewById(R.id.et_Name);
        et_Surname = root.findViewById(R.id.et_Surname);
        et_Age = root.findViewById(R.id.et_Age);
        et_EmailAddress = root.findViewById(R.id.et_Email);

        et_Phone = root.findViewById(R.id.et_Phone);

        et_City = root.findViewById(R.id.et_City);
        et_Address = root.findViewById(R.id.et_Address);
        et_ZipCode = root.findViewById(R.id.et_ZipCode);

        bt_save_preferences = root.findViewById(R.id.bt_save_preferences);
    }


    private void clickListeners() {
        et_Age.setOnClickListener(v -> {
            final DatePickerDialog[] picker = new DatePickerDialog[1];
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            picker[0] = new DatePickerDialog(getContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        Calendar currentdate = Calendar.getInstance();
                        Calendar birthdaydate = Calendar.getInstance();
                        birthdaydate.set(year1, monthOfYear, dayOfMonth);

                        int age = currentdate.get(Calendar.YEAR) - birthdaydate.get(Calendar.YEAR);
                        if (currentdate.get(Calendar.DAY_OF_YEAR) < birthdaydate.get(Calendar.DAY_OF_YEAR)) {
                            age--;
                        }

                        if (age < 18) {
                            Toast.makeText(getContext(), "Invalid birth date, must be over 18 years old. Please enter a valid date", Toast.LENGTH_LONG).show();
                            et_Age.setText("");
                        } else {
                            et_Age.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
                        }
                    }, year, month, day);
            picker[0].show();
        });

        bt_save_preferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePersonalData();
            }
        });

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

    private void loadDatatoElements(){
        et_Name.setText(user.getName());
        et_Surname.setText(user.getSurname());
        et_Age.setText(user.getBirthday());
        et_EmailAddress.setText(user.getEmail());
        et_Phone.setText(user.getPhone());

        if(user.getAddress() != null){
            et_City.setText(user.getAddress().getCity());
            et_Address.setText(user.getAddress().getAddress());
            et_ZipCode.setText(user.getAddress().getZipcode());
           ;
        }

    }



    private void savePersonalData(){
        String name = et_Name.getText().toString().trim();
        String surname = et_Surname.getText().toString().trim();
        String age = et_Age.getText().toString().trim();
        String email = et_EmailAddress.getText().toString().trim();
        String phone = et_Phone.getText().toString().trim();
        String city = et_City.getText().toString().trim();
        String address = et_Address.getText().toString().trim();
        String zipcode = et_ZipCode.getText().toString().trim();

        boolean error = false;

        if(name.isEmpty()){
            et_Name.setError("Full name is required");
            et_Name.requestFocus();
            error = true;
        }

        if(surname.isEmpty()){
            et_Surname.setError("Full name is required");
            et_Surname.requestFocus();
            error = true;
        }

        if(age.isEmpty()){
            et_Age.setError("Age is required");
            et_Age.requestFocus();
            error = true;
        }


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

        if(phone.isEmpty()){
            et_Phone.setError("Phone is required");
            et_Phone.requestFocus();
            error = true;
        }

        if(city.isEmpty()){
            et_City.setError("City is required");
            et_City.requestFocus();
            error = true;
        }

        if(address.isEmpty()){
            et_Address.setError("Address is required");
            et_Address.requestFocus();
            error = true;
        }

        if(error){
            return;
        }

        user.setName(name);
        user.setSurname(surname);
        user.setBirthday(age);
        user.setEmail(email);
        user.setPhone(phone);

        Address newaddress = new Address();
        newaddress.setCity(city);
        newaddress.setAddress(address);
        newaddress.setZipcode(zipcode);

        user.setAddress(newaddress);

        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "User data updated successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "User data update failed: " + e.getMessage());
            }
        });


    }


}
