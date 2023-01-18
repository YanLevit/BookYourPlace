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
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.bookyourplace.R;
import com.example.bookyourplace.model.User;

import com.example.bookyourplace.model.hotel_manager.HotelManager;
import com.example.bookyourplace.model.traveler.Traveler;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;


public class profile extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    DocumentReference documentReference;

    TabLayout tabLayout;
    ViewPager viewPager;
    PageAdapter pageAdapter;

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

        loadDatatoElements();

        return root;
    }

    private void loadDatatoElements() {
        if (!getArguments().isEmpty()) {
            user = (User) getArguments().getSerializable("User");
            if (user != null) {
                if (user instanceof Traveler) {
                    typeUser = "Traveler";
                    user = (Traveler) getArguments().getSerializable("User");
                }

                if (user instanceof HotelManager) {
                    typeUser = "Hotel Manager";
                    user = (HotelManager) getArguments().getSerializable("User");
                }
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseUser = firebaseAuth.getCurrentUser();
                db = FirebaseFirestore.getInstance();
                documentReference = db.collection(typeUser).document(firebaseUser.getUid());

                if (!user.getImage().isEmpty()) {
                    Glide.with(this)
                            .load(user.getImage())
                            .placeholder(R.drawable.profile_pic_example)
                            .fitCenter()
                            .into(iv_ProfileImage);
                }
            }
        }
    }


    private void initializeElements(View root) {

        viewPager = root.findViewById(R.id.pager);
        tabLayout = root.findViewById(R.id.tab_layout);
        pageAdapter = new PageAdapter (getParentFragmentManager());


        pageAdapter.addFragment(new PersonalData(),"Personal Data");
        pageAdapter.addFragment(new Security(), "Security");

        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);

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


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

}

