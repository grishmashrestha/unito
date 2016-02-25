package com.lftechnology.unito;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Grishma Shrestha <grishmashrestha@lftechnology.com> on 2/25/16.
 */
public class LengthSlideScreenTopFragment extends Fragment {

    private static final String POSITION = "position";

    // TODO: Rename and change types of parameters
    private int mPosition;

    public LengthSlideScreenTopFragment() {
        // Required empty public constructor
    }

    public static LengthSlideScreenTopFragment newInstance(int position) {
        LengthSlideScreenTopFragment fragment = new LengthSlideScreenTopFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(POSITION);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_length_slider_top, container, false);
        TextView tv = (TextView) rootView.findViewById(R.id.length_scroll_top);
        tv.setText(""+mPosition+"");
        return rootView;
    }


}
