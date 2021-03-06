package com.example.android.xyztouristattractions.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.xyztouristattractions.Attraction;
import com.example.android.xyztouristattractions.Constants;
import com.example.android.xyztouristattractions.R;
import com.example.android.xyztouristattractions.ResturentConstruction;
import com.example.android.xyztouristattractions.Utils;
import com.example.android.xyztouristattractions.provider.ResturentAttractions;
import com.example.android.xyztouristattractions.service.UtilityService;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.android.xyztouristattractions.provider.ResturentAttractions.ResturentATTRACTIONS;

/**
 * A simple {@link Fragment} subclass.
 */
public class Resturent extends Fragment {
    private Resturent.AttractionAdapter mAdapter;
    private LatLng mLatestLocation;
    private int mImageSize;
    private boolean mItemClicked;

    public Resturent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mImageSize = getResources().getDimensionPixelSize(R.dimen.image_size)
                * Constants.IMAGE_ANIM_MULTIPLIER;

        mLatestLocation = Utils.getLocation(getActivity());
        List<ResturentConstruction> attractions = loadAttractionsFromLocation(mLatestLocation);
        mAdapter = new Resturent.AttractionAdapter(getActivity(), attractions);

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        AttractionsRecyclerView recyclerView =
                (AttractionsRecyclerView) view.findViewById(android.R.id.list);
        recyclerView.setEmptyView(view.findViewById(android.R.id.empty));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mItemClicked = false;
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mBroadcastReceiver, UtilityService.getLocationUpdatedIntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location =
                    intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
            if (location != null) {
                mLatestLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mAdapter.mAttractionList = loadAttractionsFromLocation(mLatestLocation);
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    private static List<ResturentConstruction> loadAttractionsFromLocation(final LatLng curLatLng) {
        String closestCity = ResturentAttractions.getClosestCity(curLatLng);
        if (closestCity != null) {
            List<ResturentConstruction> attractions = ResturentATTRACTIONS.get(closestCity);
            if (curLatLng != null) {
                Collections.sort(attractions,
                        new Comparator<ResturentConstruction>() {
                            @Override
                            public int compare(ResturentConstruction lhs, ResturentConstruction rhs) {
                                double lhsDistance = SphericalUtil.computeDistanceBetween(
                                        lhs.location, curLatLng);
                                double rhsDistance = SphericalUtil.computeDistanceBetween(
                                        rhs.location, curLatLng);
                                return (int) (lhsDistance - rhsDistance);
                            }
                        }
                );
            }
            return attractions;
        }
        return null;
    }

    private class AttractionAdapter extends RecyclerView.Adapter<Resturent.ViewHolder>
            implements Resturent.ItemClickListener {

        public List<ResturentConstruction> mAttractionList;
        private Context mContext;

        public AttractionAdapter(Context context, List<ResturentConstruction> attractions) {
            super();
            mContext = context;
            mAttractionList = attractions;
        }

        @Override
        public Resturent.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.list_resturent, parent, false);
            return new Resturent.ViewHolder(view, this);
        }

        @Override
        public void onBindViewHolder(Resturent.ViewHolder holder, int position) {
            ResturentConstruction attraction = mAttractionList.get(position);

            holder.mTitleTextView.setText(attraction.name);
            holder.mDescriptionTextView.setText(attraction.description);
            Glide.with(mContext)
                    .load(attraction.imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.empty_photo)
                    .override(mImageSize, mImageSize)
                    .into(holder.mImageView);

            String distance =
                    Utils.formatDistanceBetween(mLatestLocation, attraction.location);
            if (TextUtils.isEmpty(distance)) {
                holder.mOverlayTextView.setVisibility(View.GONE);
            } else {
                holder.mOverlayTextView.setVisibility(View.VISIBLE);
                holder.mOverlayTextView.setText(distance);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mAttractionList == null ? 0 : mAttractionList.size();
        }

        @Override
        public void onItemClick(View view, int position) {

        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView mTitleTextView;
        TextView mDescriptionTextView;
        TextView mOverlayTextView;
        ImageView mImageView;
        Resturent.ItemClickListener mItemClickListener;

        public ViewHolder(View view, Resturent.ItemClickListener itemClickListener) {
            super(view);
            mTitleTextView = (TextView) view.findViewById(android.R.id.text1);
            mDescriptionTextView = (TextView) view.findViewById(android.R.id.text2);
            mOverlayTextView = (TextView) view.findViewById(R.id.overlaytext);
            mImageView = (ImageView) view.findViewById(android.R.id.icon);
            mItemClickListener = itemClickListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
