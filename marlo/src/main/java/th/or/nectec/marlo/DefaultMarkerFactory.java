package th.or.nectec.marlo;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker;

public class DefaultMarkerFactory implements MarkerFactory {

    @Override
    public MarkerOptions build(MarloFragment fragment, LatLng position) {
        return new MarkerOptions()
                .position(position)
                .draggable(true)
                .icon(defaultMarker());
    }
}
