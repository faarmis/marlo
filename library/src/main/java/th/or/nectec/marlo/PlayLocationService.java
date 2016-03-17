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

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

public class PlayLocationService {

    private static PlayLocationService instance;
    private final Context context;
    private GoogleApiClient locationApiClient;

    private OnConnectionFailedListener connectionFailedListener = new OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Toast.makeText(context, "ไม่สามารถเชื่อมต่อ Google Play Services ได้", Toast.LENGTH_LONG).show();
        }
    };

    private ConnectionCallbacks connectionCallbacks = new ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {

        }

        @Override
        public void onConnectionSuspended(int i) {
            Toast.makeText(context, "Google Play Service ระงับการติดต่อชั่วคราว", Toast.LENGTH_LONG).show();
        }
    };

    private PlayLocationService(Context context) {
        this.context = context;
        this.locationApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(connectionFailedListener)
                .build();
    }

    public static PlayLocationService getInstance(Context context) {
        if (instance == null)
            instance = new PlayLocationService(context);
        return instance;
    }

    public void connect() {
        if (!locationApiClient.isConnecting() && !locationApiClient.isConnected()) {
            locationApiClient.connect();
        }
    }

    public void disconnect() {
        locationApiClient.disconnect();
    }

    public Location getLastKnowLocation() {
        try {
            return LocationServices.FusedLocationApi.getLastLocation(
                    locationApiClient);
        } catch (SecurityException secure) {
            return null;
        }
    }

}
