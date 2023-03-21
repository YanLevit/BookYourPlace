package com.example.bookyourplace.model.traveler.hotels;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.bookyourplace.R;
import com.example.bookyourplace.model.GenerateUniqueIds;
import com.example.bookyourplace.model.hotel_manager.Hotel;
import com.example.bookyourplace.model.traveler.Booking;
import com.example.bookyourplace.model.traveler.Traveler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HotelViewer extends Fragment {
    private ImageButton  backBtn;
    private TextView hotelName, hotelPrice, hotelInfo, numOfRatings;
    private RatingBar rating;
    private String hotelKey;
    private ImageView iv_hotel_cover_photo;
    private ImageSlider othersphotosslider;

    private RecyclerView rvFeatures;
    private final ArrayList<Hotel> similarHotelKeys = new ArrayList<>();
    String userID;

    ////////////////////////////////

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;


    private Booking booking;


    private Button ivBooking;
    private MaterialTextView tv_date_start, tv_date_end;
    private EditText tv_adults, tv_children;
    private String hotelID;

    private Date start_date, end_date;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {

            @Override
            public void handleOnBackPressed() {
                HotelViewerArgs args = HotelViewerArgs.fromBundle(getArguments());
                String fragment = args.getSearch();
                String mapFragment = args.getClickDetails();

                if(fragment != null && fragment.equals("Search")){
                    Navigation.findNavController(getView()).popBackStack();
                }
                else if (mapFragment != null && mapFragment.equals("clickDetails")){
                    Navigation.findNavController(getView()).popBackStack();
                }

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.traveler_fragment_hotel_view, container, false);

        initializeElements(root);

        loadDatatoElements();

        clickListener(root);

        return root;
    }

    private void initializeElements(View root) {
        backBtn= root.findViewById(R.id.bt_traveler_hotel_view_back);
        backBtn.bringToFront();

        hotelName = root.findViewById(R.id.traveler_hotelview_hotelName);
        hotelPrice = root.findViewById(R.id.traveler_hotelview_hotelPrice);
        hotelInfo = root.findViewById(R.id.traveler_hotelview_hotelinfo);
        rating = root.findViewById(R.id.ratingBar);
        numOfRatings = root.findViewById(R.id.number_of_review);

        iv_hotel_cover_photo = root.findViewById(R.id.iv_hotel_cover_photo);
        othersphotosslider = root.findViewById(R.id.hotel_slider_photos);
        HotelViewerArgs args = HotelViewerArgs.fromBundle(getArguments());
        hotelKey = args.getHotelId();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference hotelRef = db.collection("hotels");

        rvFeatures = root.findViewById(R.id.features_gridview);

        ////////////////////////////////
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        hotelID = args.getHotelId();

        ivBooking = root.findViewById(R.id.iv_Booking);
        tv_date_start = root.findViewById(R.id.ti_choosen_start_date);
        tv_date_end = root.findViewById(R.id.ti_choosen_end_date);
        tv_adults = root.findViewById(R.id.booking_btn_choosen_adults);
        tv_children = root.findViewById(R.id.booking_btn_choosen_children);

    }


    private void loadDatatoElements() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference hotelRef = db.collection("hotels").document(hotelKey);
        hotelRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Hotel hotel = documentSnapshot.toObject(Hotel.class);
                    if(hotel != null)
                    {
                        rating.setRating(hotel.getStars());
                        hotelName.setText(hotel.getName());
                        hotelPrice.setText(String.valueOf(hotel.getPrice()));
                        hotelInfo.setText(hotel.getDescription());
                        numOfRatings.setText(String.valueOf(hotel.getStars()));

                        Glide.with(getActivity())
                                .load(hotel.getCoverPhoto())
                                .placeholder(R.drawable.admin_backgrounf_pic)
                                .fitCenter()
                                .into(iv_hotel_cover_photo);

                        List<SlideModel> slideModelList = new ArrayList<>();

                        if(hotel.getOtherPhotos() == null || hotel.getOtherPhotos().isEmpty()){
                            // Add default picture to slideModelList
                            slideModelList.add(new SlideModel("https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.firstcolonyfoundation.org%2Fobx-history-weekend%2Fday-3-symposium%2Fattachment%2Fno-photo-available%2F&psig=AOvVaw0pBY5qVgjp8rIgpnh23ZqW&ust=1678208910964000&source=images&cd=vfe&ved=0CBAQjRxqFwoTCICHx8Llx_0CFQAAAAAdAAAAABAf", "", ScaleTypes.FIT));
                        } else {
                            // Add pictures to slideModelList
                            for(String photo : hotel.getOtherPhotos()){
                                slideModelList.add(new SlideModel(String.valueOf(photo),"", ScaleTypes.FIT));
                            }
                        }
                        othersphotosslider.setImageList(slideModelList, ScaleTypes.FIT);


                        setHotelFeatures(hotel);
                        populateRecyclerViewOfHotel(hotel);

                        // Delete all information about who is there for the hotel
                        hotelRef.update("whoIsThere", new ArrayList<String>())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("TAG", "Deleted all information about who is there for the hotel with id: " + hotelKey);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("TAG", "Error deleting all information about who is there for the hotel with id: " + hotelKey, e);
                                    }
                                });
                    }
                }
            }
        });
    }

    private void clickListener(View root) {
        backBtn.setOnClickListener(v -> getActivity().onBackPressed());


        tv_date_start.setOnClickListener(v -> {
            Calendar mcurrentDate = Calendar.getInstance();
            int mYear = mcurrentDate.get(Calendar.YEAR);
            int mMonth = mcurrentDate.get(Calendar.MONTH);
            int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog mDatePicker;
            mDatePicker = new DatePickerDialog(getContext(), R.style.date_dialog_theme, (datepicker, selectedyear, selectedmonth, selectedday) -> {
                Calendar date = Calendar.getInstance();
                date.set(selectedyear, selectedmonth, selectedday);
                start_date = date.getTime();

                tv_date_start.setText(selectedday + "/" + selectedmonth + "/" + selectedyear);
                tv_date_start.setError(null);
            }, mYear, mMonth, mDay);
            mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            if(end_date != null){
                mDatePicker.getDatePicker().setMaxDate(end_date.getTime());
            }
            mDatePicker.show();

        });

        tv_date_end.setOnClickListener(v -> {
            Calendar mcurrentDate = Calendar.getInstance();
            int mYear = mcurrentDate.get(Calendar.YEAR);
            int mMonth = mcurrentDate.get(Calendar.MONTH);
            int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog mDatePicker;

            mDatePicker = new DatePickerDialog(getContext(), R.style.date_dialog_theme, (datepicker, selectedyear, selectedmonth, selectedday) -> {
                Calendar date = Calendar.getInstance();
                date.set(selectedyear, selectedmonth, selectedday);
                end_date = date.getTime();

                tv_date_end.setText(selectedday + "/" + selectedmonth + "/" + selectedyear);
                tv_date_end.setError(null);
            }, mYear, mMonth, mDay);
            mDatePicker.getDatePicker().setMinDate(start_date.getTime());
            mDatePicker.show();
        });


        ivBooking.setOnClickListener(v -> {
            registerOnFirebase();
        });
    }


    private boolean verifyData() {

        boolean error = false;

        if(tv_date_start.getText().toString().trim().isEmpty()){
            tv_date_start.setError("");
            tv_date_start.requestFocus();
            error = true;
        }

        if(tv_date_end.getText().toString().trim().isEmpty()){
            tv_date_end.setError("");
            tv_date_end.requestFocus();
            error = true;
        }

        int adults, children;
        if(tv_adults.getText().toString().isEmpty())
            adults = 0;
        else
            adults = Integer.parseInt(tv_adults.getText().toString().trim());

        if(tv_children.getText().toString().isEmpty())
            children = 0;
        else
            children = Integer.parseInt(tv_children.getText().toString().trim());

        if(adults <= 0 && children <= 0){
            tv_adults.setError("");

            tv_children.setError("");
            error = true;
        }

        /////////////////
        float price_night = Float.parseFloat(hotelPrice.getText().toString());
        float price = 0;
        int n_nights = 0;

        if(end_date == null || start_date == null){
            error = true;
        }
        else{
            n_nights = (int) (end_date.getTime() - start_date.getTime()) / (1000 * 60 * 60 * 24);
            price = price_night * n_nights;
        }

        ////////////////

        if(!error){
            userID = firebaseUser.getUid();

            booking = new Booking(hotelID, userID, start_date, end_date, adults, children, price);

            if (booking != null) {
                return true;
            }
            return false;
        }
        {
            return false;
        }

    }
    private void registerOnFirebase() {
        if (verifyData()) {
            String reservationID = GenerateUniqueIds.generateId();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Booking").document(reservationID).set(booking)
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(getContext(), "Booking has ben registered successfully!", Toast.LENGTH_LONG).show();

                            DocumentReference travelerRef = db.collection("Traveler").document(firebaseUser.getUid());
                            travelerRef.get().addOnSuccessListener(documentSnapshot -> {
                                Traveler user = documentSnapshot.toObject(Traveler.class);

                                user.addBooking(reservationID);
                                travelerRef.set(user);
                            }).addOnFailureListener(e -> {
                                Log.e("ERROR", "getUser:onCancelled", e);
                            });

                            DocumentReference hotelRef = db.collection("hotels").document(hotelID);
                            hotelRef.get().addOnSuccessListener(documentSnapshot -> {
                                Hotel hotel = documentSnapshot.toObject(Hotel.class);

                                hotel.addBooking(reservationID);
                                hotelRef.set(hotel);
                            }).addOnFailureListener(e -> {
                                Log.e("ERROR", "getUser:onCancelled", e);
                            });

                            final NavController navController = Navigation.findNavController(getView());
                            navController.navigate(R.id.action_hotelViewer_to_traveler_home);
                        } else {
                            Toast.makeText(getContext(), "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }



private void populateRecyclerViewOfHotel(Hotel hotel) {
    double lowerPrice, higherPrice;

    lowerPrice = hotel.getPrice() - (hotel.getPrice() * 0.1);
    higherPrice = hotel.getPrice() + (hotel.getPrice() * 0.1);

    List<String> keys = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference hotelsCollectionRef = db.collection("hotels");
    hotelsCollectionRef.get().addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            QuerySnapshot snapshot = task.getResult();
            for (QueryDocumentSnapshot document : snapshot) {
                if (!document.getId().equals(hotelKey)) {
                    Hotel hotelSnapshot = document.toObject(Hotel.class);
                    if (hotelSnapshot.getPrice() > lowerPrice && hotelSnapshot.getPrice() < higherPrice) {
                        similarHotelKeys.add(hotelSnapshot);
                        keys.add(document.getId());
                    }
                }
            }
        } else {
            Log.d("TAG", "Error getting documents: ", task.getException());
        }
    });
}

    private void setHotelFeatures(Hotel hotel) {
        Map<String, Boolean> hotelFeature = hotel.getFeature().getFeatures();
        ArrayList <String> listFeatures = new ArrayList<>();

        // using for-each loop for iteration over Map.entrySet()
        for (Map.Entry<String,Boolean> entry : hotelFeature.entrySet()) {
            if(entry.getValue() == true)
                listFeatures.add(entry.getKey());
        }
        adapterFeatures adapterFeatures= new adapterFeatures(listFeatures);
        rvFeatures.setAdapter(adapterFeatures);
        // Set layout manager to position the items
        rvFeatures.setLayoutManager(new GridLayoutManager(getContext(),2));
    }
}
