package com.example.bookyourplace.model.hotel_manager;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bookyourplace.R;


public class Home  extends Fragment {


    TextView tv_NameMensage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {

            @Override
            public void handleOnBackPressed() {

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.hotel_manager_fragment_home, container, false);

        initializeElements(root);

        return root;
    }
    private void initializeElements(View root) {

        tv_NameMensage = root.findViewById(R.id.hotel_text); /// need to change that its just for check!!

    }


}
