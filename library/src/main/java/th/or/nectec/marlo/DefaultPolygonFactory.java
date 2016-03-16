package th.or.nectec.marlo;

import android.graphics.Color;
import com.google.android.gms.maps.model.PolygonOptions;

public class DefaultPolygonFactory implements PolygonFactory {
    @Override
    public PolygonOptions build(PolygonMarloFragment fragment) {
        return new PolygonOptions()
                .strokeColor(Color.RED)
                .fillColor(Color.YELLOW)
                .strokeWidth(3);
    }
}
