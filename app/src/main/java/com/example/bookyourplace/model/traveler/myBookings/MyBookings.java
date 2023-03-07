package com.example.bookyourplace.model.traveler.myBookings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookyourplace.R;
import com.example.bookyourplace.model.hotel_manager.Hotel;
import com.example.bookyourplace.model.traveler.Booking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.List;


public class MyBookings extends Fragment {

    private RecyclerView recyclerView;
    private List<Booking> bookings;
    private Hotel[] hotels;
    private ArrayList<String> bookingKeys;

    private FirebaseFirestore db;
    ImageButton bt_backHOme;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {

            @Override
            public void handleOnBackPressed() {
//                Navigation.findNavController(getView()).navigate(R.id.action_myBookings_to_traveler_home);
                bt_backHOme.performClick();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.traveler_fragment_mybookings_hotels, container, false);

        initializeElements(root);
        clickListener(root);

        getBookingRefs(root);

        return root;
    }

    private void initializeElements(View root) {
        db = FirebaseFirestore.getInstance();
        recyclerView = root.findViewById(R.id.bookingsList_recyclerView);
        bt_backHOme = root.findViewById(R.id.bt_Backhome_bookings);
    }

    private void clickListener(View root) {
        bt_backHOme.setOnClickListener(v -> {

            Navigation.findNavController(root).navigate(R.id.action_myBookings_to_traveler_home);

        });
    }



    private void getBookingRefs(View root) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference userRef = db.collection("Traveler").document(user.getUid());
        userRef.addSnapshotListener((snapshot, e) -> {
            if (snapshot != null && snapshot.exists()) {
                bookingKeys = (ArrayList<String>) snapshot.get("bookings");
                if (bookingKeys != null) {
                    getBookings();
                }
            }
        });
    }

    private void getBookings() {
        bookings = new ArrayList<>();
        CollectionReference bookingsRef = db.collection("Booking");

        bookingsRef.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w("TAG", "Listen failed.", e);
                return;
            }
            if (snapshots != null) {
                bookings.clear();
                for (QueryDocumentSnapshot document : snapshots) {
                    if (bookingKeys.contains(document.getId())) {
                        bookings.add(document.toObject(Booking.class));
                    }
                }
                getHotels();
            } else {
                Log.d("TAG", "Current data: null");
            }
        });
    }

    private void getHotels() {
        hotels = new Hotel[bookings.size()];
        CollectionReference hotelsRef = db.collection("hotels");

        hotelsRef.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w("TAG", "Listen failed.", e);
                return;
            }
            if (snapshots != null) {
                int i = 0;
                for (QueryDocumentSnapshot document : snapshots) {
                    for (int j = 0; j < bookings.size(); j++) {
                        if (bookings.get(j).getHotelID().equals(document.getId())) {
                            hotels[j] = document.toObject(Hotel.class);
                        }
                    }
                }

                if (bookings.size() == 0) {
                    Navigation.findNavController(getView()).navigate(R.id.action_myBookings_self);
                } else {
                    adapterMyBookings adapterMyBookings = new adapterMyBookings(bookings, hotels);
                    recyclerView.setAdapter(adapterMyBookings);
                    // Set layout manager to position the items
                   recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            } else {
                Log.d("TAG", "Current data: null");
            }
        });
    }
}
