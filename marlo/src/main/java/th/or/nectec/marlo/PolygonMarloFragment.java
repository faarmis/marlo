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

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import th.or.nectec.marlo.model.Coordinate;
import th.or.nectec.marlo.model.Polygon;
import th.or.nectec.marlo.model.PolygonData;
import th.or.nectec.marlo.option.DefaultPolygonMarkerOptionFactory;
import th.or.nectec.marlo.option.DefaultPolygonOptionFactory;
import th.or.nectec.marlo.option.PolygonOptionFactory;

import java.util.List;
import java.util.Stack;

public class PolygonMarloFragment extends MarloFragment {

    private final Stack<PolygonData> multiPolygon = new Stack<>();
    private BitmapDescriptor activeMarkerIcon;
    private BitmapDescriptor passiveMarkerIcon;
    private PolygonOptionFactory polygonOptionFactory;

    private PolygonController controller = new PolygonController();

    public PolygonMarloFragment() {
        super();
        markerOptionFactory = new DefaultPolygonMarkerOptionFactory();
        polygonOptionFactory = new DefaultPolygonOptionFactory();

    }

    public static PolygonMarloFragment newInstance() {
        PolygonMarloFragment fragment = new PolygonMarloFragment();
        return fragment;
    }

    public void setPolygonOptionFactory(PolygonOptionFactory polygonOptionFactory) {
        this.polygonOptionFactory = polygonOptionFactory;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activeMarkerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        passiveMarkerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);

        ViewUtils.addPolygonToolsMenu(this);
        multiPolygon.push(new PolygonData());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.marlo_hole) {
            getActivePolygonData().newHole();
            getActivePolygonData().setCurrentState(PolygonData.State.HOLE);
        } else if (view.getId() == R.id.marlo_boundary) {
            multiPolygon.push(new PolygonData());
            getActivePolygonData().setCurrentState(PolygonData.State.BOUNDARY);
        } else if (view.getId() == R.id.marlo_undo) {
            undo();
        } else {
            super.onClick(view);

        }
    }

    public PolygonData getActivePolygonData() {
        return multiPolygon.peek();
    }

    private void setUpdateIconToLastMarker(BitmapDescriptor icon) {
        Marker lastMarker = getActivePolygonData().getLastMarker();
        if (lastMarker != null) {
            lastMarker.setIcon(icon);
        } else if (multiPolygon.size() > 1) {
            PolygonData topPolygon = multiPolygon.pop();
            multiPolygon.peek().getLastMarker().setIcon(icon);
            multiPolygon.push(topPolygon);
        }
    }

    @Override
    public void mark(LatLng markPoint) {
        SoundUtility.play(getContext(), R.raw.thumpsoundeffect);

        controller.mark(new Coordinate(markPoint));

        Marker marker = googleMap.addMarker(markerOptionFactory.build(this, markPoint));
        PolygonData activePolygon = getActivePolygonData();
        setUpdateIconToLastMarker(passiveMarkerIcon);
        boolean success = activePolygon.addMarker(marker);
        if (!success) {
            onMarkHoleOutBound(markPoint, activePolygon.getPolygon().getPoints());
            marker.remove();
        }
        PolygonDrawUtils.draw(googleMap, activePolygon, polygonOptionFactory.build(this));
    }

    protected void onMarkHoleOutBound(LatLng target, List<LatLng> points) {
        Toast.makeText(getActivity(), "Out of polygon boundary ", Toast.LENGTH_SHORT).show();
    }

    public boolean undo() {
        controller.undo();
        PolygonData polygonData = getActivePolygonData();
        if (polygonData.isEmpty()) {
            return false;
        }
        boolean removed = polygonData.removeLastMarker();
        setUpdateIconToLastMarker(activeMarkerIcon);

        if (removed && polygonData.isEmpty() && multiPolygon.size() > 1) {
            multiPolygon.pop();
        }

        PolygonDrawUtils.draw(googleMap, polygonData, polygonOptionFactory.build(this));
        return true;
    }

    public List<Polygon> getPolygons() {
        return null;
    }

    public PolygonData.State getDrawingState() {
        return getActivePolygonData().getCurrentState();
    }

}
