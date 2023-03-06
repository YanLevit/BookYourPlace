package com.example.bookyourplace.model.traveler.search_hotel;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookyourplace.R;
import com.example.bookyourplace.model.hotel_manager.Hotel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class SearchHotel extends Fragment {

    EditText mSearchField;
    ImageButton mSearchBtn;
    RecyclerView mResultList;
    TextView mResultInfo;

    private LinkedHashMap<Hotel,String> searchResults = new LinkedHashMap<>();
    private LinkedHashMap<Hotel,String> filteredResults = new LinkedHashMap<>();


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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.traveler_fragment_search_hotel, container, false);

        initializeElements(root);
        clickListener(root);

        return root;
    }

    private void clickListener(View root) {

        mSearchBtn.setOnClickListener(v -> {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            String searchQuery = mSearchField.getText().toString().trim().toLowerCase();
            if (!TextUtils.isEmpty(searchQuery)) {
                mResultInfo.setText(searchQuery);
                loadData(searchQuery, root);
            } else {
                Bundle bundle = getArguments();
                if (bundle != null) {
                    String homeSearchQuery = bundle.getString("inputText").toLowerCase();
                    if (TextUtils.isEmpty(homeSearchQuery.trim())) {
                        mResultInfo.setText("All");
                        loadData(homeSearchQuery,root);
                    } else {
                        mResultInfo.setText(homeSearchQuery);
                        loadData(homeSearchQuery, root);
                    }
                } else {
                    mResultInfo.setText("All");
                    loadData(searchQuery,root);
                }
            }
        });

    }

private void loadData(String searchQuery, View root) {
    mResultList.setLayoutManager(new LinearLayoutManager(getActivity()));
    Query query = FirebaseFirestore.getInstance().collection("hotels");

    if (!searchQuery.trim().isEmpty()) {
        query = query.whereGreaterThanOrEqualTo("address.city", searchQuery)
                .whereLessThanOrEqualTo("address.city", searchQuery + "\uf8ff")
                .orderBy("address.city", Query.Direction.ASCENDING);
    }

    FirestoreRecyclerOptions<Hotel> options = new FirestoreRecyclerOptions.Builder<Hotel>()
            .setQuery(query, Hotel.class)
            .build();

    FirestoreRecyclerAdapter<Hotel, MyViewHolder> adapter = new FirestoreRecyclerAdapter<Hotel, MyViewHolder>(options) {
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.traveler_search_list_item_layout, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Hotel model) {
            holder.setName(model.getName());
            holder.setCity(model.getAddress().getCity());
            holder.setPrice(Float.toString(model.getPrice()));
            holder.setRating(model.getStars());
            if (model.getCoverPhoto() != null && !model.getCoverPhoto().isEmpty()) {
                Picasso.get().load(model.getCoverPhoto()).into(holder.image);
            }

            holder.itemView.setOnClickListener(v -> {
                //go-to hotel page

                String hotelId = getSnapshots().getSnapshot(position).getId();
                Bundle bundle = new Bundle();
                bundle.putString("hotelId", hotelId);
                //bundle.putString("clickDetails", getSnapshots().getSnapshot(position).getId());
                bundle.putString("PreviousFragment", "Search");
                Navigation.findNavController(root).navigate(R.id.action_searchHotel_to_hotelViewer, bundle);
            });
        }
    };

    adapter.startListening();
    mResultList.setAdapter(adapter);
}

    private void initializeElements(View root) {

        mSearchField = root.findViewById(R.id.search_input_location);
        mSearchBtn = root.findViewById(R.id.search_btn);
        mResultList = root.findViewById(R.id.searchResults);
        mResultList.setLayoutManager(new LinearLayoutManager(this.getContext()));

        mResultInfo = root.findViewById(R.id.searchResultsInfoText);

        if(getArguments() != null){
            if(getArguments().getString("inputText") != null){
                String local = getArguments().getString("inputText");
                if(local.isEmpty()){
                    mResultInfo.setText("All");
                }
                else{
                    mResultInfo.setText(local);
                }
            }
        }
        else{
            mResultInfo.setText("All");
        }
    }
}

