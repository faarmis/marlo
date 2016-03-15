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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import th.or.nectec.marlo.model.PolygonData;

import java.util.Stack;

public class PolygonMarloFragment extends MarloFragment {

    private final Stack<PolygonData> multiPolygon = new Stack<>();
    private PolygonData singlePolygon = new PolygonData();
    private State drawingState = State.BOUNDARY;
    private Mode mode = Mode.MULTI;

    public static PolygonMarloFragment newInstance(Mode mode) {
        PolygonMarloFragment fragment = new PolygonMarloFragment();
        fragment.mode = mode;
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewUtils.addHoleButton(this);
        if (mode == Mode.MULTI) {
            ViewUtils.addNewPolygonButton(this);
        }
        multiPolygon.push(new PolygonData());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.hole) {
            changeToHoleState();
        } else if (view.getId() == R.id.new_polygon) {
            multiPolygon.push(new PolygonData());
            changeToBoundary();
        } else {
            super.onClick(view);
        }
    }

    private void changeToHoleState() {
        drawingState = State.HOLE;
    }

    private void changeToBoundary() {
        drawingState = State.BOUNDARY;
    }

    @Override
    protected void onViewfinderClick(LatLng target) {
        SoundUtility.play(getContext(), R.raw.thumpsoundeffect);

        Marker marker = getGoogleMap().addMarker(getMarkerOptions(target));
        switch (drawingState) {
            case BOUNDARY:
                getActivePolygonData().getBoundary().push(marker);
                break;
            case HOLE:
                getActivePolygonData().getHole().push(marker);
                break;
        }
        PolygonDrawUtils.createPolygon(getGoogleMap(), getActivePolygonData());
    }

    @Override
    protected void onUndoClick() {
        undo();
    }

    public void undo() {
        Stack<Marker> holeMarker = getActivePolygonData().getHole();
        if (!holeMarker.isEmpty()) {
            holeMarker.peek().remove();
            holeMarker.pop();
        } else {
            Stack<Marker> boundary = getActivePolygonData().getBoundary();
            if (!boundary.isEmpty()) {
                changeToBoundary();
                boundary.peek().remove();
                boundary.pop();
            }

            if (boundary.isEmpty() && mode == Mode.MULTI && multiPolygon.size() > 1) {
                multiPolygon.pop();
            }
        }
        PolygonDrawUtils.createPolygon(getGoogleMap(), getActivePolygonData());
    }

    private MarkerOptions getMarkerOptions(LatLng target) {
        return new MarkerOptions()
                .position(target)
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(drawingState == State.BOUNDARY
                        ? BitmapDescriptorFactory.HUE_MAGENTA
                        : BitmapDescriptorFactory.HUE_AZURE));
    }

    public PolygonData getActivePolygonData() {
        return mode == Mode.SINGLE ? singlePolygon : multiPolygon.peek();
    }

    private enum State {
        BOUNDARY,
        HOLE,
    }

    public enum Mode {
        SINGLE,
        MULTI
    }

}
