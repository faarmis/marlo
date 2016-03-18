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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;

public abstract class MarloFragment extends SupportMapFragment implements OnMapReadyCallback, OnClickListener {

    protected MarkerFactory markerFactory;
    private GoogleMap googleMap;
    private boolean myLocationEnable;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getMapAsync(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (myLocationEnable) PlayLocationService.getInstance(getContext()).connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        PlayLocationService.getInstance(getContext()).disconnect();
    }

    @RequiresPermission(anyOf = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION})
    public void enableMyLocationButton() {
        myLocationEnable = true;
        PlayLocationService.getInstance(getContext()).connect();
        updateMyLocationVisibility();
    }

    private void updateMyLocationVisibility() {
        View myLocation = findViewBy(R.id.marlo_gps);
        if (myLocation != null) {
            myLocation.setVisibility(myLocationEnable ? View.VISIBLE : View.GONE);
        }
    }

    protected View findViewBy(@IdRes int id) {
        ViewGroup rootView = (ViewGroup) getView();
        if (rootView == null) throw new IllegalArgumentException("Root View should not be null");
        return rootView.findViewById(id);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setCompassEnabled(true);
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng), 500, null);
            }
        });
        ViewUtils.addViewFinder(this);
        ViewUtils.addGpsLocationButton(this);

        updateMyLocationVisibility();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.marlo_view_finder || view.getId() == R.id.marlo_mark) {
            onViewfinderClick(cameraPosition());
        } else if (view.getId() == R.id.marlo_gps) {
            moveToMyLocation();
        }
    }

    protected abstract void onViewfinderClick(LatLng viewfinderTarget);

    private LatLng cameraPosition() {
        return googleMap.getCameraPosition().target;
    }

    private void moveToMyLocation() {
        Location lastKnowLocation = PlayLocationService.getInstance(getContext()).getLastKnowLocation();
        if (lastKnowLocation != null) {
            LatLng latLng = new LatLng(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng), 1000, null);
        }
    }

    public void setMarkerFactory(MarkerFactory markerFactory) {
        this.markerFactory = markerFactory;
    }

    protected GoogleMap getGoogleMap() {
        return googleMap;
    }

}
