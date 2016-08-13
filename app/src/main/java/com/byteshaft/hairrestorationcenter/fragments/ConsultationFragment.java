package com.byteshaft.hairrestorationcenter.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.byteshaft.hairrestorationcenter.HealthInformation;
import com.byteshaft.hairrestorationcenter.R;
import com.byteshaft.hairrestorationcenter.utils.AppGlobals;
import com.byteshaft.hairrestorationcenter.utils.Helpers;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class ConsultationFragment extends Fragment implements View.OnClickListener {

    private View mBaseView;
    private CircularImageView mFrontSide;
    private CircularImageView mBackSide;
    private CircularImageView mTopSide;
    private CircularImageView mLeftSide;
    private CircularImageView mRightSide;
    private Button mUploadButton;
    private Intent intent;
    private String filePath;
    private Uri uriSavedImage;
    private HashMap<Integer, String> imagesHashMap;
    private final int[] requestCodes = {1, 2, 3, 4, 5};
    private int pressedButtonId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.consultation_fragment, container, false);
        imagesHashMap = new HashMap<>();
        mFrontSide = (CircularImageView) mBaseView.findViewById(R.id.front_side);
        mLeftSide = (CircularImageView) mBaseView.findViewById(R.id.left_side);
        mBackSide = (CircularImageView) mBaseView.findViewById(R.id.back_side);
        mTopSide = (CircularImageView) mBaseView.findViewById(R.id.top_side);
        mRightSide = (CircularImageView) mBaseView.findViewById(R.id.right_side);
        mUploadButton = (Button) mBaseView.findViewById(R.id.upload_button);
        mFrontSide.setOnClickListener(this);
        mRightSide.setOnClickListener(this);
        mTopSide.setOnClickListener(this);
        mBackSide.setOnClickListener(this);
        mLeftSide.setOnClickListener(this);
        mUploadButton.setOnClickListener(this);
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        return mBaseView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.front_side:
                if (imagesHashMap.containsKey(requestCodes[0])) {
                    pressedButtonId = 0;
                    removeItemFromArray(requestCodes[0], mFrontSide);
                } else {
                    pressedButtonId = view.getId();
                    dispatchTakePictureIntent(requestCodes[0]);
                }
                break;
            case R.id.left_side:
                if (imagesHashMap.containsKey(requestCodes[1])) {
                    pressedButtonId = 0;
                    removeItemFromArray(requestCodes[0], mLeftSide);
                } else {
                    pressedButtonId = view.getId();
                    dispatchTakePictureIntent(requestCodes[1]);
                }
                break;
            case R.id.right_side:
                if (imagesHashMap.containsKey(requestCodes[2])) {
                    pressedButtonId = 0;
                    removeItemFromArray(requestCodes[0], mRightSide);
                } else {
                    pressedButtonId = view.getId();
                    dispatchTakePictureIntent(requestCodes[2]);
                }
                break;
            case R.id.top_side:
                if (imagesHashMap.containsKey(requestCodes[3])) {
                    pressedButtonId = 0;
                    removeItemFromArray(requestCodes[0], mTopSide);
                } else {
                    pressedButtonId = view.getId();
                    dispatchTakePictureIntent(requestCodes[3]);
                }
                break;
            case R.id.back_side:
                if (imagesHashMap.containsKey(requestCodes[4])) {
                    pressedButtonId = 0;
                    removeItemFromArray(requestCodes[0], mBackSide);
                } else {
                    pressedButtonId = view.getId();
                    dispatchTakePictureIntent(requestCodes[4]);
                }
                break;
            case R.id.upload_button:
                startActivity(new Intent(AppGlobals.getContext(), HealthInformation.class));
        }
    }

    private void removeItemFromArray(final int item, final CircularImageView circularImageView) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Discard Image");
        alertDialogBuilder.setMessage("Do you want to remove this image?")
                .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                imagesHashMap.remove(item);
                circularImageView.setImageResource(0);
                switch (circularImageView.getId()) {
                    case R.id.front_side:
                        circularImageView.setImageResource(R.mipmap.front_side);
                        break;
                    case R.id.back_side:
                        circularImageView.setImageResource(R.mipmap.back_side);
                        break;
                    case R.id.top_side:
                        circularImageView.setImageResource(R.mipmap.top_side);
                        break;
                    case R.id.left_side:
                        circularImageView.setImageResource(R.mipmap.left_side);
                        break;
                    case R.id.right_side:
                        circularImageView.setImageResource(R.mipmap.right_side);
                        break;
                }
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public boolean contains(int[] array, final int key) {
        Arrays.sort(array);
        return Arrays.binarySearch(array, key) != -1;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (contains(requestCodes, requestCode) && resultCode == Activity.RESULT_OK) {
            if (!imagesHashMap.containsKey(requestCode)) {
                imagesHashMap.put(requestCode, filePath);
                setImageOnImageView(new File(filePath));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (contains(requestCodes, requestCode)) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("TAG", "Permission granted");
                openCamera(requestCode);
            } else {
                Toast.makeText(getActivity(), "Permission denied!"
                        , Toast.LENGTH_LONG).show();
            }
            return;

        }
    }

    private void setImageOnImageView(File imageFile) {
        switch (pressedButtonId) {
            case R.id.front_side:
                setImage(mFrontSide, imageFile);
                break;
            case R.id.left_side:
                setImage(mLeftSide, imageFile);
                break;
            case R.id.right_side:
                setImage(mRightSide, imageFile);
                break;
            case R.id.top_side:
                setImage(mTopSide, imageFile);
                break;
            case R.id.back_side:
                setImage(mBackSide, imageFile);
                break;
        }
    }

    private void setImage(final CircularImageView image, File file) {
        Picasso.with(getActivity()).load(file).resize(200, 200).centerCrop().into(new Target(){

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                image.setBackground(null);
                image.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(final Drawable errorDrawable) {
                Log.d("TAG", "FAILED");
            }

            @Override
            public void onPrepareLoad(final Drawable placeHolderDrawable) {
                Log.d("TAG", "Prepare Load");
            }
        });
    }


    private void dispatchTakePictureIntent(int requestCode) {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    requestCode);
        } else {
            openCamera(requestCode);
        }

    }

    private void openCamera(int requestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            filePath = Helpers.createDirectoryAndSaveFile();
            uriSavedImage = Uri.fromFile(new File(filePath));
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
            System.out.println(uriSavedImage);
            startActivityForResult(takePictureIntent, requestCode);
        }
    }
}
