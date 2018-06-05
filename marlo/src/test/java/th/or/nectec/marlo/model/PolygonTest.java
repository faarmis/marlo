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

package th.or.nectec.marlo.model;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Arrays;
import java.util.List;

public class PolygonTest {


    @Test
    public void toJson() throws Exception {
        Polygon polygon = new Polygon();
        polygon.add(new MarloCoord(1f, 1f));
        polygon.add(new MarloCoord(1f, 2f));
        polygon.add(new MarloCoord(2f, 2f));

        String expect = "{ \"type\":\"Polygon\", \"coordinates\":[[[1,1],[2,1],[2,2],[1,1]]] }";
        JSONAssert.assertEquals(expect, polygon.toGeoJson().toString(), false);
    }

    @Test
    public void toJsonWithHole() throws Exception {
        Polygon polygon = new Polygon();
        polygon.add(new MarloCoord(0f, 0f));
        polygon.add(new MarloCoord(3f, 0f));
        polygon.add(new MarloCoord(3f, 3f));
        polygon.add(new MarloCoord(0f, 3f));

        Polygon hole = new Polygon();
        hole.add(new MarloCoord(1f, 1f));
        hole.add(new MarloCoord(1f, 2f));
        hole.add(new MarloCoord(2f, 2f));

        polygon.addHoles(hole);
        String expect = "{ \"type\":\"Polygon\", \"coordinates\":"
                + "[ [[0,0],[0,3],[3,3],[3,0],[0,0]],[[1,1],[2,1],[2,2],[1,1]] ] }";
        JSONAssert.assertEquals(expect, polygon.toGeoJson(), true);
    }

    @Test
    public void fromGeoJsonCoordinates() throws Exception {
        Polygon polygon = new Polygon();
        polygon.add(new MarloCoord(1f, 1f));
        polygon.add(new MarloCoord(1f, 2f));
        polygon.add(new MarloCoord(2f, 2f));

        String expect = "[[[1,1],[2,1],[2,2]]]";
        Assert.assertEquals(polygon, Polygon.fromGeoJson(expect));
    }

    @Test
    public void fromGeoJsonObject() throws Exception {
        Polygon polygon = new Polygon();
        polygon.add(new MarloCoord(1f, 1f));
        polygon.add(new MarloCoord(1f, 2f));
        polygon.add(new MarloCoord(2f, 2f));

        String expect = "{ \"type\":\"Polygon\", \"coordinates\":[[[1,1],[2,1],[2,2]]] }";
        Assert.assertEquals(polygon, Polygon.fromGeoJson(expect));
    }

    @Test
    public void fromGeoJsonCoordinatesWithHole() throws Exception {
        Polygon polygon = new Polygon();
        polygon.add(new MarloCoord(0f, 0f));
        polygon.add(new MarloCoord(3f, 0f));
        polygon.add(new MarloCoord(3f, 3f));
        polygon.add(new MarloCoord(0f, 3f));

        Polygon hole = new Polygon();
        hole.add(new MarloCoord(1f, 1f));
        hole.add(new MarloCoord(1f, 2f));
        hole.add(new MarloCoord(2f, 2f));
        polygon.addHoles(hole);

        Assert.assertEquals(polygon,
                Polygon.fromGeoJson("[[ [0,0],[0,3],[3,3],[3,0]],[[1,1],[2,1],[2,2] ]]"));
    }

    @Test
    public void fromGeoJsonObjectWithHole() throws Exception {
        Polygon polygon = new Polygon();
        polygon.add(new MarloCoord(0f, 0f));
        polygon.add(new MarloCoord(3f, 0f));
        polygon.add(new MarloCoord(3f, 3f));
        polygon.add(new MarloCoord(0f, 3f));

        Polygon hole = new Polygon();
        hole.add(new MarloCoord(1f, 1f));
        hole.add(new MarloCoord(1f, 2f));
        hole.add(new MarloCoord(2f, 2f));
        polygon.addHoles(hole);

        Assert.assertEquals(polygon,
                Polygon.fromGeoJson("{ \"type\":\"Polygon\", \"coordinates\": "
                        + "[[ [0,0],[0,3],[3,3],[3,0]],[[1,1],[2,1],[2,2] ]] }"));
    }

    @Test
    public void fromMultiPolyGeoJson() throws Exception {
        Polygon polygon = new Polygon();
        polygon.add(new MarloCoord(0f, 0f));
        polygon.add(new MarloCoord(3f, 0f));
        polygon.add(new MarloCoord(3f, 3f));
        polygon.add(new MarloCoord(0f, 3f));

        Polygon hole = new Polygon();
        hole.add(new MarloCoord(1f, 1f));
        hole.add(new MarloCoord(1f, 2f));
        hole.add(new MarloCoord(2f, 2f));
        polygon.addHoles(hole);


        Assert.assertEquals(Arrays.asList(polygon),
                Polygon.fromGeoJsonMultiPolygon("[[[ [0,0],[0,3],[3,3],[3,0]],[[1,1],[2,1],[2,2] ]]]"));
    }

    @Test
    public void fromMultiPolyGeoJsonMulti() throws Exception {
        Polygon poly1 = new Polygon();
        poly1.add(40, 180);
        poly1.add(50, 180);
        poly1.add(50, 170);
        poly1.add(40, 170);

        Polygon poly2 = new Polygon();
        poly2.add(40, -170);
        poly2.add(50, -170);
        poly2.add(50, -180);
        poly2.add(40, -180);

        List<Polygon> expected = Arrays.asList(poly1, poly2);
        List<Polygon> actual = Polygon.fromGeoJsonMultiPolygon("{\"type\": \"MultiPolygon\", \"coordinates\": "
                + "[ [[[180.0, 40.0], [180.0, 50.0], [170.0, 50.0], [170.0, 40.0], [180.0, 40.0]]], "
                + "[[[-170.0, 40.0], [-170.0, 50.0], [-180.0, 50.0], [-180.0, 40.0], [-170.0, 40.0]]] ] }");
        Assert.assertEquals(expected, actual);
    }
}
