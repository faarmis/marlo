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

package th.or.nectec.marlo.sample;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;

import th.or.nectec.marlo.MarloFragment;
import th.or.nectec.marlo.PolygonMarloFragment;
import th.or.nectec.marlo.model.MarloCoord;
import th.or.nectec.marlo.model.MarloPolygon;
import th.or.nectec.marlo.option.MarkerOptionFactory;
import th.or.nectec.marlo.option.PolygonOptionFactory;
import th.or.nectec.marlo.tile.WMSTileProvider;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker;

public class PolygonActivity extends AppCompatActivity {

    public static final String RESTORE_DATA = "[\n" +
            "      [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ],\n" +
            "      [ [100.2, 0.2], [100.8, 0.2], [100.8, 0.8], [100.2, 0.8], [100.2, 0.2] ]\n" +
            "      ]";
    TextView markerCount;
    private CustomMarloFragment marlo;
    private final PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            try {
                marlo.enableMyLocationButton();
            } catch (SecurityException security) {
                Toast.makeText(PolygonActivity.this, "Not have permission to enable MyLocation button",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(PolygonActivity.this, "MyLocation disable!", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polygon);
        markerCount = findViewById(R.id.marker_count);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        marlo = (CustomMarloFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        marlo.setPolygonOptionFactory(new PolygonOptionFactory() {
            @Override
            public PolygonOptions build(PolygonMarloFragment fragment) {
                return new PolygonOptions()
                        .fillColor(Color.argb(100, 0, 50, 50))
                        .strokeColor(Color.CYAN)
                        .strokeWidth(5);
            }
        });
        marlo.setMarkerOptionFactory(new MarkerOptionFactory() {
            @Override
            public MarkerOptions build(MarloFragment fragment, LatLng position) {
                return new MarkerOptions()
                    .title("Marlo")
                    .snippet(position.latitude + ", " + position.longitude)
                    .icon(defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    .draggable(true)
                    .position(position);
            }
        });
        marlo.setPassiveMakerOptionFactory(new MarkerOptionFactory() {
            @Override
            public MarkerOptions build(MarloFragment fragment, LatLng position) {
                return new MarkerOptions()
                    .position(position)
                    .alpha(0.75f)
                    .draggable(true)
                    .snippet(position.latitude + ", " + position.longitude)
                    .icon(defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            }
        });
        marlo.setActivity(this);
        marlo.setRestoreData(MarloPolygon.fromGeoJson(RESTORE_DATA));
        marlo.useDefaultToolsMenu();


        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\n"
                        + "Please turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

    }

    @Override
    public void onBackPressed() {
        if (!marlo.undo()) {
            super.onBackPressed();
        }
    }

    boolean isShowTools = true;
    boolean isTileProviderConfigured = false;


    public void toggleTools(View view) {
        if (!isTileProviderConfigured) {
            marlo.setTileProvider(WMSTileProvider.FOREST);
        }
        marlo.setTileEnabled(!marlo.isTileEnabled());

        if (isShowTools) {
            marlo.hideToolsMenu();
        }
        else {
            marlo.showToolsMenu();
        }
        isShowTools = !isShowTools;
    }

    public void toggleOverlay(View view) {
        if (!isTileProviderConfigured) {
            marlo.setTileProvider(WMSTileProvider.FOREST);
        }
        marlo.setTileEnabled(!marlo.isTileEnabled());
    }

    public static class CustomMarloFragment extends PolygonMarloFragment {

        private PolygonActivity activity;

        void setActivity(PolygonActivity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPolygonChanged(List<MarloPolygon> polygons, MarloCoord focusCoord) {
            int count = 0;
            for (MarloPolygon poly : polygons) {
                count += poly.getBoundary().size();
                for (MarloPolygon hole : poly.getAllHoles()) {
                    count += hole.getBoundary().size();
                }
            }
            activity.markerCount.setText("Marker = " + count);
        }

        @Override
        protected void onMarkInvalidHole(List<MarloPolygon> polygons, LatLng markPoint) {
            Toast.makeText(getContext(), "InvalidHole", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            super.onMapReady(googleMap);
            toggleMapType();
        }
    }
}
