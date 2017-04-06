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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;
import java.util.Stack;

import th.or.nectec.marlo.model.Coordinate;
import th.or.nectec.marlo.model.Polygon;
import th.or.nectec.marlo.model.PolygonData;
import th.or.nectec.marlo.option.DefaultPolygonMarkerOptionFactory;
import th.or.nectec.marlo.option.DefaultPolygonOptionFactory;
import th.or.nectec.marlo.option.PolygonOptionFactory;

public class PolygonMarloFragment extends MarloFragment {

    private BitmapDescriptor activeMarkerIcon;
    private BitmapDescriptor passiveMarkerIcon;
    private PolygonOptionFactory polyOptFactory;

    private PolygonController controller = new PolygonController();

    public PolygonMarloFragment() {
        super();
        markOptFactory = new DefaultPolygonMarkerOptionFactory();
        polyOptFactory = new DefaultPolygonOptionFactory();
    }

    public static PolygonMarloFragment newInstance() {
        PolygonMarloFragment fragment = new PolygonMarloFragment();
        return fragment;
    }

    public void setPolygonOptionFactory(PolygonOptionFactory polygonOptionFactory) {
        this.polyOptFactory = polygonOptionFactory;
    }

    protected PolygonController getController() {
        return controller;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activeMarkerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        passiveMarkerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);

        ViewUtils.addPolygonToolsMenu(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.marlo_hole) {
            controller.newHole();
        } else if (view.getId() == R.id.marlo_boundary) {
            controller.startNewPolygon();
        } else if (view.getId() == R.id.marlo_undo) {
            controller.undo();
        } else {
            super.onClick(view);
        }
    }

    @Override
    public void mark(LatLng markPoint) {
        try {
            controller.mark(new Coordinate(markPoint));
            SoundUtility.play(getContext(), R.raw.thumpsoundeffect);
            onPolygonChange(controller.getPolygons());
        } catch (HoleInvalidException expected){
            onMarkInvalidHole(controller.getPolygons(), markPoint);
        }
    }

    protected void onPolygonChange(List<Polygon> polygons) {

    }

    protected void onMarkInvalidHole(List<Polygon> polygons, LatLng markPoint) {

    }

    @Override
    public boolean undo() {
        boolean willRemove = !controller.getFocusPolygon().isEmpty();
        controller.undo();

        return willRemove;
    }

    public List<Polygon> getPolygons() {
        return controller.getPolygons();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        controller.setPresenter(new GoogleMapPresenter(googleMap));
    }

    private class GoogleMapPresenter implements PolygonController.Presenter {

        private final Stack<PolygonData> multiPolygon = new Stack<>();
        private final GoogleMap googleMap;

        public GoogleMapPresenter(GoogleMap googleMap) {
            this.googleMap = googleMap;
            multiPolygon.push(new PolygonData());
        }

        @Override
        public void markHole(Coordinate coordinate) {
            updateIconToLastMarker(passiveMarkerIcon);

            LatLng markPoint = coordinate.toLatLng();
            Marker marker = googleMap.addMarker(markOptFactory.build(PolygonMarloFragment.this, markPoint));
            PolygonData activePolygon = getActivePolygonData();
            activePolygon.addMarker(marker);
            PolygonDrawUtils.draw(googleMap, activePolygon, polyOptFactory.build(PolygonMarloFragment.this));
        }

        @Override
        public void markBoundary(Coordinate coordinate) {
            updateIconToLastMarker(passiveMarkerIcon);

            LatLng markPoint = coordinate.toLatLng();
            Marker marker = googleMap.addMarker(markOptFactory.build(PolygonMarloFragment.this, markPoint));
            PolygonData activePolygon = getActivePolygonData();
            activePolygon.addMarker(marker);
            PolygonDrawUtils.draw(googleMap, activePolygon, polyOptFactory.build(PolygonMarloFragment.this));
        }

        @Override
        public void prepareForNewPolygon() {
            multiPolygon.push(new PolygonData());
            getActivePolygonData().setCurrentState(PolygonData.State.BOUNDARY);
        }

        @Override
        public void prepareForNewHole() {
            getActivePolygonData().newHole();
            getActivePolygonData().setCurrentState(PolygonData.State.HOLE);
        }

        @Override
        public void removeLastMarker() {
            PolygonData polygonData = getActivePolygonData();
            if (polygonData.isEmpty()) {
                return;
            }
            boolean removed = polygonData.removeLastMarker();
            updateIconToLastMarker(activeMarkerIcon);

            if (removed && polygonData.isEmpty() && multiPolygon.size() > 1) {
                multiPolygon.pop();
            }

            PolygonDrawUtils.draw(googleMap, polygonData,
                    polyOptFactory.build(PolygonMarloFragment.this));
        }

        private void updateIconToLastMarker(BitmapDescriptor icon) {
            Marker lastMarker = getActivePolygonData().getLastMarker();
            if (lastMarker != null) {
                lastMarker.setIcon(icon);
            } else if (multiPolygon.size() > 1) {
                PolygonData topPolygon = multiPolygon.pop();
                multiPolygon.peek().getLastMarker().setIcon(icon);
                multiPolygon.push(topPolygon);
            }
        }

        private PolygonData getActivePolygonData() {
            return multiPolygon.peek();
        }
    }
}
