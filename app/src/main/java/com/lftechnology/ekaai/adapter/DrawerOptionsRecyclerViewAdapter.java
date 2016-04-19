package com.lftechnology.ekaai.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lftechnology.ekaai.Ekaai;
import com.lftechnology.ekaai.R;
import com.lftechnology.ekaai.constant.AppConstant;
import com.lftechnology.ekaai.helper.ItemTouchHelperAdapter;
import com.lftechnology.ekaai.helper.ItemTouchHelperViewHolder;
import com.lftechnology.ekaai.helper.OnStartDragListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Grishma Shrestha <grishmashrestha@lftechnology.com> on 4/19/16.
 */
public class DrawerOptionsRecyclerViewAdapter extends RecyclerView.Adapter<DrawerOptionsRecyclerViewAdapter.ItemViewHolder> implements ItemTouchHelperAdapter {
    private final List<String> mDataset;
    private final OnStartDragListener mDragStartListener;
    private String mSelectedConversion;

    public DrawerOptionsRecyclerViewAdapter(String[] dataset, OnStartDragListener mDragStartListener, String selectedConversion) {
        mDataset = new ArrayList(Arrays.asList(dataset));
        this.mDragStartListener = mDragStartListener;
        mSelectedConversion = selectedConversion;
    }

    @Override
    public DrawerOptionsRecyclerViewAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drawer_right_recycler_view_item, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(v);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        holder.textView.setText(Html.fromHtml(mDataset.get(position)));

        // Start a drag whenever the handle view it touched
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mDataset, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        updateUserPreference();
        return true;
    }

    private void updateUserPreference() {
        Gson gson = new Gson();
        String jsonDataset = gson.toJson(mDataset);
        SharedPreferences sharedPref = Ekaai.getContext().getSharedPreferences(AppConstant.EKAAI, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(mSelectedConversion, jsonDataset);
        editor.apply();
    }

    /**
     * Simple implementation of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        public final TextView textView;
        public final ImageView handleView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_option);
            handleView = (ImageView) itemView.findViewById(R.id.burger);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.WHITE);
            itemView.getBackground().setAlpha(100);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

}