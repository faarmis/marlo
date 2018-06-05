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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import th.or.nectec.marlo.exception.HoleInvalidException;
import th.or.nectec.marlo.model.MarloCoord;
import th.or.nectec.marlo.model.MarloPolygon;

public class PolygonController {

    private Deque<List<MarloPolygon>> backupStack = new ArrayDeque<>();
    private List<MarloPolygon> polygons = new ArrayList<>();
    private PointInHoleValidator pointInBoundaryValidator = new PolygonUtils();
    private Presenter presenter;

    public PolygonController() {
        createPolygonObject();
    }

    private void createPolygonObject() {
        MarloPolygon newPolygon = new MarloPolygon();
        polygons.add(newPolygon);
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void mark(MarloCoord coordinate) {
        mark(coordinate, true);
    }

    private void mark(MarloCoord coordinate, boolean external) {
        MarloPolygon focusPolygon = getFocusPolygon();
        if (coordinate.equals(focusPolygon.getLastCoordinate()))
            return;

        if (external)
            backup();

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
        MarloPolygon focusPolygon = getFocusPolygon();
        if (!focusPolygon.isValid())
            throw new IllegalStateException("Should finish last MarloPolygon before create new one");
        if (focusPolygon.haveHole()) {
            if (!focusPolygon.getLastHole().isValid()) {
                throw new IllegalStateException("Last hole should finish before add new polygon");
            }
        }
        createPolygonObject();
        presenter.prepareForNewPolygon();
    }

    private void validateHolePoint(MarloCoord coordinate) {
        if (!pointInBoundaryValidator.inBoundary(getFocusPolygon(), coordinate))
            throw new HoleInvalidException();
    }

    public MarloPolygon getFocusPolygon() {
        return polygons.get(polygons.size() - 1);
    }

    public void newHole() {
        MarloPolygon focusPolygon = getFocusPolygon();
        if (!focusPolygon.isValid())
            throw new IllegalStateException("Must be valid polygon before add hole");
        if (focusPolygon.haveHole()) {
            if (!focusPolygon.getLastHole().isValid()) {
                throw new IllegalStateException("Last hole should finish before add new hole");
            }
        }
        focusPolygon.addHoles(new MarloPolygon());
        presenter.prepareForNewHole();
    }

    public List<MarloPolygon> getPolygons() {
        return polygons;
    }

    public boolean undo() {
        MarloPolygon focusPolygon = getFocusPolygon();
        if (polygons.size() == 1 && focusPolygon.isEmpty())
            return false;

        if (!backupStack.isEmpty()) {
            rollback();
            return true;
        }

        return secondaryUndo(focusPolygon);
    }

    private boolean secondaryUndo(MarloPolygon focusPolygon) {
        if (focusPolygon.haveHole()) {
            MarloPolygon lastHole = focusPolygon.getLastHole();
            lastHole.pop();
            if (lastHole.isEmpty()) {
                focusPolygon.removeHole(lastHole);
            }
        } else if (!focusPolygon.isEmpty()) {
            focusPolygon.pop();
            if (focusPolygon.isEmpty() && polygons.size() > 1) {
                polygons.remove(polygons.size() - 1);
            }
        }
        presenter.removeLastMarker();
        return true;
    }

    public void restore(List<MarloPolygon> polygons) {
        for (int i = 0; i < polygons.size(); i++) {
            restore(polygons.get(i));
            if (i != polygons.size() - 1 && !polygons.get(i + 1).isEmpty())
                startNewPolygon();
        }
    }

    public void restore(@NonNull MarloPolygon polygon) {
        removeLastPointIfSameAsStart(polygon);
        for (MarloCoord point : polygon.getBoundary()) {
            mark(new MarloCoord(point), false);
        }
        for (MarloPolygon hole : polygon.getAllHoles()) {
            removeLastPointIfSameAsStart(hole);
            newHole();
            restore(hole); //Recursive
        }
    }

    private void removeLastPointIfSameAsStart(MarloPolygon polygon) {
        if (polygon.isEmpty())
            return;
        List<MarloCoord> boundary = polygon.getBoundary();
        MarloCoord startPoint = boundary.get(0);
        MarloCoord lastPoint = boundary.get(boundary.size() - 1);
        if (boundary.size() > 2 && startPoint.equals(lastPoint)) {
            boundary.remove(boundary.size() - 1);
        }
    }

    public void backup() {
        if (polygons.isEmpty() || polygons.get(0).isEmpty())
            return;

        backupStack.push(clone(polygons));
    }

    private List<MarloPolygon> clone(List<MarloPolygon> polygons) {
        List<MarloPolygon> clone = new ArrayList<>();
        for (MarloPolygon poly : polygons) {
            clone.add(new MarloPolygon(poly));
        }
        return clone;
    }

    public void replaceWith(MarloCoord oldCoord, MarloCoord newCoord) {
        boolean replaced = false;
        List<MarloPolygon> newPoly = clone(polygons);
        for (MarloPolygon poly : newPoly) {
            if (poly.replace(oldCoord, newCoord)) {
                replaced = true;
                break;
            }
        }
        if (replaced) {
            redrawPolygon(newPoly);
        }
    }

    private void redrawPolygon(List<MarloPolygon> polygonsToDraw) {
        clear();
        restore(polygonsToDraw);
    }

    public void clear() {
        presenter.clear();

        polygons = new ArrayList<>();
        createPolygonObject();
    }

    void rollback() {
        if (backupStack.isEmpty())
            throw new IllegalStateException("must call backup before.");
        redrawPolygon(backupStack.poll());
    }

    @Nullable
    MarloPolygon findPolygonByCoordinate(MarloCoord coord) {
        for (MarloPolygon poly : polygons) {
            if (poly.isCoordinateExist(coord))
                return poly;
        }
        return null;
    }

    interface Presenter {

        void markHole(MarloCoord coordinate);

        void markBoundary(MarloCoord coordinate);

        void prepareForNewPolygon();

        void prepareForNewHole();

        void removeLastMarker();

        void clear();
    }

}
