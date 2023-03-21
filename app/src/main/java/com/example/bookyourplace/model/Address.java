package com.example.bookyourplace.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
public class Address implements Serializable {

    private String country;
    private String city;
    private String address;
    private MyCoordinates coordinates;

    public Address(){
    }

    public Address(String country, String city, String address) {
        this.country = country;
        this.city = city;
        this.address = address;
    }

    public Address(String country, String city, String address, double latitude, double longitude) {
        this.country = country;
        this.city = city;
        this.address = address;
        this.coordinates = new MyCoordinates(latitude,longitude);

    }

    public Address(String country, String city, String address, MyCoordinates coordinates) {
        this.country = country;
        this.city = city;
        this.address = address;

        this.coordinates = coordinates;
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

    public LatLng getCoordinates() {
        if(coordinates == null){
            return null;
        }else{
            return coordinates.getLocation();
        }

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

    public void setCoordinates(double latitude, double longitude) {
        coordinates = new MyCoordinates(latitude,longitude);
    }

    //////////////// SETS END ////////////////
}

