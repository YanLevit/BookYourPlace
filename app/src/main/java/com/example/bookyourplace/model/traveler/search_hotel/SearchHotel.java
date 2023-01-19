package com.example.bookyourplace.model.traveler.search_hotel;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookyourplace.R;
import com.google.firebase.firestore.FirebaseFirestore;


public class SearchHotel extends Fragment {

    EditText mSearchField;
    ImageButton mSearchBtn;
    RecyclerView mResultList;
    TextView mResultInfo;

    FirebaseFirestore db;
    private boolean fabsOn = false;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {

            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(getView()).navigate(R.id.action_searchHotel_to_traveler_home);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }









}
