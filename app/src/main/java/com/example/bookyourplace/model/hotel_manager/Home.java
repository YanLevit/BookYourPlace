package com.example.bookyourplace.model.hotel_manager;


import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookyourplace.R;
import com.example.bookyourplace.model.InternalStorage;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;


public class Home  extends Fragment {

    private static final String TAG = "" ;
    FirebaseUser mUser;
    FirebaseAuth mAuth;

    HotelManager user;

    FusedLocationProviderClient fusedLocationProviderClient;
    ConstraintLayout cl_HomeUser;
    ShapeableImageView bt_ProfileMenu;
    LinearLayout profileMenu;
    Button bt_EditProfile, bt_Logout;
    TextView tv_NameMensage;
    TextView textinput_location;
    MaterialButton bt_register_hotel;

    private ArrayList<Hotel> mHotelList;
    private RecyclerView mRecyclerView;
    private HotelListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        OnBackPressedCallback callback = new OnBackPressedCallback(true) { //This code is creating an instance of OnBackPressedCallback and setting it to
            // handle when the back button is pressed. The handleOnBackPressed() method is empty in this case, so it won't do anything when the back button is pressed.
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

                confirm.setOnClickListener(v -> {
                    bt_Logout.performClick();
                    dialog.dismiss();
                });

                deny.setOnClickListener(v -> dialog.dismiss());

                dialog.show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.hotel_manager_fragment_home, container, false);// This line inflates the layout file for the fragment and returns it as a view.

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        readUserData();

        initializeElements(root);

        clickListener(root);

       getListHotel(root);

        loadDatatoElements();

        return root;

    }

    private void initializeElements(View root) { //This method initializes the elements by connecting them to the corresponding views in the layout file.

        bt_ProfileMenu = root.findViewById(R.id.bt_ProfileMenu);

        profileMenu = root.findViewById(R.id.ll_profile_menu_User);
        profileMenu.setVisibility(View.GONE);

        bt_EditProfile = root.findViewById(R.id.bt_editProfile_User);

        bt_Logout = root.findViewById(R.id.bt_Logout_User);

        cl_HomeUser = root.findViewById(R.id.cl_Home_User);

        tv_NameMensage = root.findViewById(R.id.tv_NameMensage_User);

        textinput_location = root.findViewById(R.id.textinput_location);

        bt_register_hotel = root.findViewById(R.id.bt_register_hotel);

        mHotelList = new ArrayList<>();

        mRecyclerView = root.findViewById(R.id.rc_hotel_list);

        mLayoutManager = new LinearLayoutManager(getContext());

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
            NavDirections action = com.example.bookyourplace.model.hotel_manager.HomeDirections.actionHotelManagerHomeToProfile(user);
            Navigation.findNavController(root).navigate(action);
        });


        bt_Logout.setOnClickListener(v -> {
            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.logout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            dialog.setCancelable(false);

            dialog.create();

            Button confirm = dialog.findViewById(R.id.bt_dialog_logout_Confirm);
            Button deny = dialog.findViewById(R.id.bt_dialog_logout_Deny);
            dialog.show();
            confirm.setOnClickListener(V -> {
                dialog.dismiss();
                mAuth.signOut();
                Navigation.findNavController(root).navigate(R.id.action_hotel_manager_home_to_login);
            });
            deny.setOnClickListener(V -> dialog.dismiss());
        });

        bt_register_hotel.setOnClickListener(view -> {
            Navigation.findNavController(root).navigate(R.id.action_hotel_manager_home_to_hotel_registration);
        });
    }

    private void readUserData() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firestore.collection("Hotel Manager").document(mUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        user = snapshot.toObject(HotelManager.class);
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

    private void loadDatatoElements() {
        if (user != null) {
            tv_NameMensage.setText("Hi " + user.getName());

            Glide.with(this)
                    .load(user.getImage())
                    .placeholder(R.drawable.profile_pic_example)
                    .fitCenter()
                    .into(bt_ProfileMenu);
        }
    }

    private void getListHotel(View root) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String managerId = mUser.getUid(); // get current user's ID

        Query hotelsQuery = db.collection("hotels")
                .whereEqualTo("manager", managerId); // query hotels where manager field matches current user's ID

        hotelsQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mHotelList.clear();
                for (DocumentSnapshot document : task.getResult()) {
                    Hotel hotel = document.toObject(Hotel.class);
                    mHotelList.add(hotel);
                }
                buildRecyclerView(root);
            } else {
                Log.d(TAG, "Error getting hotels: ", task.getException());
            }
        });

        setHotelListChangeListener(hotelsQuery);
    }

    private void setHotelListChangeListener(Query hotelsQuery) {
        hotelsQuery.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                return;
            }

            mHotelList.clear();

            for (DocumentSnapshot document : value.getDocuments()) {
                Hotel hotel = document.toObject(Hotel.class);
                mHotelList.add(hotel);
            }
            if (mAdapter == null) {
                mAdapter = new HotelListAdapter(mHotelList,getActivity());
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    private void buildRecyclerView(View root) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mAdapter = new HotelListAdapter(mHotelList, getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new HotelListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Hotel hotel = mHotelList.get(position);
                String hotelName = hotel.getName();
                NavDirections action = HomeDirections.actionHotelManagerHomeToHotelManagerHotelView(hotel, hotelName);
                Navigation.findNavController(root).navigate(action);
            }

            @Override
            public void onSeeClick(int position) {
                Hotel hotel = mHotelList.get(position);
                String hotelName = hotel.getName();
                NavDirections action = HomeDirections.actionHotelManagerHomeToHotelManagerHotelView(hotel, hotelName);
                Navigation.findNavController(root).navigate(action);
            }

            @Override
            public void onEditClick(int position) {
                Hotel hotel = mHotelList.get(position);
                String hotelName = hotel.getName();
                NavDirections action = HomeDirections.actionHotelManagerHomeToHotelManagerHotelEdit(hotel, hotelName, "Hotel_Manage");
                Navigation.findNavController(root).navigate(action);
            }

            @Override
            public void onDeleteClick(int position) {

                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.hotel_delete_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                dialog.setCancelable(false);

                dialog.create();

                Button confirm = dialog.findViewById(R.id.bt_dialog_delete_Confirm);
                Button deny = dialog.findViewById(R.id.bt_dialog_delete_Deny);

                confirm.setOnClickListener(v -> {
                    Hotel hotelToDelete = mHotelList.get(position);
                    String hotelName = hotelToDelete.getName();

                    StorageReference imageDeleteCover = FirebaseStorage.getInstance().getReferenceFromUrl(hotelToDelete.getCoverPhoto());
                    imageDeleteCover.delete();

                    for (String url : hotelToDelete.getOtherPhotos()) {
                        StorageReference imageDeleteOthers = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                        imageDeleteOthers.delete();
                    }

                    db.collection("hotels")
                            .whereEqualTo("name", hotelName)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    String hotelId = document.getId();
                                    document.getReference().delete().addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                        Toast.makeText(getContext(), "Hotel Deleted Successfully", Toast.LENGTH_LONG).show();
                                        db.collection("Hotel Manager")
                                                .document(mUser.getUid())
                                                .update("hotels", FieldValue.arrayRemove(hotelId))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "Hotel ID removed from hotelmanager collection.");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error removing hotel ID from hotelmanager collection", e);
                                                    }
                                                });
                                        mHotelList.remove(position);
                                        mAdapter.notifyItemRemoved(position);
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error deleting document", e);
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error getting documents", e);
                                }
                            });
                    dialog.dismiss();
                });
                deny.setOnClickListener(v -> dialog.dismiss());
                dialog.show();
            }
        });
    }
}



