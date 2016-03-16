package th.or.nectec.marlo;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public interface MarkerFactory {

    MarkerOptions build(MarloFragment fragment, LatLng position);
}
