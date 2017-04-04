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

    private final Polygon polygon = new Polygon();
    private PointInHoleValidator pointInBoundaryValidator = new PolygonUtils();

    public void mark(Coordinate coordinate) {
        if (polygon.getHolesCount() > 0) {
            validateHolePoint(coordinate);
            polygon.getLastHole().getBoundary().add(coordinate);
        } else {
            polygon.getBoundary().add(coordinate);
        }
    }

    private void validateHolePoint(Coordinate coordinate) {
        if (!pointInBoundaryValidator.inBoundary(polygon, coordinate))
            throw new HoleInvalidException();
    }

    public Polygon getPolygon() {
        validatePolygon();
        return polygon;
    }

    private void validatePolygon() {
        if (polygon.getBoundary().size() < 3) {
            throw new PolygonInvalidException();
        }
    }

    public void newHole() {
        try {
            validatePolygon();
            if (polygon.getHolesCount() > 0) {
                validateLastHole();
            }
        } catch (PolygonInvalidException invalid) {
            throw new IllegalStateException("Polygon must valid before markHole");
        }
        polygon.addHoles(new Polygon());
    }

    private void validateLastHole() {
        if (polygon.getLastHole().getBoundary().size() < 3) {
            throw new HoleInvalidException();
        }
    }

    public void setPointInHoleValidator(PointInHoleValidator validator) {
        pointInBoundaryValidator = validator;
    }

    public void undo() {
        if (polygon.getHolesCount() > 0) {
            List<Coordinate> hole = polygon.getLastHole().getBoundary();
            removeLastCoordinate(hole);
            removeHoleWhenEmpty(hole);
        } else {
            List<Coordinate> boundary = polygon.getBoundary();
            removeLastCoordinate(boundary);
        }
    }

    private void removeLastCoordinate(List<Coordinate> coordinates) {
        if (coordinates.size() != 0) {
            coordinates.remove(coordinates.size() - 1);
        }
    }

    private void removeHoleWhenEmpty(List<Coordinate> hole) {
        if (hole.isEmpty()) polygon.getAllHoles().remove(polygon.getHolesCount() - 1);
    }
}
