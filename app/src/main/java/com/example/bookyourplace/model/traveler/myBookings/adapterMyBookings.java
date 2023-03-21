package com.example.bookyourplace.model.traveler.myBookings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookyourplace.R;
import com.example.bookyourplace.model.hotel_manager.Hotel;
import com.example.bookyourplace.model.hotel_manager.HotelListAdapter;
import com.example.bookyourplace.model.traveler.Booking;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

public class adapterMyBookings extends RecyclerView.Adapter<adapterMyBookings.ViewHolder> {
    List<Booking> bookings;
    Hotel[]hotels;

    private static adapterMyBookings.OnItemClickListener bListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static void setOnItemClickListener(OnItemClickListener listener) {
        bListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView hotelName, hotelCity, nAdults,nKids, total,startDate, endDate;
        RatingBar hotelRating;
        ImageView hotelImg;
        ConstraintLayout expandableLayout;
        ImageButton expand;



        public ViewHolder(@NonNull View itemView , final adapterMyBookings.OnItemClickListener listener) {
            super(itemView);
            hotelName=itemView.findViewById(R.id.myBookings_listName);
            hotelCity=itemView.findViewById(R.id.myBookings_listCity);
            nAdults=itemView.findViewById(R.id.myBookings_adults);
            nKids=itemView.findViewById(R.id.myBookings_Kids);
            total=itemView.findViewById(R.id.mybookings_total);
            hotelRating=itemView.findViewById(R.id.myBookings_listRating);
            hotelImg=itemView.findViewById(R.id.myBookings_listPhoto);
            startDate=itemView.findViewById(R.id.mybookings_startDate);
            endDate=itemView.findViewById(R.id.mybookings_endDate);
            expandableLayout =itemView.findViewById(R.id.expandableLayout);
            expand = itemView.findViewById(R.id.myBookings_expandbtn);


            expand.setOnClickListener(v ->
            {
                Booking booking= bookings.get(getAdapterPosition());
                booking.setExpanded(!booking.isExpanded());
                notifyItemChanged(getAdapterPosition());
            });

        }
    }

    public adapterMyBookings(List<Booking>bookings,Hotel[]hotels) {
        this.bookings=bookings;
        this.hotels=hotels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View featureView = inflater.inflate(R.layout.traveler_bookings_list_item, parent, false);// inflate the layout
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(featureView , bListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.hotelName.setText(hotels[position].getName());
        holder.hotelCity.setText(hotels[position].getAddress().getCity());
        holder.hotelRating.setRating(hotels[position].getStars());
        holder.nAdults.setText(String.valueOf(bookings.get(position).getnAdults()));
        holder.nKids.setText(String.valueOf(bookings.get(position).getnChildren()));
        holder.total.setText(Float.toString(bookings.get(position).getPrice()));
        Picasso.get().load(hotels[position].getCoverPhoto()).into(holder.hotelImg);

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");//formating according to my need
        holder.startDate.setText(formatter.format(bookings.get(position).getEnterDate()));
        holder.endDate.setText(formatter.format(bookings.get(position).getExitDate()));

        boolean isExpanded= bookings.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}

