package com.mirakiphi.moztrip.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mirakiphi.moztrip.R;

/**
 * Created by GIGAMOLE on 8/18/16.
 */
public class Utils {

    public static void setupItem(Context context, final View view, final LibraryObject libraryObject) {
        final TextView txt = (TextView) view.findViewById(R.id.textName);

        final TextView rank = (TextView) view.findViewById(R.id.txt_rank);
        rank.setText(libraryObject.getRank());

        final ImageView img = (ImageView) view.findViewById(R.id.img_item);
        Glide.with(context).load("https://s-media-cache-ak0.pinimg.com/originals/6d/81/7e/6d817ed9cc645bdea6c8ffffb577f3c1.jpg").into(img);
    }

    public static class LibraryObject {

        private String mTitle;
        private String mRank;
        private int mRes;


        public LibraryObject(final int res, final String title, final String rank) {
            mRes = res;
            mTitle = title;
            mRank = rank;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(final String title) {
            mTitle = title;
        }

        public String getRank() {
            return mRank;
        }

        public void setRank(final String rank) {
            mRank = rank;
        }

        public int getRes() {
            return mRes;
        }

        public void setRes(final int res) {
            mRes = res;
        }
    }
}
