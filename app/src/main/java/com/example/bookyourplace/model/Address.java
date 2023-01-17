package com.example.bookyourplace.model;

import java.io.Serializable;

public class Address implements Serializable {

    private String country;
    private String city;
    private String address;
    private String zipcode;



    public Address(){
    }

    public Address(String country, String city, String address, String zipcode) {
        this.country = country;
        this.city = city;
        this.address = address;
        this.zipcode = zipcode;
    }


    //////////////// GETS BEGIN ////////////////
    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public String getZipcode() {
        return zipcode;
    }


    //////////////// GETS END ////////////////

    //////////////// SETS BEGIN ////////////////
    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }



}
