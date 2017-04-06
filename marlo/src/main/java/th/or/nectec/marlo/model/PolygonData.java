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

package th.or.nectec.marlo.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PolygonData {

    private final Stack<Marker> boundary = new Stack<>();
    private final Stack<Stack<Marker>> holes = new Stack<>();
    private State currentState = State.BOUNDARY;
    private Polygon polygon;

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public Marker getLastMarker() {
        if (!holes.isEmpty()) {
            Stack<Marker> lastHole = holes.peek();
            if (!lastHole.isEmpty()) {
                return lastHole.peek();
            }
        }
        if (!boundary.isEmpty())
            return boundary.peek();

        return null;
    }

    /**
     * remove last marker in object. start from last marker of last hole to boundary's marker
     *
     * @return true if some marker was removed, false otherwise
     */
    public boolean removeLastMarker() {
        if (!holes.isEmpty()) {
            Stack<Marker> lastHoles = holes.peek();
            if (!lastHoles.isEmpty()) {
                lastHoles.pop().remove();
            }
            if (lastHoles.isEmpty()) {
                holes.pop();
            }
            return true;
        } else if (!boundary.isEmpty()) {
            currentState = State.BOUNDARY;
            boundary.pop().remove();
            return true;
        } else {
            return false;
        }
    }


    /**
     * add marker to object. this method work by checking object state BOUNDARY or HOLE
     *
     * @param marker to add
     * @return true if marker successful add to object,
     * false when marker at invalid position such as hole position outside boundary
     */
    public boolean addMarker(Marker marker) {
        switch (currentState) {
            case BOUNDARY:
                boundary.add(marker);
                return true;
            case HOLE:
                if (holes.isEmpty()) {
                    newHole();
                }
                boolean inBoundary = PolyUtil.containsLocation(marker.getPosition(), getBoundaryLatLng(), false);
                if (inBoundary) {
                    holes.peek().push(marker);
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    private List<LatLng> getBoundaryLatLng() {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        for (Marker marker : boundary) {
            latLngs.add(marker.getPosition());
        }
        return latLngs;
    }

    /**
     * tell object that next addMarker() call should add to new Holes of polygon.
     * If last hole is empty the command will be ignored.
     */
    public void newHole() {
        if (holes.isEmpty() || !holes.peek().isEmpty()) {
            holes.push(new Stack<Marker>());
        }
    }

    public boolean isEmpty() {
        return boundary.isEmpty();
    }

    public Stack<Marker> getBoundary() {
        return boundary;
    }

    public Stack<Stack<Marker>> getHoles() {
        return holes;
    }

    public Polygon getDrawPolygon() {
        return polygon;
    }

    public void setDrawPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public enum State {
        BOUNDARY,
        HOLE,
    }
}
