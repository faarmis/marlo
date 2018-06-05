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

import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.ArrayList;
import java.util.List;

import th.or.nectec.marlo.exception.HoleInvalidException;
import th.or.nectec.marlo.model.MarloCoord;
import th.or.nectec.marlo.model.Polygon;

import static org.junit.Assert.assertEquals;

public class PolygonControllerTest {

    private final PolygonController controller = new PolygonController();

    @Before
    public void setUp() throws Exception {
        controller.setPresenter(new PolygonController.Presenter() {

            @Override
            public void markHole(MarloCoord coordinate) {
            }

            @Override
            public void markBoundary(MarloCoord coordinate) {
            }

            @Override
            public void prepareForNewPolygon() {}

            @Override
            public void prepareForNewHole() {}

            @Override
            public void removeLastMarker() {}

            @Override
            public void clear() {}
        });
    }

    @Test
    public void testMarkValid() throws Exception {
        List<MarloCoord> coordinates = new ArrayList<>();
        coordinates.add(new MarloCoord(0f, 0f));
        coordinates.add(new MarloCoord(1f, 1f));
        coordinates.add(new MarloCoord(0f, 1f));

        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(1f, 1f));
        controller.mark(new MarloCoord(0f, 1f));

        assertEquals(coordinates, controller.getFocusPolygon().getBoundary());

    }

    @Test(expected = IllegalStateException.class)
    public void testChangeToHoleBeforeBoundaryCompleteShouldThrowException() {
        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(0f, 1f));

        controller.newHole();
    }

    @Test
    public void testMark1Hole() throws Exception {
        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(3f, 0f));
        controller.mark(new MarloCoord(3f, 3f));
        controller.mark(new MarloCoord(0f, 3f));
        controller.newHole();
        controller.mark(new MarloCoord(1f, 1f));
        controller.mark(new MarloCoord(1f, 2f));
        controller.mark(new MarloCoord(2f, 2f));

        List<MarloCoord> expectHole = new ArrayList<>();
        expectHole.add(new MarloCoord(1f, 1f));
        expectHole.add(new MarloCoord(1f, 2f));
        expectHole.add(new MarloCoord(2f, 2f));
        assertEquals(expectHole, controller.getFocusPolygon().getHole(0).getBoundary());
    }

    @Test
    public void testMarkHoleBoundaryStillValid() throws Exception {
        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(3f, 0f));
        controller.mark(new MarloCoord(3f, 3f));
        controller.mark(new MarloCoord(0f, 3f));
        controller.newHole();
        controller.mark(new MarloCoord(1f, 1f));
        controller.mark(new MarloCoord(1f, 2f));
        controller.mark(new MarloCoord(2f, 2f));

        List<MarloCoord> expectBound = new ArrayList<>();
        expectBound.add(new MarloCoord(0f, 0f));
        expectBound.add(new MarloCoord(3f, 0f));
        expectBound.add(new MarloCoord(3f, 3f));
        expectBound.add(new MarloCoord(0f, 3f));
        assertEquals(expectBound, controller.getFocusPolygon().getBoundary());
    }

    @Test
    public void testMark3Hole() throws Exception {
        List<MarloCoord> expectHole = new ArrayList<>();
        expectHole.add(new MarloCoord(2f, 2f));
        expectHole.add(new MarloCoord(2f, 3f));
        expectHole.add(new MarloCoord(1f, 2f));
        expectHole.add(new MarloCoord(1f, 1f));

        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(3f, 0f));
        controller.mark(new MarloCoord(3f, 3f));
        controller.mark(new MarloCoord(0f, 3f));
        controller.newHole();
        controller.mark(new MarloCoord(1f, 1f));
        controller.mark(new MarloCoord(1f, 2f));
        controller.mark(new MarloCoord(2f, 2f));
        controller.newHole();
        controller.mark(new MarloCoord(2f, 2f));
        controller.mark(new MarloCoord(2f, 3f));
        controller.mark(new MarloCoord(1f, 2f));
        controller.mark(new MarloCoord(1f, 1f));
        controller.newHole();
        controller.mark(new MarloCoord(3f, 3f));
        controller.mark(new MarloCoord(1f, 1f));
        controller.mark(new MarloCoord(2f, 1f));

        assertEquals("2nd Hole should valid when marked 3rd hole",
                expectHole, controller.getFocusPolygon().getHole(1).getBoundary());
    }

    @Test(expected = HoleInvalidException.class)
    public void testMarkHolesOutsideOfPolygonShouldThrowException() throws Exception {
        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(3f, 0f));
        controller.mark(new MarloCoord(3f, 3f));
        controller.mark(new MarloCoord(0f, 3f));
        controller.newHole();
        controller.mark(new MarloCoord(-1f, -1f));
    }

    @Test
    public void testUndoBoundary() throws Exception {
        ArrayList<MarloCoord> expectBound = new ArrayList<>();
        expectBound.add(new MarloCoord(0f, 0f));
        expectBound.add(new MarloCoord(3f, 0f));
        expectBound.add(new MarloCoord(3f, 3f));

        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(3f, 0f));
        controller.mark(new MarloCoord(3f, 3f));
        controller.mark(new MarloCoord(0f, 3f));
        controller.undo();

        assertEquals(expectBound, controller.getFocusPolygon().getBoundary());

    }

    @Test
    public void testNewUndoHoles() throws Exception {

        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(3f, 0f));
        controller.mark(new MarloCoord(3f, 3f));
        controller.mark(new MarloCoord(0f, 3f));
        controller.newHole();
        controller.mark(new MarloCoord(1f, 1f));
        controller.mark(new MarloCoord(1f, 2f));
        controller.mark(new MarloCoord(2f, 2f));
        controller.undo();

        List<MarloCoord> expectHole = new ArrayList<>();
        expectHole.add(new MarloCoord(1f, 1f));
        expectHole.add(new MarloCoord(1f, 2f));
        assertEquals(expectHole, controller.getFocusPolygon().getHole(0).getBoundary());

    }

    @Test
    public void testUndoOnSecondHole() throws Exception {
        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(3f, 0f));
        controller.mark(new MarloCoord(3f, 3f));
        controller.mark(new MarloCoord(0f, 3f));
        controller.newHole();
        controller.mark(new MarloCoord(1f, 1f));
        controller.mark(new MarloCoord(1f, 2f));
        controller.mark(new MarloCoord(2f, 2f));
        controller.newHole();
        controller.mark(new MarloCoord(1f, 0.5f));

        controller.undo();
        controller.undo();

        List<MarloCoord> expectHole = new ArrayList<>();
        expectHole.add(new MarloCoord(1f, 1f));
        expectHole.add(new MarloCoord(1f, 2f));
        assertEquals(expectHole, controller.getFocusPolygon().getHole(0).getBoundary());
    }

    @Test
    public void testUndoThenMarkMore() throws Exception {
        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(3f, 0f));
        controller.mark(new MarloCoord(3f, 3f));
        controller.mark(new MarloCoord(0f, 3f));
        controller.newHole();
        controller.mark(new MarloCoord(1f, 1f));
        controller.mark(new MarloCoord(1f, 2f));
        controller.mark(new MarloCoord(2f, 2f));
        controller.newHole();
        controller.mark(new MarloCoord(1f, 0.5f));

        controller.undo();
        controller.undo();
        controller.mark(new MarloCoord(2f, 2f));

        List<MarloCoord> expectHold = new ArrayList<>();
        expectHold.add(new MarloCoord(1f, 1f));
        expectHold.add(new MarloCoord(1f, 2f));
        expectHold.add(new MarloCoord(2f, 2f));
        assertEquals(expectHold, controller.getFocusPolygon().getLastHole().getBoundary());
    }

    @Test
    public void testNewHoleThenUndoToMarkMoreBoundaryPoint() throws Exception {
        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(3f, 0f));
        controller.mark(new MarloCoord(3f, 3f));
        controller.newHole();

        controller.undo();
        controller.mark(new MarloCoord(0f, 3f));

        List<MarloCoord> expectBound = new ArrayList<>();
        expectBound.add(new MarloCoord(0f, 0f));
        expectBound.add(new MarloCoord(3f, 0f));
        expectBound.add(new MarloCoord(0f, 3f));
        assertEquals(expectBound, controller.getFocusPolygon().getBoundary());
    }

    @Test
    public void testUndoUntilNoCoordinate() throws Exception {
        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(3f, 0f));
        controller.mark(new MarloCoord(3f, 3f));
        controller.mark(new MarloCoord(0f, 3f));
        controller.newHole();
        controller.mark(new MarloCoord(1f, 1f));
        controller.mark(new MarloCoord(1f, 2f));

        controller.undo();
        controller.undo();
        controller.undo();
        controller.undo();
        controller.undo();
        controller.undo();
        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(3f, 0f));
        controller.mark(new MarloCoord(3f, 3f));

        List<MarloCoord> expectBound = new ArrayList<>();
        expectBound.add(new MarloCoord(0f, 0f));
        expectBound.add(new MarloCoord(3f, 0f));
        expectBound.add(new MarloCoord(3f, 3f));
        assertEquals(expectBound, controller.getFocusPolygon().getBoundary());
    }

    @Test(expected = IllegalStateException.class)
    public void testNewHoleWhenLastHoleNotFinishShouldThrowException() throws Exception {
        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(3f, 0f));
        controller.mark(new MarloCoord(3f, 3f));
        controller.mark(new MarloCoord(0f, 3f));
        controller.newHole();
        controller.mark(new MarloCoord(1f, 1f));
        controller.mark(new MarloCoord(1f, 2f));

        controller.newHole();
    }

    @Test
    public void testMultiPolygon() throws Exception {
        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(3f, 0f));
        controller.mark(new MarloCoord(3f, 3f));
        controller.mark(new MarloCoord(0f, 3f));
        controller.startNewPolygon();
        controller.mark(new MarloCoord(1f, 1f));
        controller.mark(new MarloCoord(1f, 2f));
        controller.mark(new MarloCoord(2f, 2f));

        List<Polygon> actual = controller.getPolygons();
        assertEquals(2, actual.size());
        JSONAssert.assertEquals("{ \"type\":\"MultiPolygon\", \"coordinates\":"
                        + "[ [[[0,0],[0,3],[3,3],[3,0],[0,0]]],[[[1,1],[2,1],[2,2],[1,1]]] ] }",
                Polygon.toGeoJson(actual), true);
    }

    @Test(expected = IllegalStateException.class)
    public void testNewPolygonWhenLastPolygonNotFinishShouldThrowException() throws Exception {
        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(3f, 0f));

        controller.startNewPolygon();
    }

    @Test(expected = IllegalStateException.class)
    public void testNewPolygonWhenLastHoleNotFinishShouldThrowException() throws Exception {
        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(3f, 0f));
        controller.mark(new MarloCoord(3f, 3f));
        controller.mark(new MarloCoord(0f, 3f));
        controller.newHole();
        controller.mark(new MarloCoord(1f, 1f));
        controller.mark(new MarloCoord(1f, 2f));

        controller.startNewPolygon();
    }

    @Test
    public void testUndoToLastPolygonThenDrawNewHole() throws Exception {
        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(3f, 0f));
        controller.mark(new MarloCoord(3f, 3f));
        controller.mark(new MarloCoord(0f, 3f));
        controller.startNewPolygon();
        controller.mark(new MarloCoord(1f, 1f));
        controller.mark(new MarloCoord(1f, 2f));

        controller.undo();
        controller.undo();
        controller.newHole();
        controller.mark(new MarloCoord(1f, 1f));
        controller.mark(new MarloCoord(1f, 2f));
        controller.mark(new MarloCoord(2f, 2f));

        assertEquals(3, controller.getFocusPolygon().getLastHole().getBoundary().size());
    }

    @Test
    public void testIgnoreMarkSameCoordinateAsLastMark() throws Exception {
        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(3f, 0f));

        controller.mark(new MarloCoord(3f, 0f));

        assertEquals(2, controller.getFocusPolygon().getBoundary().size());
    }

    @Test
    public void replace() throws Exception {
        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(1f, 1f));
        controller.mark(new MarloCoord(0f, 1f));

        controller.replaceWith(new MarloCoord(0f, 1f), new MarloCoord(0f, 5f));

        List<MarloCoord> expectBound = new ArrayList<>();
        expectBound.add(new MarloCoord(0f, 0f));
        expectBound.add(new MarloCoord(1f, 1f));
        expectBound.add(new MarloCoord(0f, 5f));
        assertEquals(expectBound, controller.getFocusPolygon().getBoundary());
    }

    @Test
    public void replaceHole() throws Exception {
        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(3f, 0f));
        controller.mark(new MarloCoord(3f, 3f));
        controller.mark(new MarloCoord(0f, 3f));
        controller.newHole();
        controller.mark(new MarloCoord(1f, 1f));
        controller.mark(new MarloCoord(1f, 2f));
        controller.mark(new MarloCoord(2f, 2f));

        controller.replaceWith(new MarloCoord(2f, 2f), new MarloCoord(2f, 1.5f));

        List<MarloCoord> expectHole = new ArrayList<>();
        expectHole.add(new MarloCoord(1f, 1f));
        expectHole.add(new MarloCoord(1f, 2f));
        expectHole.add(new MarloCoord(2f, 1.5f));
        assertEquals(expectHole, controller.getFocusPolygon().getLastHole().getBoundary());
    }

    @Test
    public void replaceSecoundHole() throws Exception {
        controller.mark(new MarloCoord(0f, 0f));
        controller.mark(new MarloCoord(3f, 0f));
        controller.mark(new MarloCoord(3f, 3f));
        controller.mark(new MarloCoord(0f, 3f));
        controller.startNewPolygon();
        controller.mark(new MarloCoord(1f, 1f));
        controller.mark(new MarloCoord(1f, 2f));
        controller.mark(new MarloCoord(2f, 2f));

        controller.replaceWith(new MarloCoord(2f, 2f), new MarloCoord(2f, 1.5f));

        List<MarloCoord> expectBound = new ArrayList<>();
        expectBound.add(new MarloCoord(1f, 1f));
        expectBound.add(new MarloCoord(1f, 2f));
        expectBound.add(new MarloCoord(2f, 1.5f));
        assertEquals(expectBound, controller.getFocusPolygon().getBoundary());
    }

}
