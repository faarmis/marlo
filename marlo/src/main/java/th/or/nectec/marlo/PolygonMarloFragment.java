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
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import th.or.nectec.marlo.model.PolygonData;
import th.or.nectec.marlo.option.DefaultPolygonMarkerOptionFactory;
import th.or.nectec.marlo.option.DefaultPolygonOptionFactory;
import th.or.nectec.marlo.option.PolygonOptionFactory;

import java.util.List;
import java.util.Stack;

public class PolygonMarloFragment extends MarloFragment {

    private final Stack<PolygonData> multiPolygon = new Stack<>();
    private final PolygonData singlePolygon = new PolygonData();

    private Mode mode = Mode.SINGLE;
    private PolygonOptionFactory polygonOptionFactory;

    public PolygonMarloFragment() {
        super();
        markerOptionFactory = new DefaultPolygonMarkerOptionFactory();
        polygonOptionFactory = new DefaultPolygonOptionFactory();
    }

    public static PolygonMarloFragment newInstance(Mode mode) {
        PolygonMarloFragment fragment = new PolygonMarloFragment();
        fragment.mode = mode;
        return fragment;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setPolygonOptionFactory(PolygonOptionFactory polygonOptionFactory) {
        this.polygonOptionFactory = polygonOptionFactory;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ViewUtils.addPolygonToolsMenu(this);
        findViewBy(R.id.marlo_boundary).setVisibility(mode == Mode.MULTI ? View.VISIBLE : View.GONE);
        multiPolygon.push(new PolygonData());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.marlo_hole) {
            getActivePolygonData().newHole();
            changeToHoleState();
        } else if (view.getId() == R.id.marlo_boundary) {
            multiPolygon.push(new PolygonData());
            changeToBoundaryState();
        } else if (view.getId() == R.id.marlo_undo) {
            undo();
        } else {
            super.onClick(view);
        }
    }

    private void changeToHoleState() {
        getActivePolygonData().setCurrentState(PolygonData.State.HOLE);
    }

    private void changeToBoundaryState() {
        getActivePolygonData().setCurrentState(PolygonData.State.BOUNDARY);
    }

    public PolygonData getActivePolygonData() {
        return mode == Mode.SINGLE ? singlePolygon : multiPolygon.peek();
    }

    @Override
    public void mark(LatLng markPoint) {
        SoundUtility.play(getContext(), R.raw.thumpsoundeffect);

        Marker marker = googleMap.addMarker(markerOptionFactory.build(this, markPoint));
        PolygonData activePolygon = getActivePolygonData();
        boolean success = activePolygon.addMarker(marker);
        if (!success) {
            onMarkHoleOutBound(markPoint, activePolygon.getPolygon().getPoints());
            marker.remove();
        }

        PolygonDrawUtils.createPolygon(googleMap, activePolygon, polygonOptionFactory.build(this));
    }

    protected void onMarkHoleOutBound(LatLng target, List<LatLng> points) {
        Toast.makeText(getActivity(), "Out of polygon boundary ", Toast.LENGTH_SHORT).show();
    }

    public boolean undo() {
        PolygonData polygonData = getActivePolygonData();
        if (polygonData.isEmpty()) {
            return false;
        }
        boolean removed = polygonData.removeLastMarker();

        if (removed && polygonData.isEmpty() && mode == Mode.MULTI && multiPolygon.size() > 1) {
            multiPolygon.pop();
        }

        PolygonDrawUtils.createPolygon(googleMap, polygonData, polygonOptionFactory.build(this));
        return true;
    }

    public Stack<PolygonData> getPolygons() {
        return multiPolygon;
    }

    public PolygonData getPolygon() {
        return singlePolygon;
    }

    public enum Mode {
        SINGLE,
        MULTI
    }
}
