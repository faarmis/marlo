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

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;

public abstract class MarloFragment extends SupportMapFragment implements OnMapReadyCallback, OnClickListener {

    private GoogleMap googleMap;
    protected MarkerFactory markerFactory;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getMapAsync(this);
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
    }

    protected View findViewBy(@IdRes int id) {
        ViewGroup rootView = (ViewGroup) getView();
        if (rootView == null) throw new IllegalArgumentException("Root View should not be null");
        return rootView.findViewById(id);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.marlo_view_finder || view.getId() == R.id.marlo_mark) {
            onViewfinderClick(cameraPosition());
        }
    }

    public void setMarkerFactory(MarkerFactory markerFactory) {
        this.markerFactory = markerFactory;
    }

    protected abstract void onViewfinderClick(LatLng viewfinderTarget);

    private LatLng cameraPosition() {
        return googleMap.getCameraPosition().target;
    }

    protected GoogleMap getGoogleMap() {
        return googleMap;
    }

}
