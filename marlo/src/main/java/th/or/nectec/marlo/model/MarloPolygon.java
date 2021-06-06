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

package th.or.nectec.marlo.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MarloPolygon implements Parcelable {

    public static final Parcelable.Creator<MarloPolygon> CREATOR = new Parcelable.Creator<MarloPolygon>() {
        @Override
        public MarloPolygon createFromParcel(Parcel source) {
            return new MarloPolygon(source);
        }

        @Override
        public MarloPolygon[] newArray(int size) {
            return new MarloPolygon[size];
        }
    };
    private final List<MarloCoord> boundary;
    private final List<MarloPolygon> holes;

    public MarloPolygon() {
        boundary = new ArrayList<>();
        holes = new ArrayList<>();
    }

    public MarloPolygon(MarloPolygon polygon) {
        boundary = new ArrayList<>(MarloCoord.clones(polygon.getBoundary()));
        holes = new ArrayList<>();
        for (MarloPolygon hole : polygon.getAllHoles()) {
            holes.add(new MarloPolygon(hole));
        }
    }

    public MarloPolygon(List<MarloCoord> boundary) {
        this(boundary, new ArrayList<MarloPolygon>());
    }

    public MarloPolygon(List<MarloCoord> boundary, List<MarloPolygon> holes) {
        this.boundary = boundary;
        this.holes = holes;
    }

    private MarloPolygon(Parcel in) {
        this.boundary = in.createTypedArrayList(MarloCoord.CREATOR);

        int holesCount = in.readInt();
        this.holes = new ArrayList<>();
        for (int hole = 0; hole < holesCount; hole++) {
            this.holes.add((MarloPolygon) in.readValue(MarloPolygon.class.getClassLoader()));
        }
    }

    /**
     * @param geojson String of GeoJson as MarloPolygon Geometry object or coordinate
     * @return Object create from GeoJson
     */
    public static MarloPolygon fromGeoJson(@NonNull String geojson) {
        try {
            JSONTokener tokener = new JSONTokener(geojson);
            Object json = tokener.nextValue();
            if (json instanceof JSONObject) {
                return fromGeoJson((JSONObject) json);
            } else if (json instanceof JSONArray) {
                return fromGeoJson((JSONArray) json);
            } else {
                throw new RuntimeException("Not support json");
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param polygon JsonObject as Geometry format of GeoJson spec.
     * @return Object create from GeoJson
     */
    public static MarloPolygon fromGeoJson(@NonNull JSONObject polygon) {
        try {
            return fromGeoJson(polygon.getJSONArray("coordinates"));
        } catch (JSONException error) {
            throw new RuntimeException(error);
        }
    }

    /**
     * @param coordinates JsonArray of coordinates field in GeoJson's polygon geometry
     * @return object from GeoJson
     */
    public static MarloPolygon fromGeoJson(@NonNull JSONArray coordinates) {
        try {
            MarloPolygon returnObj = new MarloPolygon();
            for (int boundaryIndex = 0; boundaryIndex < coordinates.length(); boundaryIndex++) {
                MarloPolygon polygon = boundaryIndex == 0 ? returnObj : new MarloPolygon();
                JSONArray boundary = coordinates.getJSONArray(boundaryIndex);
                for (int coordIndex = 0; coordIndex < boundary.length(); coordIndex++) {
                    polygon.add(MarloCoord.fromGeoJson(boundary.get(coordIndex).toString()));
                }
                if (boundaryIndex != 0) returnObj.addHoles(polygon);
            }
            return returnObj;
        } catch (JSONException error) {
            throw new RuntimeException(error);
        }
    }

    /**
     * @param geojson String of GeoJson as MultiPolygon Geometry object or coordinates
     * @return object from GeoJson
     */
    public static List<MarloPolygon> fromGeoJsonMultiPolygon(@NonNull String geojson) {
        try {
            JSONTokener tokener = new JSONTokener(geojson);
            Object json = tokener.nextValue();
            if (json instanceof JSONObject) {
                return fromGeoJsonMultiPolygon((JSONObject) json);
            } else if (json instanceof JSONArray) {
                return fromGeoJsonMultiPolygon((JSONArray) json);
            } else {
                throw new RuntimeException("Input not support type");
            }
        } catch (JSONException error) {
            throw new RuntimeException(error);
        }
    }

    /**
     * @param geometry String of GeoJson as MultiPolygon Geometry object or coordinates
     * @return object from GeoJson
     */
    public static List<MarloPolygon> fromGeoJsonMultiPolygon(@NonNull JSONObject geometry) {
        try {
            return fromGeoJsonMultiPolygon(geometry.getJSONArray("coordinates"));
        } catch (JSONException error) {
            throw new RuntimeException("Not found coordinates filed", error);
        }
    }

    /**
     * @param coordinates GeoJson array of MultiPolygon
     * @return object from coordinates
     */
    public static List<MarloPolygon> fromGeoJsonMultiPolygon(@NonNull JSONArray coordinates) {
        try {
            List<MarloPolygon> polygons = new ArrayList<>();
            for (int polygonIndex = 0; polygonIndex < coordinates.length(); polygonIndex++) {
                polygons.add(MarloPolygon.fromGeoJson(coordinates.getJSONArray(polygonIndex)));
            }
            return polygons;
        } catch (JSONException error) {
            throw new RuntimeException(error);
        }
    }

    @NonNull
    public static JSONObject toGeoJson(List<MarloPolygon> multiPolygon) {
        try {
            JSONObject geoJson = new JSONObject();
            geoJson.put("type", "MultiPolygon");
            JSONArray coordinate = new JSONArray();
            for (MarloPolygon polygon : multiPolygon) {
                coordinate.put(polygon.toGeoJsonCoordinates());
            }
            geoJson.put("coordinates", coordinate);
            return geoJson;
        } catch (JSONException error) {
            throw new RuntimeException(error);
        }
    }

    /**
     * @param coord
     * @return
     */
    public boolean isCoordinateExist(MarloCoord coord) {
        for (MarloCoord point : boundary) {
            if (point.equals(coord))
                return true;
            for (MarloPolygon hole : holes) {
                if (hole.isCoordinateExist(coord))
                    return true;
            }
        }
        return false;
    }

    public List<MarloCoord> getBoundary() {
        return boundary;
    }

    public List<MarloPolygon> getAllHoles() {
        return new ArrayList<>(holes);
    }

    /**
     * @return JsonObject in GeoJson's geometry format
     */
    public JSONObject toGeoJson() {
        try {
            JSONObject geoJson = new JSONObject();
            geoJson.put("type", "MarloPolygon");
            JSONArray coordinate = toGeoJsonCoordinates();
            geoJson.put("coordinates", coordinate);
            return geoJson;
        } catch (JSONException error) {
            throw new RuntimeException(error);
        }
    }

    public PolygonOptions toPolygonOptions() {
        return toPolygonOptions(new PolygonOptions());
    }

    public PolygonOptions toPolygonOptions(PolygonOptions options) {
        options.addAll(MarloCoord.toLatLngs(boundary));
        for (MarloPolygon hold : holes) {
            options.addHole(MarloCoord.toLatLngs(hold.boundary));
        }
        return options;
    }

    @NonNull
    private JSONArray toGeoJsonCoordinates() {
        JSONArray coordinate = new JSONArray();
        coordinate.put(boundaryToJson());
        for (MarloPolygon hole : holes) {
            if (hole.isEmpty() || !hole.isValid())
                continue;
            coordinate.put(hole.boundaryToJson());
        }
        return coordinate;
    }

    @NonNull
    private JSONArray boundaryToJson() {
        JSONArray jsonBoundary = new JSONArray();
        for (MarloCoord point : boundary) {
            jsonBoundary.put(point.toGeoJson());
        }
        if (!boundary.get(0).equals(boundary.get(boundary.size() - 1)))
            jsonBoundary.put(boundary.get(0).toGeoJson());
        return jsonBoundary;
    }

    public void add(MarloCoord coordinate) {
        if (boundary.size() > 0 && boundary.get(0).equals(coordinate))
            return; //ignore close position
        boundary.add(coordinate);
    }

    public void add(double lat, double lng) {
        add(new MarloCoord(lat, lng));
    }

    public void addHoles(MarloPolygon coordinates) {
        holes.add(coordinates);
    }

    public MarloPolygon getHole(int holeIndex) {
        return holes.get(holeIndex);
    }

    public boolean isValid() {
        return boundary.size() >= 3;
    }

    public boolean isEmpty() {
        return boundary.size() == 0;
    }

    public int getHolesCount() {
        return holes.size();
    }

    public MarloPolygon getLastHole() {
        return holes.get(holes.size() - 1);
    }

    public boolean haveHole() {
        return getHolesCount() > 0;
    }

    @Nullable
    public MarloCoord getLastCoordinate() {
        if (haveHole()) {
            MarloCoord coord;
            if ((coord = getLastHole().getLastCoordinate()) != null)
                return coord;
        }

        if (!isEmpty()) {
            MarloCoord peek = boundary.get(boundary.size() - 1);
            return new MarloCoord(peek.getLatitude(), peek.getLongitude());
        } else {
            return null;
        }
    }

    public MarloCoord pop() {
        if (!boundary.isEmpty()) {
            return boundary.remove(boundary.size() - 1);
        }
        return null;
    }

    public void removeHole(MarloPolygon hole) {
        holes.remove(hole);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarloPolygon polygon = (MarloPolygon) o;
        return Objects.equals(boundary, polygon.boundary)
                && Objects.equals(holes, polygon.holes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boundary, holes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.boundary);

        dest.writeInt(this.holes.size());
        for (MarloPolygon hole : this.holes) {
            dest.writeValue(hole);
        }
    }

    public boolean replace(MarloCoord oldCoord, MarloCoord newCoord) {
        int coordPosition = boundary.indexOf(oldCoord);
        if (coordPosition > -1) {
            boundary.set(coordPosition, newCoord);
            return true;
        }
        for (MarloPolygon hole : holes) {
            if (hole.replace(oldCoord, newCoord))
                return true;
        }
        return false;
    }
}
