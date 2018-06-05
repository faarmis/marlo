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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import th.or.nectec.marlo.model.MarloCoord;

public class PointMarloFragment extends MarloFragment {

    private Deque<Marker> markers = new ArrayDeque<>();

    private int maxPoint = 1;

    public void setMaxPoint(int maxPoint) {
        this.maxPoint = maxPoint;
        Deque<Marker> markers = new ArrayDeque<>(maxPoint);
        boolean full = false;
        for (Marker marker : this.markers) {
            if (!full) {
                markers.push(marker);
                if (markers.size() == maxPoint) {
                    full = true;
                }
            } else {
                marker.remove();
            }
        }
        this.markers = markers;
    }

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
        if (!mute) SoundUtility.play(getContext(), R.raw.thumpsoundeffect);
        if (markers.size() == maxPoint) markers.removeLast().remove();
        Marker marker = googleMap.addMarker(markOptFactory.build(this, markPoint));
        markers.push(marker);
    }

    @Override
    public void hideToolsMenu() {
        findViewBy(R.id.marlo_mark).setVisibility(View.GONE);
        findViewBy(R.id.marlo_undo).setVisibility(View.GONE);
    }

    @Override
    public void showToolsMenu() {
        findViewBy(R.id.marlo_mark).setVisibility(View.VISIBLE);
        findViewBy(R.id.marlo_undo).setVisibility(View.VISIBLE);
    }

    public boolean undo() {
        if (!markers.isEmpty()) {
            markers.pop().remove();
            return true;
        }
        return false;
    }

    public List<MarloCoord> getCoordinates() {
        ArrayList<MarloCoord> coordinates = new ArrayList<>();
        for (Marker marker : markers) {
            coordinates.add(MarloCoord.fromMarker(marker));
        }
        return coordinates;
    }

}
