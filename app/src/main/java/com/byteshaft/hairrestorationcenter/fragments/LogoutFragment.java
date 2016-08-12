package com.byteshaft.hairrestorationcenter.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byteshaft.hairrestorationcenter.R;

/**
 * Created by husnain on 8/6/16.
 */
public class LogoutFragment extends Fragment {

    private View mBaseView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.delegate_logout_fragment, container, false);
        return mBaseView;
    }
}
