package com.example.bookyourplace.model.traveler.search_hotel;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookyourplace.R;

public class MyViewHolder extends RecyclerView.ViewHolder{

    private final TextView name;
    private final TextView city;
    private final TextView price;

    private final RatingBar rating;
    ImageView image;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        name=itemView.findViewById(R.id.search_listName);
        city=itemView.findViewById(R.id.search_listCity);
        price=itemView.findViewById(R.id.search_listPrice);
        image=itemView.findViewById(R.id.search_listPhoto);
        rating=itemView.findViewById(R.id.search_listRating);
    }

    public String getName() {
        return name.getText().toString();
    }

    public String getCity() {
        return city.getText().toString();
    }

    public String getPrice() {
        return price.getText().toString();
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setCity(String city) {
        this.city.setText(city);
    }

    public void setPrice(String price) {
        this.price.setText(price);
    }

    public void setRating (float rating) {this.rating.setRating(rating);}

    @Override
    public String toString() {
        return "MyViewHolderClass{" +
                "name=" + name +
                ", city=" + city +
                ", price=" + price +
                '}';
    }
}
