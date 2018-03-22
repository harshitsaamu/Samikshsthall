package com.mirakiphi.moztrip;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by anuragmaravi on 30/01/17.
 */

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.MyViewHolder>  {

    private Context mContext;
    private List<Model> movieList = null;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tourist_places, parent, false);
        return new RestaurantsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Model movie = movieList.get(position);
        holder.textViewTP.setText(movie.getResName());
        Glide.with(mContext).load(movie.getResPhoto()).into(holder.imageViewTP);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HotelActivity.class);
                intent.putExtra("place_id",movie.getResPlaceID());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public RestaurantsAdapter(Context mContext, List<Model> movieList) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewTP;
        public TextView textViewTP;
        public MyViewHolder(View view) {
            super(view);
            imageViewTP = (ImageView) view.findViewById(R.id.imageViewTP);
            textViewTP = (TextView) view.findViewById(R.id.textViewTP);
        }
    }
}
