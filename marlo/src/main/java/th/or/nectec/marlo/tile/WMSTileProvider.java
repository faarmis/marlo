package th.or.nectec.marlo.tile;

import android.util.Log;
import androidx.annotation.Nullable;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.UrlTileProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import th.or.nectec.marlo.BuildConfig;

public class WMSTileProvider extends UrlTileProvider {

  String url;

  public WMSTileProvider(String url) {
    super(250, 250);
    this.url = url;
  }

  @Nullable  @Override
  public URL getTileUrl(int x, int y, int zoom) {
    BBox bbox = BBox.forTile(x, y, zoom);
    String s = String.format(Locale.US, url, bbox.toString());
    if (BuildConfig.DEBUG) Log.d("WMSTileProvider", s);
    URL url = null;
    try {
      url = new URL(s);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return url;
  }

  public TileOverlayOptions toOverlayOption() {
    return new TileOverlayOptions().tileProvider(this).fadeIn(true);
  }

  public static WMSTileProvider THAICHOTE = new WMSTileProvider(
      "http://go-tiles1.gistda.or.th/mapproxy/service" +
          "?LAYERS=thaichote_4326" +
          "&styles=" +
          "&FORMAT=image/jpeg" +
          "&SRS=EPSG:900913" +
          "&EXCEPTIONS=application/vnd.ogc.se_inimage" +
          "&SERVICE=WMS" +
          "&VERSION=1.1.1" +
          "&REQUEST=GetMap" +
          "&%s" +
          "&WIDTH=256" +
          "&HEIGHT=256"
  );

  public static WMSTileProvider FOREST = new WMSTileProvider(
      "http://map.dsi.go.th/cgi-bin/mapserv" +
          "?MAP=/ms603/map/wms-dsi.map" +
          "&LAYERS=fr_nrf" +
          "&TRANSPARENT=TRUE" +
          "&SERVICE=WMS" +
          "&VERSION=1.1.1" +
          "&REQUEST=GetMap" +
          "&STYLES=" +
          "&FORMAT=image/png" +
          "&SRS=EPSG:900913" +
          "&%s" +
          "&WIDTH=256" +
          "&HEIGHT=256"
  );

}
