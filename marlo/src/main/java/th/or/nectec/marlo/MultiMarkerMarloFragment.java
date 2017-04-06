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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import th.or.nectec.marlo.model.Coordinate;

public class MultiMarkerMarloFragment extends MarloFragment {

    private final Stack<Marker> markers = new Stack<>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewUtils.addPolygonToolsMenu(this);
        findViewBy(R.id.marlo_hole).setVisibility(View.GONE);
        findViewBy(R.id.marlo_boundary).setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.marlo_undo) {
            undo();
        } else {
            super.onClick(view);
        }
    }

    @Override
    public void mark(LatLng markPoint) {
        SoundUtility.play(getContext(), R.raw.thumpsoundeffect);
        Marker marker = googleMap.addMarker(markOptFactory.build(this, markPoint));
        markers.push(marker);
    }

    public boolean undo() {
        if (!markers.empty()) {
            markers.pop().remove();
            return true;
        }
        return false;
    }

    public List<Coordinate> getCoordinates() {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        for (Marker marker : markers) {
            coordinates.add(Coordinate.fromMarker(marker));
        }
        return coordinates;
    }

}
