/*
 * Copyright (c) 2017 NECTEC
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

import android.content.Context;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

import th.or.nectec.marlo.option.DefaultMarkerOptionFactory;
import th.or.nectec.marlo.option.MarkerOptionFactory;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * Base fragment of Marlo Project, Contain common process such as Setup map, Add common ui component
 * and Enable my location feature
 */
public abstract class MarloFragment extends SupportMapFragment implements OnMapReadyCallback, OnClickListener {

    private static final String TAG = "MarloFragment";

    /**
     * Request code on for request location setting when can't get last knowLocation
     * after call moveToMyLocation. Activity can handle result of user action by onActivityResult()
     */
    public static final int REQUEST_LOCATION_SETTINGS = 10512;

    protected MarkerOptionFactory markOptFactory = new DefaultMarkerOptionFactory();
    protected GoogleMap googleMap;

    protected final OnCheckedChangeListener onMapTypeButtonChange = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            googleMap.setMapType(isChecked ? GoogleMap.MAP_TYPE_NORMAL : GoogleMap.MAP_TYPE_SATELLITE);
        }
    };

    protected boolean mute = false;
    private PlayLocationService locationService;
    private boolean myLocationEnable;
    private CompoundButton mapTypeButton;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        locationService = new PlayLocationService(getContext());
        getMapAsync(this);

        ViewUtils.addViewFinder(this);
        ViewUtils.addMyLocationButton(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (myLocationEnable) {
            locationService.connect();
        }
    }

    /**
     * set true for mute sound effect on mark. default is false
     *
     * @param mute true for mute sound effect default is false
     */
    public void setMute(boolean mute) {
        this.mute = mute;
    }

    @Override
    public void onStop() {
        super.onStop();
        locationService.disconnect();
    }

    /**
     * enable myLocation feature to use moveToMyLocation.
     * If need built-in myLocationButton use enableMyLocationButton() instead.
     *
     * @throws SecurityException if permission not granted
     */
    @RequiresPermission(anyOf = {
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION})
    public void enableMyLocation() throws SecurityException {
        myLocationEnable = true;
        if (googleMap != null) {
            googleMap.setMyLocationEnabled(true);
        }
        locationService.connect();
    }

    /**
     * enable default My Location button with myLocation feature
     *
     * @throws SecurityException if permission not granted
     */
    @RequiresPermission(anyOf = {
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION})
    public void enableMyLocationButton() {
        Context context = getContext();
        if (checkSelfPermission(context, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                && checkSelfPermission(context, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            throw new IllegalStateException("Location permission must be granted");
        }
        enableMyLocation();
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

    /**
     * @param googleMap object of google maps
     */
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

        mapTypeButton = ViewUtils.addMapTypeButton(this);
        updateMyLocationVisibility();
    }

    /**
     * Switch map type between Normal and Satellite. This method must after GoogleMap is ready
     */
    public void toggleMapType() {
        mapTypeButton.performClick();
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

    /**
     * animate to current location. if can't get last know location it will automatically prompt
     * user to change setting
     *
     * @exception IllegalStateException if call before enableMyLocation() or enableMyLocationButton()
     */
    public void moveToMyLocation() {
        if (!myLocationEnable)
            throw new IllegalStateException("Must enable myLocation feature before");
        Location lastKnowLocation = locationService.getLastKnowLocation();
        if (lastKnowLocation != null) {
            LatLng latLng = new LatLng(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16), 1000, null);
        } else {
            locationService.requestLocationSetting(getActivity(), REQUEST_LOCATION_SETTINGS);
        }
    }

    /**
     * execute undo process of fragment
     *
     * @return true if undo process of fragment was done, false when fragment have noting to undo
     */
    public abstract boolean undo();

    /**
     * see Google's MarkerOption for implement factory
     *
     * @param markerOptionFactory factory use to create marker for GoogleMap
     */
    public void setMarkerOptionFactory(MarkerOptionFactory markerOptionFactory) {
        this.markOptFactory = markerOptionFactory;
    }

}
