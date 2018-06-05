/*
 * Copyright (c) 2017 NECTEC
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

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;
import java.util.Stack;

import th.or.nectec.marlo.exception.HoleInvalidException;
import th.or.nectec.marlo.model.MarloCoord;
import th.or.nectec.marlo.model.Polygon;
import th.or.nectec.marlo.option.DefaultPolygonMarkerOptionFactory;
import th.or.nectec.marlo.option.DefaultPolygonOptionFactory;
import th.or.nectec.marlo.option.MarkerOptionFactory;
import th.or.nectec.marlo.option.PolygonOptionFactory;

public class PolygonMarloFragment extends MarloFragment {

    private MarkerOptionFactory passiveMarkOptFactory;
    private PolygonOptionFactory polyOptFactory;
    private PolygonController controller = new PolygonController();
    private int padding = 100;
    private Polygon tempRestoreData;
    private List<Polygon> tmpRestoreDataList;
    private boolean shouldAnimateToRestorePolygon;

    public PolygonMarloFragment() {
        super();
        markOptFactory = new DefaultPolygonMarkerOptionFactory();
        polyOptFactory = new DefaultPolygonOptionFactory();
    }

    public void setPolygonOptionFactory(PolygonOptionFactory polygonOptionFactory) {
        this.polyOptFactory = polygonOptionFactory;
    }

    @Override
    public void setMarkerOptionFactory(MarkerOptionFactory markerOptionFactory) {
        super.setMarkerOptionFactory(markerOptionFactory);
    }

    public void setPassiveMakerOptionFactory(MarkerOptionFactory passiveMarkerOptFactory) {
        this.passiveMarkOptFactory = passiveMarkerOptFactory;
    }

    protected PolygonController getController() {
        return controller;
    }

    boolean isUseDefaultTools = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isUseDefaultTools) {
            ViewUtils.addPolygonToolsMenu(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (shouldAnimateToRestorePolygon) {
            animateToPolygons();
            shouldAnimateToRestorePolygon = false;
        }
    }

    public void animateToPolygons() {
        List<Polygon> polygons = controller.getPolygons();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        boolean added = false;
        for (Polygon focusPolygon : polygons) {
            for (MarloCoord coordinate : focusPolygon.getBoundary()) {
                builder.include(coordinate.toLatLng());
                added = true;
            }
        }
        if (added)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), padding));
    }

    public void useDefaultToolsMenu() {
        isUseDefaultTools = true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.marlo_hole) {
            try {
                controller.newHole();
            } catch (IllegalStateException expected) {
                onNotReadyToNewHole();
            }
        } else if (view.getId() == R.id.marlo_boundary) {
            try {
                controller.startNewPolygon();
            } catch (IllegalStateException expected) {
                onNotReadyToNewPolygon();
            }
        } else if (view.getId() == R.id.marlo_undo) {
            undo();
        } else {
            super.onClick(view);
        }
    }

    protected void onNotReadyToNewHole() {
        //For subclass to implement
    }

    protected void onNotReadyToNewPolygon() {
        //For subclass to implement
    }

    @Override
    public boolean undo() {
        boolean undo = controller.undo();
        if (undo) {
            onPolygonChanged(controller.getPolygons(),
                    controller.getFocusPolygon().getLastCoordinate());
        }
        return undo;
    }

    protected void onPolygonChanged(List<Polygon> polygons, MarloCoord focusCoordinate) {
        //For subclass to implement
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        super.onMapReady(googleMap);
        controller.setPresenter(new GoogleMapPresenter(googleMap));
        googleMap.setOnMarkerDragListener(new OnPolygonMarkerDragListener(googleMap));

        onMarloMapReady();

        if (tempRestoreData != null) {
            setRestoreData(tempRestoreData);
        }
        if (tmpRestoreDataList != null) {
            setRestoreData(tmpRestoreDataList);
        }
    }

    protected void onMarloMapReady() {
        //For subclass to implement
    }

    protected void onPreviewPolygonUpdated(Polygon previewPolygon) {
        //For subclass to implement
    }

    public void setRestoreData(Polygon restoreData) {
        if (googleMap != null) {
            controller.restore(restoreData);
            shouldAnimateToRestorePolygon = true;
            tempRestoreData = null;
            onPolygonChanged(controller.getPolygons(), controller.getFocusPolygon().getLastCoordinate());
            return;
        }
        tempRestoreData = restoreData;
    }

    public void setRestoreData(List<Polygon> restoreData) {
        if (googleMap != null) {
            controller.restore(restoreData);
            shouldAnimateToRestorePolygon = true;
            tmpRestoreDataList = null;
            onPolygonChanged(controller.getPolygons(), controller.getFocusPolygon().getLastCoordinate());
            return;
        }
        tmpRestoreDataList = restoreData;
    }

    protected void onMarkInvalidHole(List<Polygon> polygons, LatLng markPoint) {
        //For subclass to implement
    }

    @Override
    public void showToolsMenu() {
        viewFinder.setVisibility(View.VISIBLE);
        myLocation.setVisibility(View.VISIBLE);
        ViewUtils.setPolygonToolsMenuVisibility(this, View.VISIBLE);
    }

    @Override
    public void hideToolsMenu() {
        viewFinder.setVisibility(View.GONE);
        myLocation.setVisibility(View.GONE);
        ViewUtils.setPolygonToolsMenuVisibility(this, View.GONE);
    }

    @Override
    public void mark(LatLng markPoint) {
        try {
            controller.mark(new MarloCoord(markPoint));
            if (!mute) SoundUtility.play(getContext(), R.raw.thumpsoundeffect);
            onPolygonChanged(controller.getPolygons(), controller.getFocusPolygon().getLastCoordinate());
        } catch (HoleInvalidException expected) {
            onMarkInvalidHole(controller.getPolygons(), markPoint);
        }
    }

    public List<Polygon> getPolygons() {
        return controller.getPolygons();
    }

    private class GoogleMapPresenter implements PolygonController.Presenter {

        private final Stack<PolygonData> multiPolygon = new Stack<>();
        private final GoogleMap googleMap;

        GoogleMapPresenter(GoogleMap googleMap) {
            this.googleMap = googleMap;
            multiPolygon.push(new PolygonData());
        }

        @Override
        public void markHole(MarloCoord coordinate) {
            updateIconToLastMarker(passiveMarkOptFactory);

            LatLng markPoint = coordinate.toLatLng();
            Marker marker =
                    googleMap.addMarker(markOptFactory.build(PolygonMarloFragment.this, markPoint));
            marker.setTag(coordinate);
            PolygonData activePolygon = getActivePolygonData();
            activePolygon.addMarker(marker);
            PolygonDrawUtils.draw(googleMap, activePolygon,
                    polyOptFactory.build(PolygonMarloFragment.this));
        }

        private void updateIconToLastMarker(MarkerOptionFactory optionFactory) {
            MarkerOptions option = optionFactory.build(PolygonMarloFragment.this, new LatLng(1, 1));
            Marker lastMarker = getActivePolygonData().getLastMarker();
            if (lastMarker != null) {
                updateByOption(lastMarker, option);
            } else if (multiPolygon.size() > 1) {
                PolygonData topEmptyPolygon = multiPolygon.pop();
                updateByOption(multiPolygon.peek().getLastMarker(), option);
                multiPolygon.push(topEmptyPolygon);
            }
        }

        private PolygonData getActivePolygonData() {
            return multiPolygon.peek();
        }

        private void updateByOption(Marker marker, MarkerOptions options) {
            marker.setAlpha(options.getAlpha());
            marker.setAnchor(options.getAnchorU(), options.getAnchorV());
            marker.setIcon(options.getIcon());
            marker.setFlat(options.isFlat());
        }

        @Override
        public void markBoundary(MarloCoord coordinate) {
            updateIconToLastMarker(passiveMarkOptFactory);

            LatLng markPoint = coordinate.toLatLng();
            Marker marker = googleMap.addMarker(markOptFactory.build(PolygonMarloFragment.this, markPoint));
            marker.setTag(coordinate);
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
            updateIconToLastMarker(markOptFactory);

            if (removed && polygonData.isEmpty() && multiPolygon.size() > 1) {
                multiPolygon.pop();
            }

            PolygonDrawUtils.draw(googleMap, polygonData,
                    polyOptFactory.build(PolygonMarloFragment.this));
        }

        @Override
        public void clear() {
            for (PolygonData data : multiPolygon) {
                if (data.isEmpty())
                    continue;

                data.getDrawPolygon().remove();
                removeMarker(data.getBoundary());
                for (List<Marker> hole : data.getHoles()) {
                    removeMarker(hole);
                }
            }
            multiPolygon.clear();
            multiPolygon.push(new PolygonData());
        }

        private void removeMarker(Iterable<Marker> markers) {
            for (Marker marker : markers) {
                marker.remove();
            }
        }
    }

    private class OnPolygonMarkerDragListener implements GoogleMap.OnMarkerDragListener {

        private final GoogleMap googleMap;
        MarloCoord oldCoord;
        MarloCoord previewFocusCoord;
        Polygon previewPolygon;
        com.google.android.gms.maps.model.Polygon previewGooglePolygon;

        OnPolygonMarkerDragListener(GoogleMap googleMap) {
            this.googleMap = googleMap;
        }

        @Override
        public void onMarkerDragStart(Marker marker) {
            controller.backup();
            oldCoord = (MarloCoord) marker.getTag();

            previewFocusCoord = new MarloCoord(oldCoord);
            previewPolygon = new Polygon(controller.findPolygonByCoordinate(oldCoord));
            removeEmptyHole(previewPolygon);
            previewGooglePolygon = null;
        }

        private void removeEmptyHole(Polygon polygon) {
            List<Polygon> holes = polygon.getAllHoles();
            for (Polygon hole : holes) {
                if (hole.isEmpty()) polygon.removeHole(hole);
            }
        }

        @Override
        public void onMarkerDrag(Marker marker) {
            if (previewPolygon == null)
                return; //may occur when onMarkerDrag was called before onMarkerDragStart() finished

            if (previewGooglePolygon != null)
                previewGooglePolygon.remove();
            MarloCoord changedCoord = MarloCoord.fromMarker(marker);
            previewPolygon.replace(previewFocusCoord, changedCoord);
            previewFocusCoord = changedCoord;

            previewGooglePolygon = googleMap.addPolygon(previewPolygon.toPolygonOptions(previewTemplate()));
            onPreviewPolygonUpdated(previewPolygon);
        }

        private PolygonOptions previewTemplate() {
            PolygonOptions template = polyOptFactory.build(PolygonMarloFragment.this);
            template.strokeJointType(JointType.ROUND);

            template.fillColor(blend(35, template.getFillColor()));
            template.strokeColor(blend(80, template.getStrokeColor()));
            return template;
        }

        private int blend(int alpha, int color) {
            return Color.argb(alpha, Color.red(color), Color.blue(color), Color.red(color));
        }

        @Override
        public void onMarkerDragEnd(Marker marker) {
            if (previewPolygon != null && previewGooglePolygon != null) //sometime null because of slow ui process
                previewGooglePolygon.remove();

            MarloCoord newCoord = MarloCoord.fromMarker(marker);
            try {
                controller.replaceWith(oldCoord, newCoord);
                onPolygonChanged(controller.getPolygons(), newCoord);
            } catch (HoleInvalidException holeOutOfBound) {
                controller.rollback();
                onMarkInvalidHole(controller.getPolygons(), marker.getPosition());
            }
        }
    }
}
