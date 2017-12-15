package th.or.nectec.marlo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class PlayLocationService {
    private Activity context;
    private OnReceivedLocation onReceivedLocation;

    public static final int REQUEST_CHECK_SETTINGS = 10512;

    public PlayLocationService(Activity context) {
        this.context = context;
    }

    @SuppressLint("MissingPermission")
    @RequiresPermission(anyOf = {
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION})
    public void getLastKnownLocation(OnReceivedLocation listener) {
        onReceivedLocation = listener;

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);
        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location lastKnowLocation = task.getResult();
                if (lastKnowLocation != null && onReceivedLocation != null) {
                    onReceivedLocation.onReceived(lastKnowLocation);
                } else {
                    requestLocationSetting();
                }
            }
        });
    }

    private void requestLocationSetting() {
        LocationRequest defaultRequest = new LocationRequest();
        defaultRequest.setInterval(10000);
        defaultRequest.setFastestInterval(5000);
        defaultRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        requestLocationSetting(defaultRequest);
    }


    private void requestLocationSetting(final LocationRequest request) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(request);
        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(context, new OnSuccessListener<LocationSettingsResponse>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getLastKnownLocation(onReceivedLocation);
            }
        });
        task.addOnFailureListener(context, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(context, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException ignore) {
                        //Ignore exception
                    }
                }
            }
        });
    }

    interface OnReceivedLocation {
        void onReceived(Location location);
    }
}
