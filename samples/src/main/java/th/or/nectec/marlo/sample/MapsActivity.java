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

package th.or.nectec.marlo.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import th.or.nectec.marlo.MarkerFactory;
import th.or.nectec.marlo.MarloFragment;
import th.or.nectec.marlo.PolygonFactory;
import th.or.nectec.marlo.PolygonMarloFragment;

public class MapsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        PolygonMarloFragment marlo = (PolygonMarloFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        marlo.setPolygonFactory(new PolygonFactory() {
            @Override
            public PolygonOptions build(PolygonMarloFragment fragment) {
                return new PolygonOptions()
                        .fillColor(Color.DKGRAY)
                        .strokeColor(Color.RED)
                        .strokeWidth(5);
            }
        });
        marlo.setMarkerFactory(new MarkerFactory() {
            @Override
            public MarkerOptions build(MarloFragment fragment, LatLng position) {
                return new MarkerOptions()
                        .position(position);
            }
        });
    }

}
