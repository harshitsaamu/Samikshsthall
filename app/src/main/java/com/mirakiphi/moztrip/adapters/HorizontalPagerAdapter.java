package com.mirakiphi.moztrip.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mirakiphi.moztrip.HotelActivity;
import com.mirakiphi.moztrip.Model;
import com.mirakiphi.moztrip.R;

import java.util.List;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by GIGAMOLE on 7/27/16.
 */
public class HorizontalPagerAdapter extends PagerAdapter {
    //Changes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Model> list = null;

    private boolean mIsTwoWay;

    public HorizontalPagerAdapter(final Context context, final boolean isTwoWay, List<Model> list) {
        Log.i(TAG, "HorizontalPagerAdapter: " + String.valueOf(list));
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mIsTwoWay = isTwoWay;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View view;
        TextView textName;
        ImageView img_item;
            view = mLayoutInflater.inflate(com.mirakiphi.moztrip.R.layout.item, container, false);
        img_item = (ImageView) view.findViewById(R.id.img_item);
        textName = (TextView) view.findViewById(R.id.textName);
            final Model model = list.get(position);
        textName.setText(model.getTpName());
        Glide.with(mContext).load(model.getTpReference()).into(img_item);

//            Utils.setupItem(mContext, view, LIBRARIES[position]);

            container.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newi=new Intent(mContext, HotelActivity.class);
                newi.putExtra("place_id", model.getTpPlaceID());
                mContext.startActivity(newi);
            }
        });
                return view;

    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }
}

