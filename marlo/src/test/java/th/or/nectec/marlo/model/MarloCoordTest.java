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

import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MarloCoordTest {

    private static final double DELTA = 0.000001; //6 decimal, 10cm accuracy
    private static final double LAT = 14.078606;
    private static final double LONG = 100.603120;

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    private final MarloCoord location = new MarloCoord(LAT, LONG);

    @Test
    public void createByLatLng() throws Exception {
        MarloCoord coordinate = new MarloCoord(LAT, LONG);
        MarloCoord coordinateFromLatLng = new MarloCoord(new LatLng(LAT, LONG));

        assertEquals(coordinate, coordinateFromLatLng);
    }

    @Test
    public void getLatitude() {
        assertEquals(LAT, location.getLatitude(), DELTA);
    }

    @Test
    public void getLongitude() {
        assertEquals(LONG, location.getLongitude(), DELTA);
    }

    @Test
    public void coordinateWithDifferentLatitudeMustNotEquals() {
        MarloCoord anotherLocation = new MarloCoord(15.078606, LONG);

        assertNotEquals(location, anotherLocation);
    }

    @Test
    public void coordinateWithDifferentLongitudeMustNotEquals() {
        MarloCoord anotherLocation = new MarloCoord(LAT, 179.603120);

        assertNotEquals(location, anotherLocation);
    }

    @Test
    public void coordinateTheSameLatitudeLongitudeMustEquals() {
        MarloCoord sameLocation = new MarloCoord(LAT, LONG);

        assertEquals(location, sameLocation);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOutOfRangeLatitude() throws Exception {
        new MarloCoord(-90.1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOutOfRangeLongitude() throws Exception {
        new MarloCoord(0, 180.1f);
    }

    @Test
    public void toGeoJson() throws Exception {
        JSONAssert.assertEquals("[ 100.60312, 14.078606 ]", new MarloCoord(LAT, LONG).toGeoJson(), false);
    }

    @Test
    public void fromGeoJson() throws Exception {
        Assert.assertEquals(new MarloCoord(LAT, LONG), MarloCoord.fromGeoJson("[ 100.60312, 14.078606 ]"));

    }
}
