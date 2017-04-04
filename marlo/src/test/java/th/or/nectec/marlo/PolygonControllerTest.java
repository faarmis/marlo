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
import org.junit.Test;
import th.or.nectec.marlo.model.Coordinate;
import th.or.nectec.marlo.model.Polygon;

import static org.junit.Assert.assertEquals;

public class PolygonControllerTest {

    @Test
    public void testMarkValid() throws Exception {
        List<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(0f, 0f));
        coordinates.add(new Coordinate(1f, 1f));
        coordinates.add(new Coordinate(0f, 1f));

        PolygonController controller = new PolygonController();
        controller.mark(new Coordinate(0f, 0f));
        controller.mark(new Coordinate(1f, 1f));
        controller.mark(new Coordinate(0f, 1f));

        assertEquals(coordinates, controller.getFocusPolygon().getBoundary());

    }

    @Test(expected = PolygonInvalidException.class)
    public void testMarkIncompletePolygon() throws Exception {
        PolygonController controller = new PolygonController();
        controller.mark(new Coordinate(0f, 0f));
        controller.mark(new Coordinate(0f, 1f));

        controller.getFocusPolygon();
    }

    @Test(expected = IllegalStateException.class)
    public void testChangeToHoleBeforeBoundaryCompleteShouldThrowException() {
        PolygonController controller = new PolygonController();
        controller.mark(new Coordinate(0f, 0f));
        controller.mark(new Coordinate(0f, 1f));

        controller.newHole();
    }

    @Test
    public void testMark1Hole() throws Exception {
        List<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(1f, 1f));
        coordinates.add(new Coordinate(1f, 2f));
        coordinates.add(new Coordinate(2f, 2f));
        Polygon hole = new Polygon(coordinates);

        PolygonController controller = new PolygonController();
        controller.mark(new Coordinate(0f, 0f));
        controller.mark(new Coordinate(3f, 0f));
        controller.mark(new Coordinate(3f, 3f));
        controller.mark(new Coordinate(0f, 3f));
        controller.newHole();
        controller.mark(new Coordinate(1f, 1f));
        controller.mark(new Coordinate(1f, 2f));
        controller.mark(new Coordinate(2f, 2f));

        assertEquals(hole, controller.getFocusPolygon().getHole(0));
    }

    @Test
    public void testMarkHoleBoundaryStillValid() throws Exception {
        List<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(0f, 0f));
        coordinates.add(new Coordinate(3f, 0f));
        coordinates.add(new Coordinate(3f, 3f));
        coordinates.add(new Coordinate(0f, 3f));

        PolygonController controller = new PolygonController();
        controller.mark(new Coordinate(0f, 0f));
        controller.mark(new Coordinate(3f, 0f));
        controller.mark(new Coordinate(3f, 3f));
        controller.mark(new Coordinate(0f, 3f));
        controller.newHole();
        controller.mark(new Coordinate(1f, 1f));
        controller.mark(new Coordinate(1f, 2f));
        controller.mark(new Coordinate(2f, 2f));

        assertEquals(coordinates, controller.getFocusPolygon().getBoundary());
    }

    @Test
    public void testMark3Hole() throws Exception {
        List<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(2f, 2f));
        coordinates.add(new Coordinate(2f, 3f));
        coordinates.add(new Coordinate(1f, 2f));
        coordinates.add(new Coordinate(1f, 1f));
        Polygon hole = new Polygon(coordinates);

        PolygonController controller = new PolygonController();
        controller.mark(new Coordinate(0f, 0f));
        controller.mark(new Coordinate(3f, 0f));
        controller.mark(new Coordinate(3f, 3f));
        controller.mark(new Coordinate(0f, 3f));
        controller.newHole();
        controller.mark(new Coordinate(1f, 1f));
        controller.mark(new Coordinate(1f, 2f));
        controller.mark(new Coordinate(2f, 2f));
        controller.newHole();
        controller.mark(new Coordinate(2f, 2f));
        controller.mark(new Coordinate(2f, 3f));
        controller.mark(new Coordinate(1f, 2f));
        controller.mark(new Coordinate(1f, 1f));
        controller.newHole();
        controller.mark(new Coordinate(3f, 3f));
        controller.mark(new Coordinate(1f, 1f));
        controller.mark(new Coordinate(2f, 1f));

        assertEquals("2nd Hole should valid when marked 3rd hole", hole, controller.getFocusPolygon().getHole(1));
    }

    @Test(expected = HoleInvalidException.class)
    public void testMarkHolesOutsideOfPolygonShouldThrowException() throws Exception {
        PolygonController controller = new PolygonController();
        controller.mark(new Coordinate(0f, 0f));
        controller.mark(new Coordinate(3f, 0f));
        controller.mark(new Coordinate(3f, 3f));
        controller.mark(new Coordinate(0f, 3f));
        controller.newHole();
        controller.mark(new Coordinate(-1f, -1f));
    }

    @Test
    public void testUndoBoundary() throws Exception {
        ArrayList<Coordinate> boundary = new ArrayList<>();
        boundary.add(new Coordinate(0f, 0f));
        boundary.add(new Coordinate(3f, 0f));
        boundary.add(new Coordinate(3f, 3f));

        PolygonController controller = new PolygonController();
        controller.mark(new Coordinate(0f, 0f));
        controller.mark(new Coordinate(3f, 0f));
        controller.mark(new Coordinate(3f, 3f));
        controller.mark(new Coordinate(0f, 3f));
        controller.undo();

        assertEquals(boundary, controller.getFocusPolygon().getBoundary());

    }

    @Test
    public void testNewUndoHoles() throws Exception {
        ArrayList<Coordinate> hole = new ArrayList<>();
        hole.add(new Coordinate(1f, 1f));
        hole.add(new Coordinate(1f, 2f));
        Polygon unfinishHole = new Polygon(hole);

        PolygonController controller = new PolygonController();
        controller.mark(new Coordinate(0f, 0f));
        controller.mark(new Coordinate(3f, 0f));
        controller.mark(new Coordinate(3f, 3f));
        controller.mark(new Coordinate(0f, 3f));
        controller.newHole();
        controller.mark(new Coordinate(1f, 1f));
        controller.mark(new Coordinate(1f, 2f));
        controller.mark(new Coordinate(2f, 2f));
        controller.undo();

        assertEquals(unfinishHole, controller.getFocusPolygon().getHole(0));

    }

    @Test
    public void testUndoOnSecondHole() throws Exception {
        List<Coordinate> hole = new ArrayList<>();
        hole.add(new Coordinate(1f, 1f));
        hole.add(new Coordinate(1f, 2f));
        Polygon unfinishHole = new Polygon(hole);

        PolygonController controller = new PolygonController();
        controller.mark(new Coordinate(0f, 0f));
        controller.mark(new Coordinate(3f, 0f));
        controller.mark(new Coordinate(3f, 3f));
        controller.mark(new Coordinate(0f, 3f));
        controller.newHole();
        controller.mark(new Coordinate(1f, 1f));
        controller.mark(new Coordinate(1f, 2f));
        controller.mark(new Coordinate(2f, 2f));
        controller.newHole();
        controller.mark(new Coordinate(1f, 0.5f));

        controller.undo();
        controller.undo();

        assertEquals(unfinishHole, controller.getFocusPolygon().getHole(0));
    }

    @Test
    public void testUndoThenMarkMore() throws Exception {
        ArrayList<Coordinate> holeCoordinate = new ArrayList<>();
        holeCoordinate.add(new Coordinate(1f, 1f));
        holeCoordinate.add(new Coordinate(1f, 2f));
        holeCoordinate.add(new Coordinate(2f, 2f));
        Polygon hole = new Polygon(holeCoordinate);

        PolygonController controller = new PolygonController();
        controller.mark(new Coordinate(0f, 0f));
        controller.mark(new Coordinate(3f, 0f));
        controller.mark(new Coordinate(3f, 3f));
        controller.mark(new Coordinate(0f, 3f));
        controller.newHole();
        controller.mark(new Coordinate(1f, 1f));
        controller.mark(new Coordinate(1f, 2f));
        controller.mark(new Coordinate(2f, 2f));
        controller.newHole();
        controller.mark(new Coordinate(1f, 0.5f));

        controller.undo();
        controller.undo();
        controller.mark(new Coordinate(2f, 2f));

        assertEquals(hole, controller.getFocusPolygon().getLastHole());
    }

    @Test
    public void testNewHoleThenUndoToMarkMoreBoundaryPoint() throws Exception {
        ArrayList<Coordinate> boundary = new ArrayList<>();
        boundary.add(new Coordinate(0f, 0f));
        boundary.add(new Coordinate(3f, 0f));
        boundary.add(new Coordinate(3f, 3f));
        boundary.add(new Coordinate(0f, 3f));

        PolygonController controller = new PolygonController();
        controller.mark(new Coordinate(0f, 0f));
        controller.mark(new Coordinate(3f, 0f));
        controller.mark(new Coordinate(3f, 3f));
        controller.newHole();

        controller.undo();
        controller.mark(new Coordinate(0f, 3f));

        assertEquals(boundary, controller.getFocusPolygon().getBoundary());
    }

    @Test
    public void testUndoUntilNoCoordinate() throws Exception {
        ArrayList<Coordinate> boundary = new ArrayList<>();
        boundary.add(new Coordinate(0f, 0f));
        boundary.add(new Coordinate(3f, 0f));
        boundary.add(new Coordinate(3f, 3f));

        PolygonController controller = new PolygonController();
        controller.mark(new Coordinate(0f, 0f));
        controller.mark(new Coordinate(3f, 0f));
        controller.mark(new Coordinate(3f, 3f));
        controller.mark(new Coordinate(0f, 3f));
        controller.newHole();
        controller.mark(new Coordinate(1f, 1f));
        controller.mark(new Coordinate(1f, 2f));

        controller.undo();
        controller.undo();
        controller.undo();
        controller.undo();
        controller.undo();
        controller.undo();
        controller.mark(new Coordinate(0f, 0f));
        controller.mark(new Coordinate(3f, 0f));
        controller.mark(new Coordinate(3f, 3f));

        assertEquals(boundary, controller.getFocusPolygon().getBoundary());
    }

    @Test(expected = HoleInvalidException.class)
    public void testNewHoleWhenLastHoleNotFinishShouldThrowException() throws Exception {
        PolygonController controller = new PolygonController();
        controller.mark(new Coordinate(0f, 0f));
        controller.mark(new Coordinate(3f, 0f));
        controller.mark(new Coordinate(3f, 3f));
        controller.mark(new Coordinate(0f, 3f));
        controller.newHole();
        controller.mark(new Coordinate(1f, 1f));
        controller.mark(new Coordinate(1f, 2f));
        controller.newHole();
    }

    @Test
    public void testMultiPolygon(){
        PolygonController controller = new PolygonController();
        controller.mark(new Coordinate(0f, 0f));
        controller.mark(new Coordinate(3f, 0f));
        controller.mark(new Coordinate(3f, 3f));
        controller.mark(new Coordinate(0f, 3f));
        controller.newPolygon();
        controller.mark(new Coordinate(1f, 1f));
        controller.mark(new Coordinate(1f, 2f));
        controller.mark(new Coordinate(2f, 2f));

        Polygon expected = new Polygon();
        expected.addBoundary(new Coordinate(1f, 1f));
        expected.addBoundary(new Coordinate(1f, 2f));
        expected.addBoundary(new Coordinate(2f, 2f));
        assertEquals(expected, controller.getFocusPolygon());
    }
}
