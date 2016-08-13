package com.byteshaft.hairrestorationcenter.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.byteshaft.hairrestorationcenter.R;

import java.util.ArrayList;


public class EducationFragment extends Fragment {

    private View mBaseView;
    private static EducationAdapter sAdapter;
    private static ProgressDialog sProgressDialog;
    private RecyclerView mRecyclerView;
    private static CustomView sViewHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_education, container, false);
        mRecyclerView = (RecyclerView) mBaseView.findViewById(R.id.recycler_view_education);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(1);
        mRecyclerView.setHasFixedSize(true);
        return mBaseView;
    }

    // custom RecyclerView class for inflating customView
    class EducationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<String> item;

        public EducationAdapter(ArrayList<String> categories)   {
            this.item = categories;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delegate_education,
                    parent, false);
            sViewHolder = new CustomView(view);
            return sViewHolder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            sViewHolder.textViewOffers.setText(item.get(position));
        }

        @Override
        public int getItemCount() {
            return item.size();
        }
    }

    // custom class getting view item by giving view in constructor.
    public static class CustomView extends RecyclerView.ViewHolder {
        public TextView textViewOffers;
        public ImageView imageView;
        public TextView textViewDescription;
        public TextView textViewDate;

        public CustomView(View itemView) {
            super(itemView);
            textViewOffers = (TextView) itemView.findViewById(R.id.offers);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            textViewDescription = (TextView) itemView.findViewById(R.id.description);
            textViewDate = (TextView) itemView.findViewById(R.id.date);

        }
    }


}
