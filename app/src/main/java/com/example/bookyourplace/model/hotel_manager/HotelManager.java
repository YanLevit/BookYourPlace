package com.example.bookyourplace.model.hotel_manager;

import com.example.bookyourplace.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HotelManager extends User implements Serializable {
    private List<String> hotels;

    public HotelManager(){
        this.hotels = new ArrayList<>();
    }

    public HotelManager(String name, String surname, String phone, String email, boolean isGoogle) {
        super(name, surname, phone, email, isGoogle);
        this.hotels = new ArrayList<>();
    }

    public HotelManager(String name, String surname, String email, String phone, String password) {
        super(name, surname, email, phone, password);
        this.hotels = new ArrayList<>();
    }

    //////////////// GETS BEGIN ////////////////

}

