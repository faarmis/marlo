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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MarloCoord implements Parcelable {
    public static final Parcelable.Creator<MarloCoord> CREATOR =
            new Parcelable.Creator<MarloCoord>() {
                @Override
                public MarloCoord createFromParcel(Parcel source) {
                    return new MarloCoord(source);
                }

                @Override
                public MarloCoord[] newArray(int size) {
                    return new MarloCoord[size];
                }
            };
    private double latitude;
    private double longitude;

    public MarloCoord(LatLng latLng) {
        this(latLng.latitude, latLng.longitude);
    }

    public MarloCoord(double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public MarloCoord(MarloCoord coordinate) {
        this(coordinate.getLatitude(), coordinate.getLongitude());
    }

    //Parcelable
    private MarloCoord(Parcel in) {
        this(in.readDouble(), in.readDouble());
    }

    public static MarloCoord fromMarker(Marker marker) {
        LatLng position = marker.getPosition();
        return new MarloCoord(position.latitude, position.longitude);
    }

    public static MarloCoord fromGeoJson(String coordinate) {
        try {
            JSONArray array = new JSONArray(coordinate);
            return new MarloCoord(array.getDouble(1), array.getDouble(0));
        } catch (JSONException json) {
            throw new RuntimeException(json);
        }
    }

    public static List<LatLng> toLatLngs(Iterable<MarloCoord> coordinates) {
        List<LatLng> latLngList = new ArrayList<>();
        for (MarloCoord coord : coordinates) {
            latLngList.add(coord.toLatLng());
        }
        return latLngList;
    }

    public static List<MarloCoord> clones(List<MarloCoord> blueprint) {
        List<MarloCoord> coords = new ArrayList<>();
        for (MarloCoord coordinate : blueprint) {
            coords.add(new MarloCoord(coordinate));
        }
        return coords;
    }

    public double getLatitude() {
        return latitude;
    }

    private void setLatitude(double latitude) {
        if (latitude < -90f || latitude > 90f)
            throw new IllegalArgumentException("-90 <= Latitude <= 90, Your values is " + latitude);
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private void setLongitude(double longitude) {
        if (longitude < -180f || longitude > 180f)
            throw new IllegalArgumentException("-180 <= longitude <= 180, Your value is " + longitude);
        this.longitude = longitude;
    }

    public LatLng toLatLng() {
        return new LatLng(latitude, longitude);
    }

    public JSONArray toGeoJson() {
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(0, longitude);
            jsonArray.put(1, latitude);
            return jsonArray;
        } catch (JSONException json) {
            throw new RuntimeException(json);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarloCoord location = (MarloCoord) o;
        return Double.compare(location.latitude, latitude) == 0
                && Double.compare(location.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Location{"
                + "latitude=" + latitude
                + ", longitude=" + longitude
                + '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

}
