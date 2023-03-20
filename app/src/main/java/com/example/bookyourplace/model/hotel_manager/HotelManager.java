package com.example.bookyourplace.model.hotel_manager;

import com.example.bookyourplace.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HotelManager extends User implements Serializable {
    ////////////////     HOTELS     ////////////////
    private List<String> hotels;

    public HotelManager(){
        this.hotels = new ArrayList<>();
    }

    public HotelManager(String name, String surname, String email, String phone, String password) {
        super(name, surname, email, phone, password);
        this.hotels = new ArrayList<>();
    }

    //////////////// GETS BEGIN ////////////////
    public List<String> getHotels() {
        return hotels;
    }
    //////////////// GETS END ////////////////

    //////////////// SETS BEGIN ////////////////
    public void setHotels(List<String> hotels) {
        this.hotels = hotels;
    }

    //////////////// SETS END ////////////////

}

