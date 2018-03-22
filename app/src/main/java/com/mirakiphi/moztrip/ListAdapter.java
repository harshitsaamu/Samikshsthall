package com.mirakiphi.moztrip;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by anuragmaravi on 30/01/17.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder>  {

    private Context mContext;
    private List<Model> movieList = null;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tourist_places1, parent, false);
        return new ListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Model movie = movieList.get(position);
        holder.textViewTP1.setText(movie.getTpName());
        Glide.with(mContext).load(movie.getTpReference()).into(holder.imageViewTP1);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HotelActivity.class);
                intent.putExtra("place_id", movie.getTpPlaceID());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
               // Toast.makeText(mContext, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public ListAdapter(Context mContext, List<Model> movieList) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewTP1;
        public TextView textViewTP1;
        public MyViewHolder(View view) {
            super(view);
            imageViewTP1 = (ImageView) view.findViewById(R.id.imageViewTP1);
            textViewTP1 = (TextView) view.findViewById(R.id.textViewTP1);
        }
    }
}
