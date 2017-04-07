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

package th.or.nectec.marlo.option;


import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import th.or.nectec.marlo.MarloFragment;
import th.or.nectec.marlo.PolygonMarloFragment;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker;

public class DefaultPolygonMarkerOptionFactory implements MarkerOptionFactory {

    @Override
    public MarkerOptions build(MarloFragment fragment, LatLng position) {
        if (!(fragment instanceof PolygonMarloFragment)) {
            throw new IllegalArgumentException("DefaultPolygonMarkerOptionFactory must use with PolygonMarloFragment");
        }
        return new MarkerOptions()
                .position(position)
                .draggable(true)
                .icon(defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
    }
}
