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

import th.or.nectec.marlo.model.Coordinate;
import th.or.nectec.marlo.model.Polygon;

import java.util.ArrayList;
import java.util.List;

public class PolygonController {

    private final List<Polygon> polygons;
    private Polygon focusPolygon;
    private PointInHoleValidator pointInBoundaryValidator = new PolygonUtils();

    public PolygonController() {
        this(new ArrayList<Polygon>());
    }

    public PolygonController(List<Polygon> polygons) {
        this.polygons = polygons;
        if (polygons.size() > 0) focusPolygon = polygons.get(polygons.size() - 1);
        else newPolygon();
    }

    public void mark(Coordinate coordinate) {
        if (focusPolygon.haveHole()) {
            validateHolePoint(coordinate);
            focusPolygon.getLastHole().add(coordinate);
        } else {
            focusPolygon.add(coordinate);
        }
    }

    public void newPolygon(){
        if (focusPolygon != null)
            validatePolygon();

        Polygon newPolygon = new Polygon();
        polygons.add(newPolygon);
        focusPolygon = newPolygon;
    }

    private void validateHolePoint(Coordinate coordinate) {
        if (!pointInBoundaryValidator.inBoundary(focusPolygon, coordinate))
            throw new HoleInvalidException();
    }

    public Polygon getFocusPolygon() {
        validatePolygon();
        return focusPolygon;
    }

    public List<Polygon> getPolygons(){
        for (Polygon poly : polygons){
            if(!poly.isValid()) throw new PolygonInvalidException();
        }
        return polygons;
    }

    private void validatePolygon() {
        if (!focusPolygon.isValid()) {
            throw new PolygonInvalidException();
        }
    }

    public void newHole() {
        try {
            validatePolygon();
            if (focusPolygon.haveHole()) {
                validateLastHole();
            }
        } catch (PolygonInvalidException invalid) {
            throw new IllegalStateException("Polygon must valid before markHole");
        }
        focusPolygon.addHoles(new Polygon());
    }

    private void validateLastHole() {
        if (!focusPolygon.getLastHole().isValid()) {
            throw new HoleInvalidException();
        }
    }

    public void undo() {
        if (focusPolygon.haveHole()) {
            Polygon lastHole = focusPolygon.getLastHole();
            lastHole.pop();
            if (lastHole.isEmpty())
                focusPolygon.removeHole(lastHole);
        } else {
            focusPolygon.pop();
        }
    }

}
