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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Polygon implements Parcelable {

    public static final Parcelable.Creator<Polygon> CREATOR = new Parcelable.Creator<Polygon>() {
        @Override
        public Polygon createFromParcel(Parcel source) {
            return new Polygon(source);
        }

        @Override
        public Polygon[] newArray(int size) {
            return new Polygon[size];
        }
    };
    private final List<Coordinate> boundary;
    private final List<Polygon> holes;

    public Polygon() {
        boundary = new ArrayList<>();
        holes = new ArrayList<>();
    }

    public Polygon(Polygon polygon) {
        boundary = new ArrayList<>(Coordinate.clones(polygon.getBoundary()));
        holes = new ArrayList<>();
        for (Polygon hole : polygon.getAllHoles()) {
            holes.add(new Polygon(hole));
        }
    }

    public Polygon(List<Coordinate> boundary) {
        this(boundary, new ArrayList<Polygon>());
    }

    public Polygon(List<Coordinate> boundary, List<Polygon> holes) {
        this.boundary = boundary;
        this.holes = holes;
    }

    private Polygon(Parcel in) {
        this.boundary = in.createTypedArrayList(Coordinate.CREATOR);

        int holesCount = in.readInt();
        this.holes = new ArrayList<>();
        for (int hole = 0; hole < holesCount; hole++) {
            this.holes.add((Polygon) in.readValue(Polygon.class.getClassLoader()));
        }
    }

    /**
     * @param geojson String of GeoJson as Polygon Geometry object or coordinate
     * @return Object create from GeoJson
     */
    public static Polygon fromGeoJson(@NonNull String geojson) {
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
    public static Polygon fromGeoJson(@NonNull JSONObject polygon) {
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
    public static Polygon fromGeoJson(@NonNull JSONArray coordinates) {
        try {
            Polygon returnObj = new Polygon();
            for (int boundaryIndex = 0; boundaryIndex < coordinates.length(); boundaryIndex++) {
                Polygon polygon = boundaryIndex == 0 ? returnObj : new Polygon();
                JSONArray boundary = coordinates.getJSONArray(boundaryIndex);
                for (int coordIndex = 0; coordIndex < boundary.length(); coordIndex++) {
                    polygon.add(Coordinate.fromGeoJson(boundary.get(coordIndex).toString()));
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
    public static List<Polygon> fromGeoJsonMultiPolygon(@NonNull String geojson) {
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
    public static List<Polygon> fromGeoJsonMultiPolygon(@NonNull JSONObject geometry) {
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
    public static List<Polygon> fromGeoJsonMultiPolygon(@NonNull JSONArray coordinates) {
        try {
            List<Polygon> polygons = new ArrayList<>();
            for (int polygonIndex = 0; polygonIndex < coordinates.length(); polygonIndex++) {
                polygons.add(Polygon.fromGeoJson(coordinates.getJSONArray(polygonIndex)));
            }
            return polygons;
        } catch (JSONException error) {
            throw new RuntimeException(error);
        }
    }

    @NonNull
    public static JSONObject toGeoJson(List<Polygon> multiPolygon) {
        try {
            JSONObject geoJson = new JSONObject();
            geoJson.put("type", "MultiPolygon");
            JSONArray coordinate = new JSONArray();
            for (Polygon polygon : multiPolygon) {
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
    public boolean isCoordinateExist(Coordinate coord) {
        for (Coordinate point : boundary) {
            if (point.equals(coord))
                return true;
            for (Polygon hole : holes) {
                if (hole.isCoordinateExist(coord))
                    return true;
            }
        }
        return false;
    }

    public List<Coordinate> getBoundary() {
        return boundary;
    }

    public List<Polygon> getAllHoles() {
        return new ArrayList<>(holes);
    }

    /**
     * @return JsonObject in GeoJson's geometry format
     */
    public JSONObject toGeoJson() {
        try {
            JSONObject geoJson = new JSONObject();
            geoJson.put("type", "Polygon");
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
        options.addAll(Coordinate.toLatLngs(boundary));
        for (Polygon hold : holes) {
            options.addHole(Coordinate.toLatLngs(hold.boundary));
        }
        return options;
    }

    @NonNull
    private JSONArray toGeoJsonCoordinates() {
        JSONArray coordinate = new JSONArray();
        coordinate.put(boundaryToJson());
        for (Polygon hole : holes) {
            coordinate.put(hole.boundaryToJson());
        }
        return coordinate;
    }

    @NonNull
    private JSONArray boundaryToJson() {
        JSONArray jsonBoundary = new JSONArray();
        for (Coordinate point : boundary) {
            jsonBoundary.put(point.toGeoJson());
        }
        if (!boundary.get(0).equals(boundary.get(boundary.size() - 1)))
            jsonBoundary.put(boundary.get(0).toGeoJson());
        return jsonBoundary;
    }

    public void add(Coordinate coordinate) {
        if (boundary.size() > 0 && boundary.get(0).equals(coordinate))
            return; //ignore close position
        boundary.add(coordinate);
    }

    public void add(double lat, double lng) {
        add(new Coordinate(lat, lng));
    }

    public void addHoles(Polygon coordinates) {
        holes.add(coordinates);
    }

    public Polygon getHole(int holeIndex) {
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

    public Polygon getLastHole() {
        return holes.get(holes.size() - 1);
    }

    public boolean haveHole() {
        return getHolesCount() > 0;
    }

    @Nullable
    public Coordinate getLastCoordinate() {
        if (haveHole()) {
            return getLastHole().getLastCoordinate();
        }

        if (!isEmpty()) {
            Coordinate peek = boundary.get(boundary.size() - 1);
            return new Coordinate(peek.getLatitude(), peek.getLongitude());
        } else {
            return null;
        }
    }

    public Coordinate pop() {
        if (!boundary.isEmpty()) {
            return boundary.remove(boundary.size() - 1);
        }
        return null;
    }

    public void removeHole(Polygon hole) {
        holes.remove(hole);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Polygon polygon = (Polygon) o;
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
        for (Polygon hole : this.holes) {
            dest.writeValue(hole);
        }
    }

    public boolean replace(Coordinate oldCoord, Coordinate newCoord) {
        int coordPosition = boundary.indexOf(oldCoord);
        if (coordPosition > -1) {
            boundary.set(coordPosition, newCoord);
            return true;
        }
        for (Polygon hole : holes) {
            if (hole.replace(oldCoord, newCoord))
                return true;
        }
        return false;
    }
}
