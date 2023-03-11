package com.example.bookyourplace.model.traveler;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

//import com.bumptech.glide.Glide;
import com.bumptech.glide.Glide;
import com.example.bookyourplace.R;
import com.example.bookyourplace.model.InternalStorage;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
//import com.google.type.LatLng;

import java.io.IOException;

public class Home extends Fragment {

    FirebaseUser mUser;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    Traveler user;

     FusedLocationProviderClient fusedLocationProviderClient;
    ConstraintLayout cl_HomeUser;
    ShapeableImageView bt_ProfileMenu;
    LinearLayout profileMenu;
    Button bt_EditProfile, bt_Logout;
    TextView tv_NameMensage;
    FloatingActionButton search_btn;
    TextView textinput_location;
    MaterialButton bt_search_onMap;
    ExtendedFloatingActionButton bookings_btn;
    FloatingActionButton favsBtn;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {

            @Override
            public void handleOnBackPressed() {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.logout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                dialog.setCancelable(false);

                dialog.create();

                Button confirm = dialog.findViewById(R.id.bt_dialog_logout_Confirm);
                Button deny = dialog.findViewById(R.id.bt_dialog_logout_Deny);

                confirm.setOnClickListener(v -> {bt_Logout.performClick(); dialog.dismiss();});

                deny.setOnClickListener(v -> dialog.dismiss());

                dialog.show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.traveler_fragment_home, container, false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        readUserData();

        initializeElements(root);

        //getLocation();

        clickListener(root);

        loadDatatoElements();

        return root;
    }


    private void initializeElements(View root) {
        bt_ProfileMenu = root.findViewById(R.id.bt_ProfileMenu);

        profileMenu = root.findViewById(R.id.ll_profile_menu_User);
        profileMenu.setVisibility(View.GONE);

        bt_EditProfile = root.findViewById(R.id.bt_editProfile_User);

        bt_Logout = root.findViewById(R.id.bt_Logout_User);

        cl_HomeUser = root.findViewById(R.id.cl_Home_User);

        tv_NameMensage = root.findViewById(R.id.tv_NameMensage_User);

        search_btn = root.findViewById(R.id.home_search_btn);
        textinput_location= root.findViewById(R.id.textinput_location);

        bookings_btn = root.findViewById(R.id.my_bookings_btn);

        favsBtn=root.findViewById(R.id.my_favs_btn2);

        bt_search_onMap = root.findViewById(R.id.bt_search_onMap);
    }


    private void clickListener(View root) {
        cl_HomeUser.setOnClickListener(v -> profileMenu.setVisibility(View.GONE));

        bt_ProfileMenu.setOnClickListener(v -> {
            if (profileMenu.getVisibility() == View.GONE) {
                profileMenu.setVisibility(View.VISIBLE);
                profileMenu.bringToFront();
            } else {
                profileMenu.setVisibility(View.GONE);
            }

        });

        bt_EditProfile.setOnClickListener(v -> {
            NavDirections action = HomeDirections.actionTravelerHomeToProfile(user);
            Navigation.findNavController(root).navigate(action);
        });


        bt_Logout.setOnClickListener(v -> {
            mAuth.signOut();
            Navigation.findNavController(root).navigate(R.id.action_traveler_home_to_login);
        });


        search_btn.setOnClickListener(v -> {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("inputText", textinput_location.getText().toString());
//                    Navigation.findNavController(root).navigate(R.id.action_traveler_home_to_searchHotel, bundle);
            String inputText = textinput_location.getText().toString();
            NavDirections action = HomeDirections.actionTravelerHomeToSearchHotel(inputText);
            Navigation.findNavController(root).navigate(action);
                });

        bt_search_onMap.setOnClickListener(v -> {
           Navigation.findNavController(root).navigate(R.id.action_traveler_home_to_hotelOnMap);
        });


        favsBtn.setOnClickListener(v -> {
           // Navigation.findNavController(root).navigate(R.id.action_traveler_home_to_favorites);
        });


        bookings_btn.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_traveler_home_to_myBookings);
        });
    }

    private void readUserData() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firestore.collection("Traveler").document(mUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        user = snapshot.toObject(Traveler.class);
                        loadDatatoElements();
                        try {
                            InternalStorage.writeObject(getContext(), "User", user);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.d("ERROR", "get failed with ", task.getException());
                }
            }
        });
    }
    private void loadDatatoElements(){
        if(user != null){
            tv_NameMensage.setText("Hi "+user.getName());

            Glide.with(this)
                    .load(user.getImage())
                    .placeholder(R.drawable.profile_pic_example)
                    .fitCenter()
                    .into(bt_ProfileMenu);
        }
    }

}
