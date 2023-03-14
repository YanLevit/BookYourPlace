package com.example.bookyourplace.model.hotel_manager;


import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import com.example.bookyourplace.R;
import com.example.bookyourplace.model.InternalStorage;
import com.example.bookyourplace.model.traveler.Booking;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;


public class Hotel_View extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;


    private HotelManager user;

    private String hotelId;
    private Hotel hotel;

    private ImageView iv_hotel_view_photo;
    private TextView tv_hotel_view_name ;
    private RatingBar rb_hotel_view_stars;
    private ImageButton bt_hotel_view_edit, bt_hotel_view_delete, bt_hotel_view_back;

    private RecyclerView lastBookings_rv;

    private static String TAG = "";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {

            @Override
            public void handleOnBackPressed() {
                bt_hotel_view_back.performClick();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_hotel_manager_hotel_view, container, false);

//        hotelId = getArguments().getString("Hotel Name");
        Hotel_ViewArgs args = Hotel_ViewArgs.fromBundle(getArguments());
        hotelId = args.getHotel().getName();

        initializeElements(root);

        getUserData();
        getCurrentBookings(root);
        loadDatatoElements(root);
        clickListeners(root);


        return root;

    }

    private void initializeElements(View root) {
        iv_hotel_view_photo = root.findViewById(R.id.iv_hotel_view_photo);
        tv_hotel_view_name = root.findViewById(R.id.tv_hotel_view_name);
        rb_hotel_view_stars = root.findViewById(R.id.rb_hotel_view_stars);
        bt_hotel_view_edit = root.findViewById(R.id.bt_hotel_view_edit);
        bt_hotel_view_delete = root.findViewById(R.id.bt_hotel_view_delete);

        bt_hotel_view_back = root.findViewById(R.id.bt_hotel_view_back);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        lastBookings_rv = root.findViewById(R.id.manager_lastBooking_rv);
    }

    private void getUserData(){
        try {
            user = (HotelManager) InternalStorage.readObject(getContext(), "User");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadDatatoElements(View root) {
        Hotel_ViewArgs args = Hotel_ViewArgs.fromBundle(getArguments());
        String HotelName = args.getHotel().getName();
        if(!HotelName.isEmpty()) {
            hotel = args.getHotel();
            if (hotel != null) {
                tv_hotel_view_name.setText(hotel.getName());

                rb_hotel_view_stars.setRating(hotel.getStars());

                Glide.with(this)
                        .load(hotel.getCoverPhoto())
                        .placeholder(R.drawable.admin_backgrounf_pic)
                        .fitCenter()
                        .into(iv_hotel_view_photo);
            } else {
                Navigation.findNavController(root).navigate(R.id.action_profile_to_hotel_manager_home);
            }
        }
        else {
            Navigation.findNavController(root).navigate(R.id.action_profile_to_hotel_manager_home);
        }
    }

    private void getCurrentBookings(View root) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference hotelsRef = db.collection("hotels");
        Query query = hotelsRef.whereEqualTo("name", hotelId).limit(1);



        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if (!querySnapshot.isEmpty()) {
                    DocumentSnapshot hotelDoc = querySnapshot.getDocuments().get(0);
                    Hotel hotel = hotelDoc.toObject(Hotel.class);
                    ArrayList<String> keys = (ArrayList<String>) hotel.getBookings();
                    populateRecyclerView(root, keys);
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error getting current bookings", e);
            }
        });
    }

    private void populateRecyclerView(View root, ArrayList<String> keys) {
        CollectionReference bookingsRef = FirebaseFirestore.getInstance().collection("Booking");
        bookingsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                ArrayList<Booking> bookings = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : querySnapshot) {
                    if (keys.contains(documentSnapshot.getId())) {
                        Booking booking = documentSnapshot.toObject(Booking.class);
                        bookings.add(booking);
                    }
                }

                // Create adapter passing in the sample user data
                ViewBookingsAdpater adapter = new ViewBookingsAdpater(bookings);
                // Attach the adapter to the recyclerview to populate items
                lastBookings_rv.setAdapter(adapter);
                // Set layout manager to position the items
                lastBookings_rv.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error getting bookings", e);
            }
        });
    }


    private void clickListeners(View root) {
        bt_hotel_view_back.setOnClickListener(v -> {
            Navigation.findNavController(getView()).navigate(R.id.action_hotel_manager_hotel_view_to_hotel_manager_home);
        });

        bt_hotel_view_edit.setOnClickListener(v -> {
            String hotelName = hotel.getName();
            NavDirections action = Hotel_ViewDirections.actionHotelManagerHotelViewToHotelManagerHotelEdit(hotel,hotelName,"Hotel_View");
            Navigation.findNavController(root).navigate(action);
        });

        bt_hotel_view_delete.setOnClickListener(v -> {
            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.hotel_delete_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            dialog.setCancelable(false);

            dialog.create();

            Button confirm = dialog.findViewById(R.id.bt_dialog_delete_Confirm);
            Button deny = dialog.findViewById(R.id.bt_dialog_delete_Deny);
            confirm.setOnClickListener(V->{
                StorageReference imageDeleteCover = FirebaseStorage.getInstance().getReferenceFromUrl(hotel.getCoverPhoto());
                imageDeleteCover.delete();

                for(String url : hotel.getOtherPhotos()){
                    StorageReference imageDeleteOthers = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                    imageDeleteOthers.delete();
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("hotels").document(hotelId).delete();
                bt_hotel_view_back.performClick();
                dialog.dismiss();
            });
            deny.setOnClickListener(V -> dialog.dismiss());
            dialog.show();
        });
    }
}