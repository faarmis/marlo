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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import th.or.nectec.marlo.model.PolygonData;

final class PolygonDrawUtils {

    private PolygonDrawUtils() {}

    public static void draw(GoogleMap map, PolygonData data, PolygonOptions polygon) {
        drawBoundary(polygon, data);
        drawHoles(polygon, data);

        if (data.getDrawPolygon() != null) {
            data.getDrawPolygon().remove();
        }
        if (!data.isEmpty()) {
            data.setDrawPolygon(map.addPolygon(polygon));
        }
    }

    private static void drawBoundary(PolygonOptions polygon, PolygonData polygonData) {
        Stack<Marker> markers = polygonData.getBoundary();
        for (Marker eachMarker : markers) {
            polygon.add(eachMarker.getPosition());
        }
    }

    private static void drawHoles(PolygonOptions polygon, PolygonData polygonData) {
        Stack<Stack<Marker>> holeMarker = polygonData.getHoles();
        if (!holeMarker.isEmpty()) {
            for (Stack<Marker> hole : holeMarker) {
                List<LatLng> holes = new ArrayList<>();
                for (Marker eachMarker : hole) {
                    holes.add(eachMarker.getPosition());
                }
                if (holes.size() >= 3) {
                    polygon.addHole(holes);
                }
            }
        }
    }
}
