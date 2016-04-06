/*
 * Copyright (c) 2016 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package th.or.nectec.marlo;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresPermission;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import th.or.nectec.marlo.option.DefaultMarkerOptionFactory;
import th.or.nectec.marlo.option.MarkerOptionFactory;

/**
 * Base fragment of Marlo Project, Contain common process such as Setup map, Add common ui component
 * and Enable my location feature
 */
public abstract class MarloFragment extends SupportMapFragment implements OnMapReadyCallback, OnClickListener {

    private static final String TAG = "MarloFragment";

    protected MarkerOptionFactory markerOptionFactory = new DefaultMarkerOptionFactory();
    protected GoogleMap googleMap;

    protected final OnCheckedChangeListener onMapTypeButtonChange = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            googleMap.setMapType(isChecked ? GoogleMap.MAP_TYPE_NORMAL : GoogleMap.MAP_TYPE_SATELLITE);
        }
    };

    private boolean myLocationEnable;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getMapAsync(this);

        ViewUtils.addViewFinder(this);
        ViewUtils.addGpsLocationButton(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (myLocationEnable) {
            PlayLocationService.getInstance(getContext()).connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        PlayLocationService.getInstance(getContext()).disconnect();
    }

    @RequiresPermission(anyOf = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION})
    public void enableMyLocationButton() throws SecurityException {
        myLocationEnable = true;
        if (googleMap != null) {
            googleMap.setMyLocationEnabled(true);
        }
        PlayLocationService.getInstance(getContext()).connect();
        updateMyLocationVisibility();
    }

    private void updateMyLocationVisibility() {
        View myLocationButton = findViewBy(R.id.marlo_gps);
        if (myLocationButton != null) {
            myLocationButton.setVisibility(myLocationEnable ? View.VISIBLE : View.GONE);
        }
    }

    protected final View findViewBy(@IdRes int id) {
        ViewGroup rootView = (ViewGroup) getView();
        if (rootView == null) throw new IllegalArgumentException("Root view is null");
        return rootView.findViewById(id);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setCompassEnabled(true);
        try {
            googleMap.setMyLocationEnabled(myLocationEnable);
        } catch (SecurityException se) {
            if (BuildConfig.DEBUG) Log.e(TAG, "onMapReady", se);
        }

        ViewUtils.addMapTypeButton(this);
        updateMyLocationVisibility();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.marlo_view_finder || view.getId() == R.id.marlo_mark) {
            mark(googleMap.getCameraPosition().target);
        } else if (view.getId() == R.id.marlo_gps) {
            moveToMyLocation();
        }
    }

    /**
     * execute mark process of fragment
     *
     * @param markPoint latlng position to mark
     */
    public abstract void mark(LatLng markPoint);

    private void moveToMyLocation() {
        Location lastKnowLocation = PlayLocationService.getInstance(getContext()).getLastKnowLocation();
        if (lastKnowLocation != null) {
            LatLng latLng = new LatLng(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16), 1000, null);
        }
    }

    /**
     * execute undo process of fragment
     *
     * @return true if undo process of fragment was done, false when fragment have noting to undo
     */
    public abstract boolean undo();

    public void setMarkerOptionFactory(MarkerOptionFactory markerOptionFactory) {
        this.markerOptionFactory = markerOptionFactory;
    }

}
