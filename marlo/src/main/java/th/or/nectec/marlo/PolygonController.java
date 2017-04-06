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

import java.util.ArrayList;
import java.util.List;

import th.or.nectec.marlo.model.Coordinate;
import th.or.nectec.marlo.model.Polygon;

public class PolygonController {

    private final List<Polygon> polygons;
    private Polygon focusPolygon;
    private PointInHoleValidator pointInBoundaryValidator = new PolygonUtils();

    private Presenter presenter;

    public PolygonController() {
        this(new ArrayList<Polygon>());
    }

    public PolygonController(List<Polygon> polygons) {
        this.polygons = polygons;
        if (polygons.size() > 0) focusPolygon = polygons.get(polygons.size() - 1);
        else createPolygonObject();
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void mark(Coordinate coordinate) {
        if (coordinate.equals(focusPolygon.getLastCoordinate()))
            return;

        if (focusPolygon.haveHole()) {
            validateHolePoint(coordinate);
            focusPolygon.getLastHole().add(coordinate);
            presenter.markHole(coordinate);
        } else {
            focusPolygon.add(coordinate);
            presenter.markBoundary(coordinate);
        }
    }

    public void startNewPolygon() {
        if (!focusPolygon.isValid())
            throw new IllegalStateException("Should finish last Polygon before create new one");
        if (focusPolygon.haveHole()) {
            if (!focusPolygon.getLastHole().isValid()) {
                throw new IllegalStateException("Last hole should finish before add new polygon");
            }
        }
        createPolygonObject();
        presenter.prepareForNewPolygon();
    }

    private void createPolygonObject() {
        Polygon newPolygon = new Polygon();
        polygons.add(newPolygon);
        focusPolygon = newPolygon;
    }

    private void validateHolePoint(Coordinate coordinate) {
        if (!pointInBoundaryValidator.inBoundary(focusPolygon, coordinate))
            throw new HoleInvalidException();
    }

    public Polygon getFocusPolygon() {
        return focusPolygon;
    }

    public void newHole() {
        if (!focusPolygon.isValid())
            throw new IllegalStateException("Must be valid polygon before add hole");
        if (focusPolygon.haveHole()) {
            if (!focusPolygon.getLastHole().isValid()) {
                throw new IllegalStateException("Last hole should finish before add new hole");
            }
        }

        focusPolygon.addHoles(new Polygon());
        presenter.prepareForNewHole();
    }

    public List<Polygon> getPolygons() {
        return polygons;
    }

    public boolean undo() {
        if (focusPolygon.haveHole()) {
            Polygon lastHole = focusPolygon.getLastHole();
            lastHole.pop();
            if (lastHole.isEmpty())
                focusPolygon.removeHole(lastHole);
        } else if (!focusPolygon.isEmpty()){
            focusPolygon.pop();
            if (focusPolygon.isEmpty() && polygons.size() > 1){
                polygons.remove(polygons.size() - 1);
                focusPolygon = polygons.get(polygons.size() - 1);
            }
        }
        presenter.removeLastMarker();
        return false;
    }

    public void retore(Polygon polygon) {
        //focusPolygon = new Polygon(polygon);
        for (Coordinate point : polygon.getBoundary()){
            mark(new Coordinate(point));
        }
        for (Polygon hole : polygon.getAllHoles()){
            newHole();
            retore(hole);
        }
    }

    interface Presenter {

        void markHole(Coordinate coordinate);

        void markBoundary(Coordinate coordinate);

        void prepareForNewPolygon();

        void prepareForNewHole();

        void removeLastMarker();
    }

}
