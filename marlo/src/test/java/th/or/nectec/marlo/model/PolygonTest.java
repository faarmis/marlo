package th.or.nectec.marlo.model;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class PolygonTest {


    @Test
    public void toJson() throws Exception {
        Polygon polygon = new Polygon();
        polygon.add(new Coordinate(1f, 1f));
        polygon.add(new Coordinate(1f, 2f));
        polygon.add(new Coordinate(2f, 2f));

        String expect = "[[[1,1],[2,1],[2,2]]]";
        JSONAssert.assertEquals(expect, polygon.toGeoJson().toString(), false);
    }

    @Test
    public void toJsonWithHole() throws Exception {
        Polygon polygon = new Polygon();
        polygon.add(new Coordinate(0f, 0f));
        polygon.add(new Coordinate(3f, 0f));
        polygon.add(new Coordinate(3f, 3f));
        polygon.add(new Coordinate(0f, 3f));

        Polygon hole = new Polygon();
        hole.add(new Coordinate(1f, 1f));
        hole.add(new Coordinate(1f, 2f));
        hole.add(new Coordinate(2f, 2f));
        polygon.addHoles(hole);

        String expect = "[[[0,0],[0,3],[3,3],[3,0]],[[1,1],[2,1],[2,2]]]";
        JSONAssert.assertEquals(expect, polygon.toGeoJson().toString(), false);
    }

    @Test
    public void fromGeoJson() throws Exception {
        Polygon polygon = new Polygon();
        polygon.add(new Coordinate(1f, 1f));
        polygon.add(new Coordinate(1f, 2f));
        polygon.add(new Coordinate(2f, 2f));

        String expect = "[[[1,1],[2,1],[2,2]]]";
        Assert.assertEquals(polygon, Polygon.fromGeoJson(expect));
    }

    @Test
    public void fromGeoJsonWithHole() throws Exception {
        Polygon polygon = new Polygon();
        polygon.add(new Coordinate(0f, 0f));
        polygon.add(new Coordinate(3f, 0f));
        polygon.add(new Coordinate(3f, 3f));
        polygon.add(new Coordinate(0f, 3f));

        Polygon hole = new Polygon();
        hole.add(new Coordinate(1f, 1f));
        hole.add(new Coordinate(1f, 2f));
        hole.add(new Coordinate(2f, 2f));
        polygon.addHoles(hole);

        Assert.assertEquals(polygon, Polygon.fromGeoJson("[[ [0,0],[0,3],[3,3],[3,0]],[[1,1],[2,1],[2,2] ]]"));
    }
}