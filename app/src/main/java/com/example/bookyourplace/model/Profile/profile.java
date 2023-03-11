package com.example.bookyourplace.model.Profile;

import static android.app.Activity.RESULT_OK;
import static com.example.bookyourplace.model.GenerateUniqueIds.generateId;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class profile extends Fragment {
    private static final int CAMERA_REQUEST_CODE =123 ;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    DocumentReference documentReference;

    TabLayout tabLayout;
    ViewPager viewPager;
    PageAdapter pageAdapter;
    Uri profileImageUri;
    StorageTask mUploadTask;
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
        profileArgs args = profileArgs.fromBundle(getArguments());
        if (!getArguments().isEmpty()) {
            User user = args.getUser() ;
            if (user != null) {
                if (user instanceof Traveler) {
                    typeUser = "Traveler";
                }

                if (user instanceof HotelManager) {
                    typeUser = "Hotel Manager";
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
                        if(db.collection("Traveler").document(userId).getId().equals(userId)){
                            db.collection("Traveler").document(userId).set(user)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            db.collection("Traveler").document(userId).update("image",uri.toString());
                                            Glide.with(getContext())
                                                    .load(user.getImage())
                                                    .error(R.drawable.profile_pic_example)
                                                    .fitCenter()
                                                    .into(iv_ProfileImage);
                                            progressDialog.dismiss();
                                            bt_ProfileImageSave.setVisibility(View.GONE);
                                        }else {
                                            Toast.makeText(getContext(),"Error uploading data" , Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                        else if (db.collection("Hotel Manger").document(userId).getId().equals(userId)) {
                            db.collection("Hotel Manager").document(userId).set(user)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            db.collection("Hotel Manager").document(userId).update("image",uri.toString());
                                            //Load the user's new profile image
                                            Glide.with(getContext())
                                                    .load(user.getImage())
                                                    .error(R.drawable.profile_pic_example)
                                                    .fitCenter()
                                                    .into(iv_ProfileImage);

                                            progressDialog.dismiss();
                                            bt_ProfileImageSave.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(getContext(), "Error uploading data", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
    }




    private void openFileChooser() {
        // Create intent to open the phone's gallery
        Intent intentGallery = new Intent();
        intentGallery.setType("image/*");
        intentGallery.setAction(Intent.ACTION_PICK);

        // Create intent to open the phone's camera
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create intent to show chooser dialog
        Intent chooserIntent = Intent.createChooser(intentGallery, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {intentCamera});

        startActivityForResult(chooserIntent, 1);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profileImageUri = data.getData();

            Glide.with(this)
                    .load(profileImageUri)
                    .error(R.drawable.profile_pic_example)
                    .fitCenter()
                    .into(iv_ProfileImage);


            bt_ProfileImageSave.setVisibility(View.VISIBLE);
        }
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            profileImageUri = data.getData();
//
//            Glide.with(this)
//                    .load(profileImageUri)
//                    .error(R.drawable.profile_pic_example)
//                    .fitCenter()
//                    .into(iv_ProfileImage);
//
//            pb_ProfileImage.setVisibility(View.VISIBLE);
//            bt_ProfileImageSave.setVisibility(View.VISIBLE);
//        }
//    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}

