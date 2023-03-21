package com.example.bookyourplace.model.hotel_manager;



import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookyourplace.R;
import com.example.bookyourplace.model.traveler.Booking;
import com.example.bookyourplace.model.traveler.Traveler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

public class ViewBookingsAdpater extends RecyclerView.Adapter<ViewBookingsAdpater.ViewHolder> {
    private final List<Booking> mBookings;

    private static final String TAG = "" ;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, start, end, adults, kids, total;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.manager_out_name);
            start = itemView.findViewById(R.id.manager_out_start);
            end = itemView.findViewById(R.id.manager_out_end);
            adults = itemView.findViewById(R.id.manager_out_adults);
            kids = itemView.findViewById(R.id.manager_out_kids);
            total = itemView.findViewById(R.id.manager_out_total);
            pic = itemView.findViewById(R.id.manager_out_pic);
        }
    }

    public ViewBookingsAdpater(List<Booking> mBookings) {
        this.mBookings = mBookings;
    }

    @NonNull
    @Override
    public ViewBookingsAdpater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View bookingView = inflater.inflate(R.layout.hotel_manager_client_booking_item, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(bookingView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = mBookings.get(position);

        getUserDetails(booking.getUserID(), holder);

        holder.adults.setText(Integer.toString(booking.getnAdults()));
        holder.kids.setText(Integer.toString(booking.getnChildren()));
        holder.total.setText(Float.toString(booking.getPrice()));

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");//formating according to my need
        holder.start.setText(formatter.format(booking.getEnterDate()));
        holder.end.setText(formatter.format(booking.getExitDate()));

    }

private void getUserDetails(String userID, ViewHolder holder) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef = db.collection("Traveler").document(userID);

    docRef.get().addOnSuccessListener(documentSnapshot -> {
        if (documentSnapshot.exists()) {
            Traveler user = documentSnapshot.toObject(Traveler.class);
            holder.name.setText(user.getName());
            if (!user.getImage().isEmpty())
                Picasso.get().load(user.getImage()).into(holder.pic);
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Log.w(TAG, "Error getting user details", e);
        }
    });
}
    @Override
    public int getItemCount() {
        return mBookings.size();
    }
}
