package com.mirakiphi.moztrip.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mirakiphi.moztrip.PlaceActivity;
import com.mirakiphi.moztrip.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by harshit on 07-04-2017.
 */

public class articles_adapter extends RecyclerView.Adapter<articles_adapter.viewholder> {
    private Context context;
    private LayoutInflater inflater;
    private List<article_elements> data= Collections.emptyList();
    public articles_adapter(Context context,List<article_elements> data)
    {
        this.data=data;
        inflater=LayoutInflater.from(context);
        this.context=context;
    }
    @Override
    public viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.news_card,parent,false);
        viewholder holder=new viewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final viewholder holder, int position) {
        final article_elements current=data.get(position);
        holder.article_title.setText(current.title);
        holder.article_desc.setText(current.description);
        Glide.with(context).load(current.image_url).into(holder.article_image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlaceActivity.class);
                intent.putExtra("Name", current.description);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    class viewholder extends RecyclerView.ViewHolder
    { TextView article_title,article_desc;
        ImageView article_image;
        private viewholder (View itemView)
        {
            super(itemView);
            article_image=(ImageView)itemView.findViewById(R.id.article_imageview);
            article_title=(TextView)itemView.findViewById(R.id.article_titleview);
            article_desc=(TextView)itemView.findViewById(R.id.article_subtextview);
        }
    }
}
