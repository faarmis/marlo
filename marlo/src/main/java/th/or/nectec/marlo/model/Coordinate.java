/*
 * Copyright (c) 2016 NECTEC
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

public class Coordinate implements Parcelable {
    private double latitude;
    private double longitude;

    public Coordinate(double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public Coordinate(LatLng latLng) {
        this(latLng.latitude, latLng.longitude);
    }

    public static Coordinate fromMarker(Marker marker) {
        LatLng position = marker.getPosition();
        return new Coordinate(position.latitude, position.longitude);
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

    public LatLng toLatLng(){ return new LatLng(latitude, longitude); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate location = (Coordinate) o;
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

    //Parcelable
    private Coordinate(Parcel in) {
        this(in.readDouble(), in.readDouble());
    }

    public static final Parcelable.Creator<Coordinate> CREATOR = new Parcelable.Creator<Coordinate>() {
        @Override
        public Coordinate createFromParcel(Parcel source) {
            return new Coordinate(source);
        }

        @Override
        public Coordinate[] newArray(int size) {
            return new Coordinate[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    public JSONArray toGeoJson(){
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(0, longitude);
            jsonArray.put(1, latitude);
            return jsonArray;
        }catch (JSONException json){
            throw new RuntimeException(json);
        }
    }
}
