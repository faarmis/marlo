package th.or.nectec.marlo;


import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker;

public class DefaultPolygonMarkerFactory implements MarkerFactory {

    @Override
    public MarkerOptions build(MarloFragment fragment, LatLng position) {
        if (!(fragment instanceof PolygonMarloFragment)) {
            throw new IllegalArgumentException("DefaultPolygonMarkerFactory must use with PolygonMarloFragment");
        }
        PolygonMarloFragment polygonMarloFragment = (PolygonMarloFragment) fragment;
        return new MarkerOptions()
                .position(position)
                .draggable(true)
                .icon(defaultMarker(polygonMarloFragment.getDrawingState() == PolygonMarloFragment.State.BOUNDARY
                        ? BitmapDescriptorFactory.HUE_ROSE
                        : BitmapDescriptorFactory.HUE_YELLOW));
    }
}
