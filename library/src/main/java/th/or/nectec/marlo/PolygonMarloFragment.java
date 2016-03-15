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
import com.google.android.gms.maps.model.MarkerOptions;
import th.or.nectec.marlo.model.PolygonData;

import java.util.Stack;

public class PolygonMarloFragment extends MarloFragment {

    private PolygonData polygonData = new PolygonData();
    private State drawingState = State.BOUNDARY;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewUtils.addHoleButton(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.hole) {
            changeToHoleState();
        } else {
            super.onClick(view);
        }
    }

    private void changeToHoleState() {
        drawingState = State.HOLE;
    }

    @Override
    protected void onUndoClick() {
        undo();
    }

    @Override
    protected void onViewfinderClick(LatLng target) {
        SoundUtility.play(getContext(), R.raw.thumpsoundeffect);

        addMarker(drawingState == State.BOUNDARY ? polygonData.getBoundary() : polygonData.getHole());
        PolygonDrawUtils.createPolygon(getGoogleMap(), polygonData);
    }

    private void addMarker(Stack<Marker> markers) {
        Marker marker = getGoogleMap().addMarker(markerOnCenterOfScreen());
        markers.push(marker);

    }

    private MarkerOptions markerOnCenterOfScreen() {
        return new MarkerOptions()
                .position(getGoogleMap().getCameraPosition().target)
                .draggable(true);
    }

    public void undo() {
        Stack<Marker> holeMarker = polygonData.getHole();
        if (!holeMarker.isEmpty()) {
            holeMarker.peek().remove();
            holeMarker.pop();
        } else {
            Stack<Marker> boundary = polygonData.getBoundary();
            if (!boundary.isEmpty()) {
                changeToBoundary();
                boundary.peek().remove();
                boundary.pop();
            }
        }
        PolygonDrawUtils.createPolygon(getGoogleMap(), polygonData);
    }

    private void changeToBoundary() {
        drawingState = State.BOUNDARY;
    }

    enum State {
        BOUNDARY,
        HOLE,
    }

}
