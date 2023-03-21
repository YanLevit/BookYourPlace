package com.example.bookyourplace.model.hotel_manager;



import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.bookyourplace.R;
import com.example.bookyourplace.model.Address;
import com.example.bookyourplace.model.GenerateUniqueIds;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hotel_Edit extends Fragment {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private String hotelID;
    private Hotel hotel;

    private EditText et_Name,  et_TotalRooms, et_Price;

    private TextView tv_Hotel_Stars ;
    private RatingBar rb_Stars;

    private CountryCodePicker ccp_PhoneCode;
    private EditText et_Phone;

    private CountryCodePicker ccp_country;
    private EditText et_City, et_Address;

    private ExtendedFloatingActionButton bt_Features;
    private TextView tv_Features_Selected;
    private HotelFeature hotelFeatures;

    private EditText et_Description;

    private LinearLayout ll_Hotel_Photos;
    private LinearLayout ll_Hotel_Cover_Photo;
    private TextView tv_title_cover_photo, tv_title_others_photo;
    private Dialog dialog;

    private static final int PICK_COVER_IMAGE_REQUEST = 1;
    private static final int PICK_OTHERS_IMAGE_REQUEST = 2;
    private Uri coverPhoto;
    private ImageSlider mainslider;
    private List<SlideModel> slideModelList;
    private List<Uri> othersphotos;
    private TextView tv_popupMenu;

    private LatLng coordinates;

    private ImageButton bt_hotel_edit_back;

    private MaterialButton bt_EditHotel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {

            @Override
            public void handleOnBackPressed() {
                Hotel_EditArgs args = Hotel_EditArgs.fromBundle(getArguments());
                String fragment = args.getPreviousFragment();

                switch (fragment){
                    case "Hotel_View":
                        NavDirections action = Hotel_EditDirections.actionHotelManagerHotelEditToHotelManagerHotelView(hotel,hotelID);
                        Navigation.findNavController(getView()).navigate(action);
                        break;

                    case "Hotel_Manage":
                        Navigation.findNavController(getView()).navigate(R.id.action_hotel_manager_hotel_edit_to_hotel_manager_home);
                        break;
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_hotel_manager_hotel_edit, container, false);
        Hotel_ViewArgs args = Hotel_ViewArgs.fromBundle(getArguments());
        hotelID = args.getHotel().getName();

        initializeElements(root);

        loadDatatoElements(root);

        clickListeners();

        return root;
    }

    private void initializeElements(View root) {
        firebaseAuth = FirebaseAuth.getInstance();

        et_Name = root.findViewById(R.id.et_Hotel_Name_edit);

        tv_Hotel_Stars = root.findViewById(R.id.tv_Hotel_Stars_edit);
        rb_Stars = root.findViewById(R.id.rb_Hotel_Stars_edit);

        ccp_PhoneCode = root.findViewById(R.id.ccp_PhoneCode_Hotel_edit);
        et_Phone=root.findViewById(R.id.et_Phone_Hotel_edit);
        ccp_PhoneCode.registerCarrierNumberEditText(et_Phone);

        et_TotalRooms = root.findViewById(R.id.et_Hotel_Total_Rooms_edit);
        et_Price = root.findViewById(R.id.et_Hotel_Price_Rooms_edit);

        ccp_country = root.findViewById(R.id.ccp_Hotel_Country_edit);
        et_City = root.findViewById(R.id.et_Hotel_City_edit);
        et_Address = root.findViewById(R.id.et_Hotel_Address_edit);


        et_Description = root.findViewById(R.id.et_Description_Hotel_edit);

        bt_Features = root.findViewById(R.id.bt_Features_edit);
        tv_Features_Selected = root.findViewById(R.id.tv_Features_Selected_edit);

        ll_Hotel_Cover_Photo = root.findViewById(R.id.ll_Hotel_Cover_Photo);
        tv_title_cover_photo = root.findViewById(R.id.tv_title_cover_photo_edit);
        ll_Hotel_Photos = root.findViewById(R.id.ll_Hotel_Photos_edit);
        tv_title_others_photo = root.findViewById(R.id.tv_title_others_photo_edit);
        othersphotos = new ArrayList<>();

        bt_hotel_edit_back = root.findViewById(R.id.bt_hotel_edit_back);

        bt_EditHotel = root.findViewById(R.id.bt_EditHotel);
    }

    private void loadDatatoElements(View root) {
        if (!getArguments().isEmpty()) {
            hotelID = getArguments().getString("Hotel Name");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseStorage storage = FirebaseStorage.getInstance();

            CollectionReference hotelsRef = db.collection("hotels");
            Query query = hotelsRef.whereEqualTo("name", hotelID).limit(1);

            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot hotelDoc = querySnapshot.getDocuments().get(0);
                        hotel = hotelDoc.toObject(Hotel.class);

                        et_Name.setText(hotel.getName());
                        rb_Stars.setRating(hotel.getStars());

                        ccp_PhoneCode.setFullNumber(hotel.getPhone());

                        et_TotalRooms.setText(String.valueOf(hotel.getTotal_Rooms()));
                        et_Price.setText(String.valueOf(hotel.getPrice()));

                        ccp_country.setDefaultCountryUsingNameCode(hotel.getAddress().getCountry());
                        ccp_country.resetToDefaultCountry();
                        et_City.setText(hotel.getAddress().getCity());
                        et_Address.setText(hotel.getAddress().getAddress());


                        if (hotel.getAddress().getCoordinates() != null) {
                            coordinates = hotel.getAddress().getCoordinates();
                        }

                        et_Description.setText(hotel.getDescription());

                        hotelFeatures = hotel.getFeature();

                        if (!hotel.getCoverPhoto().isEmpty()) {
                            StorageReference coverPhotoRef = storage.getReferenceFromUrl(hotel.getCoverPhoto());
                            coverPhotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    coverPhoto = uri;
                                }
                            });
                        } else {
                            coverPhoto = null;
                        }

                        for (String photo : hotel.getOtherPhotos()) {
                            StorageReference otherPhotoRef = storage.getReferenceFromUrl(photo);
                            otherPhotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    othersphotos.add(uri);
                                }
                            });
                        }

                        hotel_features_listener();
                    } else {
                        Navigation.findNavController(root).navigate(R.id.action_hotel_manager_hotel_edit_to_hotel_manager_home);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("TAG", "Error getting current bookings", e);
                }
            });
        } else {
            Navigation.findNavController(root).navigate(R.id.action_hotel_manager_hotel_edit_to_hotel_manager_home);
        }
    }

    private void clickListeners() {
        bt_hotel_edit_back.setOnClickListener(v -> {
            String fragment = getArguments().getString("PreviousFragment");

            switch (fragment){
                case "Hotel_View":
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Hotel", hotel);
                    bundle.putString("Hotel Name",hotelID);
                    Navigation.findNavController(getView()).navigate(R.id.action_hotel_manager_hotel_edit_to_hotel_manager_hotel_view, bundle);
                    break;

                case "Hotel_Manage":
                    Navigation.findNavController(getView()).navigate(R.id.action_hotel_manager_hotel_edit_to_hotel_manager_home);
                    break;
            }
        });

        bt_EditHotel.setOnClickListener(v -> {
           verifyData();
        });


       hotel_features_listener();

       hotel_photos_listener();
    }

    public void hotel_features_listener() {
        if (hotelFeatures == null) {
            hotelFeatures = new HotelFeature(getResources().getStringArray(R.array.Features));
            tv_Features_Selected.setText("Features");
        }

        String[] featuresKeys = new String[hotelFeatures.getHotelFeature()];
        boolean[] featuresValues = new boolean[hotelFeatures.getHotelFeature()];
        ArrayList<Integer> featuresSelected = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Boolean> entry : hotelFeatures.getFeatures().entrySet()) {
            featuresKeys[index] = entry.getKey();
            featuresValues[index] = entry.getValue();
            if (entry.getValue()) {
                featuresSelected.add(index);
            }
            index++;
        }

        String featurename = "";
        for (int i = 0; i < featuresSelected.size(); i++) {
            featurename = featurename + featuresKeys[featuresSelected.get(i)];
            if (i != featuresSelected.size() - 1) {
                featurename = featurename + ", ";
            }
        }
        tv_Features_Selected.setText(featurename);

        bt_Features.setOnClickListener(view -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
            mBuilder.setTitle("Selected features of hotel");
            mBuilder.setMultiChoiceItems(featuresKeys, featuresValues, (dialogInterface, position, isChecked) -> {
                if (isChecked) {
                    featuresSelected.add(position);
                    hotelFeatures.setFeatures_Value(featuresKeys[position], true);
                } else {
                    featuresSelected.remove(Integer.valueOf(position));
                    hotelFeatures.setFeatures_Value(featuresKeys[position], false);
                }
            });

            mBuilder.setPositiveButton("Ok", (dialogInterface, which) -> {
                String item = "";
                for (int i = 0; i < featuresSelected.size(); i++) {
                    item = item + featuresKeys[featuresSelected.get(i)];
                    if (i != featuresSelected.size() - 1) {
                        item = item + ", ";
                    }
                }
                Log.e("Features", hotelFeatures.toString());
                tv_Features_Selected.setText(item);
            });

            mBuilder.setNegativeButton("Dismiss", (dialogInterface, i) -> dialogInterface.dismiss());

            mBuilder.setNeutralButton("Clear all", (dialogInterface, which) -> {
                for (int i = 0; i < featuresValues.length; i++) {
                    featuresValues[i] = false;
                    featuresSelected.clear();
                    hotelFeatures.setFeatures_Value(featuresKeys[i], false);
                }
                tv_Features_Selected.setText(",");
            });

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        });
    }

    private void hotel_photos_listener() {
        ll_Hotel_Cover_Photo.setOnClickListener(v -> {
            openUploadDialog(PICK_COVER_IMAGE_REQUEST);
        });

        ll_Hotel_Photos.setOnClickListener(v -> {
            openUploadDialog(PICK_OTHERS_IMAGE_REQUEST);
        });
    }

    private void openUploadDialog(int requestCode) {
        slideModelList  =new ArrayList<>();

        if(othersphotos == null){
            othersphotos = new ArrayList<>();
        }

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.fragment_dialog_upload_photos);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCancelable(false);

        dialog.create();

        mainslider= dialog.findViewById(R.id.hotel_slider_upload);
        mainslider.bringToFront();

        tv_popupMenu = dialog.findViewById(R.id.tv_popupMenu);

        Button bt_chose = dialog.findViewById(R.id.bt_chose_photos);
        Button bt_save = dialog.findViewById(R.id.bt_save_photos);
        Button bt_reset = dialog.findViewById(R.id.bt_reset);

        ImageButton bt_close = dialog.findViewById(R.id.bt_close_upload_photo);

        switch (requestCode){
            case PICK_COVER_IMAGE_REQUEST:
                if(coverPhoto != null){
                    slideModelList.add(new SlideModel(String.valueOf(coverPhoto),"", ScaleTypes.FIT));
                }
                break;
            case PICK_OTHERS_IMAGE_REQUEST:
                if(!othersphotos.isEmpty()){
                    for(Uri photo : othersphotos){
                        slideModelList.add(new SlideModel(String.valueOf(photo),"", ScaleTypes.FIT));
                    }
                }
                break;
        }
        mainslider.setImageList(slideModelList, ScaleTypes.FIT);
        mainslider.stopSliding();
        sliderClick(requestCode);

        bt_chose.setOnClickListener(v -> openFileChooser(requestCode));

        bt_save.setOnClickListener(v -> {
            slideModelList.clear();
            dialog.dismiss();
        });

        bt_reset.setOnClickListener(v -> {
            switch (requestCode){
                case PICK_COVER_IMAGE_REQUEST:
                    StorageReference imageDeleteCover = FirebaseStorage.getInstance().getReferenceFromUrl(hotel.getCoverPhoto());
                    imageDeleteCover.delete();
                    coverPhoto = null;
                    break;

                case PICK_OTHERS_IMAGE_REQUEST:
                    for(String url : hotel.getOtherPhotos()){
                        StorageReference imageDeleteOthers = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                        imageDeleteOthers.delete();
                    }
                    othersphotos.clear();
                    break;
            }
            slideModelList.clear();
            mainslider.setImageList(slideModelList, ScaleTypes.FIT);
            mainslider.stopSliding();
            sliderClick(requestCode);
        });

        bt_close.setOnClickListener(v -> {
            slideModelList.clear();
            mainslider.setImageList(slideModelList, ScaleTypes.FIT);
            mainslider.stopSliding();
            sliderClick(requestCode);
            dialog.cancel();
        });

        dialog.show();
    }

    private void openFileChooser(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        switch (requestCode){
            case PICK_OTHERS_IMAGE_REQUEST:
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                break;
        }
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri photoUri;

        if(resultCode == getActivity().RESULT_OK){
            if (requestCode == PICK_OTHERS_IMAGE_REQUEST) {
                if (data.getClipData() != null) {
                    //Multiple
                    int total_items = data.getClipData().getItemCount();
                    for(int i=0; i < total_items; i++){
                        photoUri = data.getClipData().getItemAt(i).getUri();
                        othersphotos.add(photoUri);
                        slideModelList.add(new SlideModel(String.valueOf(photoUri),"",ScaleTypes.FIT));
                    }
                    mainslider.setImageList(slideModelList, ScaleTypes.FIT);
                    mainslider.stopSliding();
                }
                else if (data.getData() != null) {
                    //Single
                    photoUri = data.getData();
                    othersphotos.add(photoUri);
                    slideModelList.add(new SlideModel(String.valueOf(photoUri),"",ScaleTypes.FIT));
                    mainslider.setImageList(slideModelList, ScaleTypes.FIT);
                    mainslider.stopSliding();
                }
                sliderClick(PICK_OTHERS_IMAGE_REQUEST);
            }

            if (requestCode == PICK_COVER_IMAGE_REQUEST) {
                //Single
                slideModelList.clear();
                coverPhoto = data.getData();
                slideModelList.add(new SlideModel(String.valueOf(coverPhoto),"",ScaleTypes.FIT));
                mainslider.setImageList(slideModelList, ScaleTypes.FIT);
                mainslider.stopSliding();

                sliderClick(PICK_COVER_IMAGE_REQUEST);
            }

        }
    }

    private void sliderClick(int requestCode) {
        mainslider.setItemClickListener(e -> {
            Context wrapper = new ContextThemeWrapper(getContext(), R.style.popupMenuStyle);
            PopupMenu popupMenu = new PopupMenu(wrapper, tv_popupMenu, Gravity.CENTER,0,0);

            //parm 2 is menu id, param 3 is position of this menu item in menu items list, param 4 is title of the menu
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "View Full");
            popupMenu.getMenu().getItem(0).setIcon(R.drawable.ic_view_full);
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "Delete");
            popupMenu.getMenu().getItem(1).setIcon(R.drawable.ic_delete_data);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(true);
            }

            final float[] rotate = {0};

            popupMenu.setOnMenuItemClickListener(item -> {
                ImageView iv_photo_full = dialog.findViewById(R.id.iv_photo_full);

                switch (item.getItemId()){
                    case 0:
                        ConstraintLayout cl_upload_photo_home = dialog.findViewById(R.id.cl_upload_photo_home);
                        cl_upload_photo_home.getLayoutParams().height = ConstraintLayout.LayoutParams.MATCH_PARENT; // LayoutParams: android.view.ViewGroup.LayoutParams
                        cl_upload_photo_home.requestLayout();//It is necesary to refresh the screen

                        switch (requestCode){
                            case PICK_COVER_IMAGE_REQUEST:
                                Glide.with(this)
                                        .load(coverPhoto)
                                        .centerInside()
                                        .into(iv_photo_full);
                                break;

                            case PICK_OTHERS_IMAGE_REQUEST:
                                Glide.with(this)
                                        .load(othersphotos.get(e))
                                        .centerInside()
                                        .into(iv_photo_full);
                                break;
                        }

                        ConstraintLayout cl_upload_photo = dialog.findViewById(R.id.cl_upload_photo);
                        cl_upload_photo.setVisibility(View.GONE);

                        ConstraintLayout cl_photo_full = dialog.findViewById(R.id.cl_photo_full);
                        cl_photo_full.setVisibility(View.VISIBLE);

                        ImageButton bt_close_full = dialog.findViewById(R.id.bt_close_full);
                        ImageButton bt_rotate_left = dialog.findViewById(R.id.bt_rotate_left);
                        ImageButton bt_rotate_right = dialog.findViewById(R.id.bt_rotate_right);

                        bt_close_full.setOnClickListener(v -> {
                            cl_upload_photo_home.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

                            cl_photo_full.setVisibility(View.GONE);
                            cl_upload_photo.setVisibility(View.VISIBLE);
                        });

                        bt_rotate_right.setOnClickListener(v -> {
                            rotate[0] += 90;
                            if(rotate[0] >= 360){
                                rotate[0] = 0;
                            }

                            iv_photo_full.setRotation(rotate[0]);
                            switch (requestCode){
                                case PICK_COVER_IMAGE_REQUEST:
                                    Glide.with(this)
                                            .load(coverPhoto)
                                            .centerInside()
                                            .into(iv_photo_full);
                                    break;

                                case PICK_OTHERS_IMAGE_REQUEST:
                                    Glide.with(this)
                                            .load(othersphotos.get(e))
                                            .centerInside()
                                            .into(iv_photo_full);
                                    break;
                            }

                        });

                        bt_rotate_left.setOnClickListener(v -> {
                            rotate[0] -= 90;
                            if(rotate[0] <= -360){
                                rotate[0] = 0;
                            }

                            iv_photo_full.setRotation(rotate[0]);
                            switch (requestCode){
                                case PICK_COVER_IMAGE_REQUEST:
                                    Glide.with(this)
                                            .load(coverPhoto)
                                            .centerInside()
                                            .into(iv_photo_full);
                                    break;

                                case PICK_OTHERS_IMAGE_REQUEST:
                                    Glide.with(this)
                                            .load(othersphotos.get(e))
                                            .centerInside()
                                            .into(iv_photo_full);
                                    break;
                            }

                        });

                        bt_rotate_right.performClick();
                        bt_rotate_left.performClick();
                        break;

                    case 1:
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setTitle("Delete");
                        dialog.setMessage("Delete this image");
                        dialog.setCancelable(true);

                        dialog.setPositiveButton("Yes", (dialog1, id) -> {
                            Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                            switch (requestCode){
                                case PICK_COVER_IMAGE_REQUEST:
                                    StorageReference imageDeleteCover = FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(coverPhoto));
                                    imageDeleteCover.delete();

                                    coverPhoto = null;
                                    slideModelList.clear();
                                    break;

                                case PICK_OTHERS_IMAGE_REQUEST:
                                    StorageReference imageDeleteOthers = FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(othersphotos.get(e)));
                                    imageDeleteOthers.delete();

                                    othersphotos.remove(e);
                                    slideModelList.remove(e);
                                    break;
                            }

                            mainslider.setImageList(slideModelList, ScaleTypes.FIT);
                        });

                        dialog.setNegativeButton("No", (dialog2, id) -> {
                            // User cancelled the dialog
                            dialog2.dismiss();
                        });

                        AlertDialog alert = dialog.create();
                        alert.show();
                        break;
                }

                return true;
            });

            popupMenu.show();
        });
    }

    private void verifyData(){

        boolean error = false;
        ////////////// PERSONAL DATA /////////////////
        String name = et_Name.getText().toString().trim();
        float starts = rb_Stars.getRating();
        String phone = "";
        ////////////// ROOMS DATA /////////////////
        String rooms = et_TotalRooms.getText().toString().trim();
        int total_rooms = 0;

        String price = et_Price.getText().toString().trim();
        float price_room = 0;
        ////////////// ADDRESS /////////////////
        String country = ccp_country.getSelectedCountryNameCode();
        String city = et_City.getText().toString().trim();
        String address_string = et_Address.getText().toString().trim();


        ////////////// DESCRIPTION /////////////////
        String description = et_Description.getText().toString().trim();


        if(name.isEmpty()){
            et_Name.setError("Hotel name is required");
            et_Name.requestFocus();
            error = true;
        }
        else{
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
        }

        if(starts == 0){
            tv_Hotel_Stars.setError("Hotel stars is required");
            tv_Hotel_Stars.requestFocus();
            error = true;
        }


        if(ccp_PhoneCode.isValidFullNumber()){
            phone = ccp_PhoneCode.getFormattedFullNumber();
        }
        if(phone.isEmpty() || !ccp_PhoneCode.isValidFullNumber()){
            et_Phone.setError("Phone is required or is not valid");
            et_Phone.requestFocus();
            error = true;
        }


        if(!rooms.isEmpty()){
            total_rooms = Integer.parseInt(et_TotalRooms.getText().toString().trim());
            if(total_rooms <= 1){
                et_TotalRooms.setError("Total rooms is required and greater than 1");
                et_TotalRooms.requestFocus();
                error = true;
            }
        }
        else {
            et_TotalRooms.setError("Total rooms is required and greater than 1");
            et_TotalRooms.requestFocus();
            error = true;
        }


        if(!price.isEmpty()){
            price_room = Float.parseFloat(et_Price.getText().toString().trim());
            if(price_room <= 1){
                et_Price.setError("Price the rooms is required and greater than 1");
                et_Price.requestFocus();
                error = true;
            }
        }
        else {
            et_Price.setError("Price the rooms is required and greater than 1");
            et_Price.requestFocus();
            error = true;
        }

        if(city.isEmpty()){
            Toast.makeText(getContext(), "You did not enter a city", Toast.LENGTH_LONG).show();
            et_City.setError("City name is required");
            et_City.requestFocus();
            error = true;
        }

        if(address_string.isEmpty()){
            et_Address.setError("Address name is required");
            et_Address.requestFocus();
            error = true;
        }



        if(tv_Features_Selected.getText().toString().trim().isEmpty()){
            tv_Features_Selected.setError("Select at least one feature");
            tv_Features_Selected.requestFocus();
            error = true;
        }

        if(description.isEmpty() || description.length() < 20){
            et_Description.setError("Description is required");
            et_Description.requestFocus();
            error = true;
        }

        if(coverPhoto == null){
            tv_title_cover_photo.setError("Select cover photo");
            tv_title_cover_photo.requestFocus();
            error = true;
        }

        if(othersphotos.isEmpty()){
            tv_title_others_photo.setError("Select others photos");
            tv_title_others_photo.requestFocus();
            error = true;
        }

        if(error){
            return;
        }

        Address address = new Address(country, city, address_string,coordinates.latitude,coordinates.longitude);

        hotel.setName(name);
        hotel.setPhone(phone);
        hotel.setAddress(address);
        hotel.setStars(starts);
        hotel.setTotal_Rooms(total_rooms);
        hotel.setPrice(price_room);
        hotel.setDescription(description);
        hotel.setFeature(hotelFeatures);

        changeOnFirebase();

    }

    private void changeOnFirebase() {
        hotelID = getArguments().getString("Hotel Name");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        CollectionReference hotelsRef = db.collection("hotels");
        Query query = hotelsRef.whereEqualTo("name", hotelID).limit(1);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
            String docId = documentSnapshot.getId();

            db.collection("hotels").document(docId)
                    .set(hotel)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Hotel has been registered successfully!", Toast.LENGTH_LONG).show();
                            changePhotosOnFirebase();
                        } else {
                            Toast.makeText(getContext(), "Failed to register changes! Try again!", Toast.LENGTH_LONG).show();
                        }
                    });
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to fetch hotel! Try again!", Toast.LENGTH_LONG).show();
        });
    }




    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void changePhotosOnFirebase() {
        if (othersphotos != null && coverPhoto != null) {
            // Code for showing progressDialog while uploading
            boolean upadatel_cover = false;
            boolean upadatel_others = false;
            if (!String.valueOf(coverPhoto).toLowerCase().startsWith("https://")) {
                upadatel_cover = true;
            }
            for(Uri photo : othersphotos) {
                if (!String.valueOf(photo).toLowerCase().startsWith("https://")) {
                    upadatel_others = true;
                }
            }
            if(upadatel_cover && upadatel_others){
               uploadCoverPhoto(true);
            }
            if(upadatel_cover && !upadatel_others){
               uploadCoverPhoto(false);
            }
            if(!upadatel_cover && upadatel_others){
               uploadOthersPhotos();
            }
            if(!upadatel_cover && !upadatel_others){
                bt_hotel_edit_back.performClick();
            }


        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }



    private void uploadCoverPhoto(boolean value) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");

        if (!String.valueOf(coverPhoto).toLowerCase().startsWith("https://")) {
            progressDialog.show();

            if (!hotel.getCoverPhoto().isEmpty()) {
                FirebaseStorage.getInstance().getReferenceFromUrl(hotel.getCoverPhoto()).delete();
            }
            String coverPhotoFileName = GenerateUniqueIds.generateId() + "." + getFileExtension(coverPhoto);
            StorageReference coverPhotoRef = FirebaseStorage.getInstance().getReference().child("Cover").child(coverPhotoFileName);
            UploadTask uploadTask = coverPhotoRef.putFile(coverPhoto);

            uploadTask.addOnProgressListener(snapshot -> {
                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
            }).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return coverPhotoRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference hotelsRef = db.collection("hotels");

                    Query query = hotelsRef.whereEqualTo("name", hotelID).limit(1);

                    query.get().addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot hotelDoc = querySnapshot.getDocuments().get(0);
                            String hotelId = hotelDoc.getId();

                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("coverPhoto", downloadUri.toString());

                            hotelsRef.document(hotelId).update(updateData).addOnSuccessListener(aVoid -> {
                                hotel.setCoverPhoto(downloadUri.toString());
                                progressDialog.dismiss();
                                if (value) {
                                    uploadOthersPhotos();
                                } else {
                                    bt_hotel_edit_back.performClick();
                                }
                            }).addOnFailureListener(e -> {
                                Log.e("Error", e.getMessage());
                                progressDialog.dismiss();
                            });
                        }
                    }).addOnFailureListener(e -> {
                        Log.e("Error", e.getMessage());
                        progressDialog.dismiss();
                    });
                } else {
                    Log.e("Error", task.getException().getMessage());
                    progressDialog.dismiss();
                }
            });
        }
    }


    private void uploadOthersPhotos(){
            if (othersphotos != null && coverPhoto != null) {
                // Code for showing progressDialog while uploading
                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                // Get a reference to the Firestore document
                DocumentReference hotelRef = FirebaseFirestore.getInstance().collection("hotels").document(hotelID);

                // Upload the cover photo
                StorageReference coverRef = FirebaseStorage.getInstance().getReference().child(hotelID).child("Cover").child(GenerateUniqueIds.generateId() + "." + getFileExtension(coverPhoto));
                UploadTask coverUploadTask = coverRef.putFile(coverPhoto);
                coverUploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Get the download URL for the cover photo
                    return coverRef.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri coverDownloadUri = task.getResult();
                        // Set the cover photo URL in the Firestore document
                        hotelRef.update("coverPhoto", coverDownloadUri.toString());
                        // Upload the other photos
                        List<String> listothersphotos = new ArrayList<>();
                        int numPhotos = othersphotos.size();
                        for (Uri photo : othersphotos) {
                            StorageReference otherRef = FirebaseStorage.getInstance().getReference().child(hotelID).child("Others").child(GenerateUniqueIds.generateId() + "." + getFileExtension(photo));
                            UploadTask otherUploadTask = otherRef.putFile(photo);
                            otherUploadTask.continueWithTask(task1 -> {
                                if (!task1.isSuccessful()) {
                                    throw task1.getException();
                                }
                                // Get the download URL for the other photo
                                return otherRef.getDownloadUrl();
                            }).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Uri otherDownloadUri = task1.getResult();
                                    listothersphotos.add(otherDownloadUri.toString());
                                    if (listothersphotos.size() == numPhotos) {
                                        // Set the other photos URLs in the Firestore document
                                        hotelRef.update("otherPhotos", listothersphotos);
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getContext(), task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            } else {
                Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
            }
    }
}



