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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import th.or.nectec.marlo.model.PolygonData;

import java.util.Stack;

public class MultiPolygonMarloFragment extends MarloFragment {

    private final Stack<PolygonData> polygonDataStack = new Stack<>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PolygonData polygonData = new PolygonData();
        polygonDataStack.push(polygonData);
    }

    @Override
    protected void onViewfinderClick(LatLng position) {
        PolygonData polygonData = polygonDataStack.peek();
        addMarker(polygonData.getBoundary());
        PolygonDrawUtils.createPolygon(getGoogleMap(), polygonData);
    }

    @Override
    protected void onUndoClick() {
        undo();
    }

    public void undo() {
        Stack<Marker> holeMarker = polygonDataStack.peek().getHole();
        if (!holeMarker.isEmpty()) {
            holeMarker.peek().remove();
            holeMarker.pop();
            PolygonDrawUtils.createPolygon(getGoogleMap(), polygonDataStack.peek());
        }

        Stack<Marker> markers = polygonDataStack.peek().getBoundary();
        if (holeMarker.isEmpty() && !markers.isEmpty()) {
            markers.peek().remove();
            markers.pop();
            PolygonDrawUtils.createPolygon(getGoogleMap(), polygonDataStack.peek());
        }
        if (polygonDataStack.size() > 1 && markers.isEmpty()) {
            polygonDataStack.pop();
        }
    }

    private void addMarker(Stack<Marker> markers) {
        Marker marker = getGoogleMap().addMarker(markerOnCenterOfScreen());
        markers.push(marker);
        SoundUtility.play(getContext(), R.raw.thumpsoundeffect);
    }

    private MarkerOptions markerOnCenterOfScreen() {
        return new MarkerOptions()
                .position(getGoogleMap().getCameraPosition().target)
                .draggable(true);
    }

}
