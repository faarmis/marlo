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

import org.junit.Test;
import th.or.nectec.marlo.model.Coordinate;

import static org.junit.Assert.assertNotNull;

public class PolygonControllerTest {

    @Test
    public void testMarkValid() throws Exception {
        PolygonController controller = new PolygonController();
        controller.mark(new Coordinate(0f, 0f));
        controller.mark(new Coordinate(1f, 1f));
        controller.mark(new Coordinate(0f, 1f));

        assertNotNull("Polygon should not be NULL", controller.getPolygon());
    }

    @Test(expected = PolygonInvalidException.class)
    public void testMarkIncompletePolygon() throws Exception {
        PolygonController controller = new PolygonController();
        controller.mark(new Coordinate(0f, 0f));
        controller.mark(new Coordinate(0f, 1f));

        controller.getPolygon();
    }
}
