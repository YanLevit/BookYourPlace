package com.example.bookyourplace.model.Profile;

import static com.example.bookyourplace.model.GenerateUniqueIds.generateId;
import static com.google.common.io.Files.getFileExtension;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;


public class profile extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    DocumentReference documentReference;
    FirebaseStorage storage;

    TabLayout tabLayout;
    ViewPager viewPager;
    PageAdapter pageAdapter;
    Uri profileImageUri;
    StorageTask mUploadTask;
    User user;
    String typeUser;
    ImageView iv_ProfileImage;
    ImageButton bt_ProfileImageEdit , bt_ProfileImageSave,  bt_Backhome_Profile;

    private ProgressBar pb_ProfileImage;


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
        pb_ProfileImage = root.findViewById(R.id.pb_ProfileImage);
        pb_ProfileImage.setVisibility(View.GONE);
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

        bt_ProfileImageEdit.setOnClickListener(v -> openFileChooser());

        bt_ProfileImageSave.setOnClickListener(v -> saveProfileImage());

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

    private void saveProfileImage() {
        if (profileImageUri != null) {
            if (!user.getImage().isEmpty()) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference imageDeleteRef = storage.getReferenceFromUrl(user.getImage());
                imageDeleteRef.delete();
            }
        }

        // Code for showing progressDialog while uploading
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        // Defining the child of storageReference
        String imageId = generateId() + "." + getFileExtension(profileImageUri);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storage.getReference().child(imageId).putFile(profileImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storage.getReference().child(imageId).getDownloadUrl().addOnSuccessListener(uri -> {
                        // Success, Image uploaded
                        Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();
                        user.setImage(uri.toString());
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("Travelers").document(userId).set(user)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            db.collection("hotel manager").document(userId).set(user)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                progressDialog.dismiss();
                                                                bt_ProfileImageSave.setVisibility(View.GONE);
                                                            } else {
                                                                Toast.makeText(getContext(), "Error uploading data", Toast.LENGTH_SHORT).show();
                                                                progressDialog.dismiss();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    });
                });

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null){
            profileImageUri = data.getData();

            Glide.with(this).load(profileImageUri).error(R.drawable.profile_pic_example).fitCenter().into(iv_ProfileImage);
            pb_ProfileImage.setVisibility(View.VISIBLE);
            bt_ProfileImageSave.setVisibility(View.VISIBLE);

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}

