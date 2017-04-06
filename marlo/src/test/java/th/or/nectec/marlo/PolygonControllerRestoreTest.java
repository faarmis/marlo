package th.or.nectec.marlo;

import org.junit.Before;
import org.junit.Test;

import th.or.nectec.marlo.model.Coordinate;
import th.or.nectec.marlo.model.Polygon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class PolygonControllerRestoreTest {
    private final PolygonController controller = new PolygonController();

    @Before
    public void setUp() throws Exception {
        controller.setPresenter(new PolygonController.Presenter() {
            @Override
            public void markHole(Coordinate coordinate) {

            }

            @Override
            public void markBoundary(Coordinate coordinate) {

            }

            @Override
            public void prepareForNewPolygon() {

            }

            @Override
            public void prepareForNewHole() {

            }

            @Override
            public void removeLastMarker() {

            }
        });
    }

    @Test
    public void testRestorePolygon() throws Exception {
        Polygon polygon = new Polygon();
        polygon.add(new Coordinate(0f, 0f));
        polygon.add(new Coordinate(3f, 0f));
        polygon.add(new Coordinate(3f, 3f));
        polygon.add(new Coordinate(0f, 3f));

        controller.retore(polygon);

        assertEquals(polygon, controller.getFocusPolygon());
    }

    @Test
    public void testRestorePolygonWithHole() throws Exception {
        Polygon polygon = new Polygon();
        polygon.add(new Coordinate(0f, 0f));
        polygon.add(new Coordinate(3f, 0f));
        polygon.add(new Coordinate(3f, 3f));
        polygon.add(new Coordinate(0f, 3f));

        Polygon hole = new Polygon();
        hole.add(new Coordinate(2f, 2f));
        hole.add(new Coordinate(2f, 3f));
        hole.add(new Coordinate(1f, 2f));
        hole.add(new Coordinate(1f, 1f));
        polygon.addHoles(hole);

        controller.retore(polygon);

        System.out.println(polygon.toGeoJson().toString());
        assertEquals(polygon, controller.getFocusPolygon());
    }

    @Test
    public void polygonInControllerNotSameInstanceWithRestorePolygon() throws Exception {
        Polygon polygon = new Polygon();
        polygon.add(new Coordinate(0f, 0f));
        polygon.add(new Coordinate(3f, 0f));
        polygon.add(new Coordinate(3f, 3f));
        polygon.add(new Coordinate(0f, 3f));

        controller.retore(polygon);
        polygon.add(new Coordinate(0f, 1.5f));

        assertTrue(polygon != controller.getFocusPolygon());
        assertNotEquals(polygon, controller.getFocusPolygon());
    }
}
