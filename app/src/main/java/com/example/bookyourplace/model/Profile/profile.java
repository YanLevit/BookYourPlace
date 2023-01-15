package com.example.bookyourplace.model.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.bookyourplace.R;
import com.example.bookyourplace.model.User;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class profile extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    User user;
    String typeUser;
    ImageView iv_ProfileImage;
    ImageButton bt_ProfileImageEdit , bt_ProfileImageSave,  bt_Backhome_Profile;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {

            @Override
            public void handleOnBackPressed() {
                bt_Backhome_Profile.performClick();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeElements(root);

        clickListeners(root);


        return root;
    }


    private void initializeElements(View root) {

        iv_ProfileImage = root.findViewById(R.id.iv_ProfileImage);
        bt_ProfileImageEdit = root.findViewById(R.id.bt_ProfileImageEdit);
        bt_ProfileImageSave = root.findViewById(R.id.bt_ProfileImageSave);
        bt_ProfileImageSave.setVisibility(View.GONE);
        bt_Backhome_Profile = root.findViewById(R.id.bt_Backhome_Profile);
    }

    private void clickListeners(View root) {

        bt_Backhome_Profile.setOnClickListener(v -> {
            switch (typeUser){
                case "Traveler":
                    Navigation.findNavController(root).navigate(R.id.action_profile_to_traveler_home);
                    break;
                case "Hotel Manager":
                    Navigation.findNavController(root).navigate(R.id.action_profile_to_hotel_manager_home);
                    break;
            }

        });
    }

}
