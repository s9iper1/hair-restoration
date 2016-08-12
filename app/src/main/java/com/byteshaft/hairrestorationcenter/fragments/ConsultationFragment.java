package com.byteshaft.hairrestorationcenter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.byteshaft.hairrestorationcenter.HealthInformation;
import com.byteshaft.hairrestorationcenter.MainActivity;
import com.byteshaft.hairrestorationcenter.R;
import com.byteshaft.hairrestorationcenter.utils.AppGlobals;

/**
 * Created by husnain on 8/6/16.
 */
public class ConsultationFragment extends Fragment implements View.OnClickListener {

    private View mBaseView;
    private ImageButton mFrontSide;
    private ImageButton mBackSide;
    private ImageButton mTopSide;
    private ImageButton mLetSide;
    private ImageButton mRightSide;

    private Button mUploadButton;
    private Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.delegate_consultation_fragment, container, false);
        mFrontSide = (ImageButton) mBaseView.findViewById(R.id.front_side);
        mLetSide = (ImageButton) mBaseView.findViewById(R.id.left_side);
        mBackSide = (ImageButton) mBaseView.findViewById(R.id.back_side);
        mTopSide = (ImageButton) mBaseView.findViewById(R.id.top_side);
        mRightSide = (ImageButton) mBaseView.findViewById(R.id.right_side);
        mUploadButton = (Button) mBaseView.findViewById(R.id.upload_button);

        mFrontSide.setOnClickListener(this);
        mRightSide.setOnClickListener(this);
        mTopSide.setOnClickListener(this);
        mBackSide.setOnClickListener(this);
        mLetSide.setOnClickListener(this);
        mUploadButton.setOnClickListener(this);

        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        return mBaseView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.front_side:
                startActivityForResult(intent, 1);
                break;
            case R.id.left_side:
                break;
            case R.id.right_side:
                break;
            case R.id.top_side:
                break;
            case R.id.back_side:
                break;
            case R.id.upload_button:
                startActivity(new Intent(AppGlobals.getContext(), HealthInformation.class));
        }
    }
}
