package com.example.bookyourplace.model.traveler.search_hotel;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SearchHotel extends Fragment {

    EditText mSearchField;
    ImageButton mSearchBtn;
    RecyclerView mResultList;
    TextView mResultInfo;

    ImageView mweatherIcon;

    TextView mCountryName;

    TextView mCityName;

    TextView mWeatherCondition;

    TextView mTemperature;

    String cityName;

    double Temperature;

    String condition;

    String country;

    TextView weatherResult;

    CardView weatherCard;



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
        setQuery(root);
        return root;
    }

    private void setQuery(View root) {
        Query query = FirebaseFirestore.getInstance().collection("hotels");
        SearchHotelArgs args = SearchHotelArgs.fromBundle(getArguments());
        String locality = args.getInputText();
        if (!locality.trim().isEmpty()) {
            query = query.whereGreaterThanOrEqualTo("address.city", locality)
                    .whereLessThanOrEqualTo("address.city", locality + "\uf8ff")
                    .orderBy("address.city", Query.Direction.ASCENDING);
        }

        loadData(query,root);
    }

    private void clickListener(View root) {

        mSearchBtn.setOnClickListener(v -> {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            Query query = FirebaseFirestore.getInstance().collection("hotels").whereGreaterThanOrEqualTo("address.city", mSearchField.getText().toString().trim())
                    .whereLessThanOrEqualTo("address.city", mSearchField.getText().toString().trim() + "\uf8ff")
                    .orderBy("address.city", Query.Direction.ASCENDING);


            if (mSearchField.getText().toString().trim().isEmpty()) {
                mResultInfo.setText("All");
                loadData(query,root);
                weatherCard.setVisibility(View.GONE);
                weatherResult.setVisibility(View.GONE);
            } else {
                mResultInfo.setText(mSearchField.getText().toString());
                loadData(query,root);
                weatherCard.setVisibility(View.VISIBLE);
                weatherResult.setVisibility(View.VISIBLE);
                }
        });
    }

    private void loadData(Query query, View root) {
    mResultList.setLayoutManager(new LinearLayoutManager(getActivity()));

        new Thread (()->{
            OkHttpClient client = new OkHttpClient();

            String apiKey = "b078a1fc847ce0ae5df77854bd57507d";
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + mSearchField.getText().toString().trim() + "&appid=" + apiKey;

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String responseString = response.body().string();
                            JSONObject jsonObject = new JSONObject(responseString);
                            // Extract city name
                            cityName = jsonObject.getString("name");

                            // Extract temperature
                            JSONObject main = jsonObject.getJSONObject("main");
                            Temperature = main.getDouble("temp")-273.15;

                            // Extract weather condition
                            JSONArray weather = jsonObject.getJSONArray("weather");
                            JSONObject weatherObj = weather.getJSONObject(0);
                            condition = weatherObj.getString("description");
                            String iconCode = weatherObj.getString("icon");
                            String iconUrl = "https://openweathermap.org/img/w/" + iconCode + ".png";

                            // Extract country
                            JSONObject sys = jsonObject.getJSONObject("sys");
                            country = sys.getString("country");
                            Locale l = new Locale("", country);
                            String countryName = l.getDisplayCountry();

                            requireActivity().runOnUiThread(() -> {
                                mCityName.setText(cityName);
                                mTemperature.setText((String.format("%.0f °C",Temperature)));
                                mCountryName.setText(countryName);
                                mWeatherCondition.setText(condition);
                                Picasso.get().load(iconUrl).into(mweatherIcon);
                            });


                        } catch (JSONException e) {}

                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                }
            });
        }).start();



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
                NavDirections action = SearchHotelDirections.actionSearchHotelToHotelViewer("Search",hotelId,"","");
                Navigation.findNavController(root).navigate(action);
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
        mCityName= root.findViewById(R.id.city_name_textView);
        mCountryName=root.findViewById(R.id.country_name_textView);
        mTemperature = root.findViewById(R.id.temperature_textView);
        mWeatherCondition = root.findViewById(R.id.description_textView);
        mweatherIcon = root.findViewById(R.id.weatherIcon);
        weatherResult = root.findViewById(R.id.weatherResultInfoText);
        weatherCard = root.findViewById(R.id.weatherCard);

        mResultInfo = root.findViewById(R.id.searchResultsInfoText);
        SearchHotelArgs args = SearchHotelArgs.fromBundle(getArguments());
        String inputText = args.getInputText();
        if(inputText!=null) {
            mResultInfo.setText(inputText);
        }
        else{
            mResultInfo.setText("All");
        }
    }
}

