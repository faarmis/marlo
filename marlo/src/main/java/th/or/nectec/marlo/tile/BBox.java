package th.or.nectec.marlo.tile;

import android.service.quicksettings.Tile;

class BBox {
  static double[] TILE_ORIGIN = new double[2];
  static double MAP_SIZE = 20037508.34789244 * 2;

  Double westBound;
  Double southBound;
  Double eastBound;
  Double northBound;

  static {
    TILE_ORIGIN[0] = -20037508.34789244;
    TILE_ORIGIN[1] = 20037508.34789244;
  }

  public BBox(Double westBound, Double southBound, Double eastBound, Double northBound) {

    this.westBound = westBound;
    this.southBound = southBound;
    this.eastBound = eastBound;
    this.northBound = northBound;
  }

  static BBox forTile(int x, int y, int zoomLevel){
    double tileSize = MAP_SIZE / Math.pow(2.0, zoomLevel);
    Double minX = TILE_ORIGIN[0] + x * tileSize;
    Double maxX = TILE_ORIGIN[0] + (x + 1) * tileSize;
    Double minY = TILE_ORIGIN[1] - (y + 1) * tileSize;
    Double maxY = TILE_ORIGIN[1] - y * tileSize;

    return new BBox(minX, minY, maxX, maxY);
  }

  @Override public String toString() {
    return "bbox="+ westBound + "," + southBound + "," + eastBound + "," + northBound;
  }
}
