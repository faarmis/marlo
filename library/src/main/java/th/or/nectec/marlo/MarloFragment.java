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
import android.view.View;
import android.view.View.OnClickListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public abstract class MarloFragment extends SupportMapFragment implements OnMapReadyCallback, OnClickListener {

    private GoogleMap googleMap;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        ViewUtils.addViewFinder(this);
        ViewUtils.addUndoButton(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.view_finder) {
            onViewfinderClick(cameraPosition());
        } else if (view.getId() == R.id.undo) {
            onUndoClick();
        }
    }

    protected abstract void onViewfinderClick(LatLng viewfinderTarget);

    private LatLng cameraPosition() {
        return googleMap.getCameraPosition().target;
    }

    protected abstract void onUndoClick();

    protected GoogleMap getGoogleMap() {
        return googleMap;
    }

}
