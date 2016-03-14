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

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MarloFragment extends SupportMapFragment implements OnClickListener, OnMapReadyCallback {

    GoogleMap googleMap;

    Stack<PolygonData> polygonDataStack = new Stack<>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMapAsync(this);

        PolygonData polygonData = new PolygonData();
        polygonDataStack.push(polygonData);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.view_finder) {
            PolygonData polygonData = polygonDataStack.peek();
            addMarker(polygonData.boundary);
            createPolygon(polygonData);
        }else if (view.getId() == R.id.undo) {
            undo();
        }
    }

    private void addMarker(Stack<Marker> markers) {
        Marker marker = googleMap.addMarker(markerOnCenterOfScreen());
        markers.push(marker);
        SoundUtils.play(getContext(), R.raw.thumpsoundeffect);
    }



    public void createPolygon(PolygonData polygonData) {
        PolygonOptions polygon = new PolygonOptions();
        polygon.strokeColor(Color.RED);
        polygon.fillColor(Color.YELLOW);
        polygon.strokeWidth(3);

        addBoundary(polygon, polygonData);
        addHole(polygon, polygonData);

        if (polygonData.polygon != null)
            polygonData.polygon.remove();
        if (!polygonData.boundary.isEmpty())
            polygonData.polygon = googleMap.addPolygon(polygon);

    }

    MarkerOptions markerOnCenterOfScreen() {
        return new MarkerOptions()
                .position(googleMap.getCameraPosition().target)
                .draggable(true);
    }

    private void addBoundary(PolygonOptions polygon, PolygonData polygonData) {
        Stack<Marker> markers = polygonData.boundary;
        for (Marker eachMarker : markers) {
            polygon.add(eachMarker.getPosition());
        }
    }

    private void addHole(PolygonOptions polygon, PolygonData polygonData) {
        Stack<Marker> holeMarker = polygonData.hole;
        if (!holeMarker.isEmpty()) {
            List<LatLng> holes = new ArrayList<>();
            for (Marker eachMarker : holeMarker) {
                holes.add(eachMarker.getPosition());
            }
            if (holes.size() >= 3)
                polygon.addHole(holes);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().isMyLocationButtonEnabled();

        ViewUtils.addViewFinder(this);
        ViewUtils.addUndoButton(this);
    }

    protected GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void undo() {
        Stack<Marker> holeMarker = polygonDataStack.peek().hole;
        if (!holeMarker.isEmpty()) {
            holeMarker.peek().remove();
            holeMarker.pop();
            createPolygon(polygonDataStack.peek());
        }

        Stack<Marker> markers = polygonDataStack.peek().boundary;
        if (holeMarker.isEmpty() && !markers.isEmpty()) {
            markers.peek().remove();
            markers.pop();
            createPolygon(polygonDataStack.peek());
        }
        if (polygonDataStack.size() > 1 && markers.isEmpty()) {
            polygonDataStack.pop();
        }
    }

}
