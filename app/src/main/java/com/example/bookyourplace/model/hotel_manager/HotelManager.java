package com.example.bookyourplace.model.hotel_manager;

import com.example.bookyourplace.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HotelManager extends User implements Serializable {
    private List<String> hotels; //This declares a private variable hotels of type List<String> which will be
    // used to store a list of hotels that the hotel manager is associated with.



    public HotelManager(){this.hotels = new ArrayList<>();} //This is a constructor for the HotelManager class.
    // It creates a new empty ArrayList of type String and assigns it to the hotels variable.

  /*  public HotelManager(String name, String surname, String phone, String email) {
        super(name, surname, phone, email);
        this.hotels = new ArrayList<>();
    }
*/
    public HotelManager(String name, String surname, String email, String phone, String password) { //This constructor is taking five arguments name, surname, email, phone
        // , and password and it's calling the parent class User constructor to pass these arguments to the parent constructor
        // and it creates a new empty ArrayList of type String and assigns it to the hotels variable.

        super(name, surname, email, phone, password);
        this.hotels = new ArrayList<>();
    }

    //////////////// GETS BEGIN ////////////////

}

