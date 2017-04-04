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

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

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

    public Polygon(List<Coordinate> boundary){
        this(boundary, new ArrayList<Polygon>());
    }

    public Polygon(List<Coordinate> boundary, List<Polygon> holes) {
        this.boundary = boundary;
        this.holes = holes;
    }

    protected Polygon(Parcel in) {
        this.boundary = in.createTypedArrayList(Coordinate.CREATOR);

        int holesCount = in.readInt();
        this.holes = new ArrayList<>();
        for (int hole = 0; hole < holesCount; hole++) {
            this.holes.add((Polygon) in.readValue(Polygon.class.getClassLoader()));
        }
    }

    public void add(Coordinate coordinate){
        boundary.add(coordinate);
    }

    public static Polygon fromPolygonData(PolygonData polyData) {
        List<Coordinate> boundaryCoordinate = new ArrayList<>();
        Stack<Marker> boundary = polyData.getBoundary();
        for (Marker marker :
                boundary) {
            boundaryCoordinate.add(Coordinate.fromMarker(marker));
        }

        List<Polygon> holesList = new ArrayList<>();
        Stack<Stack<Marker>> holes = polyData.getHoles();
        for (Stack<Marker> hole : holes) {
            List<Coordinate> holeBoundary = new ArrayList<>();
            for (Marker marker : hole) {
                holeBoundary.add(Coordinate.fromMarker(marker));
            }
            holesList.add(new Polygon(holeBoundary));
        }

        return new Polygon(boundaryCoordinate, holesList);
    }

    public void addBoundary(Coordinate coordinate) {
        boundary.add(coordinate);
    }

    public List<Coordinate> getBoundary() {
        return boundary;
    }

    public Polygon getHole(int holeIndex) {
        return holes.get(holeIndex);
    }

    public List<Polygon> getAllHoles() {
        return holes;
    }

    public boolean isValid() {
        return boundary.size() >= 3;
    }

    public boolean isEmpty(){
        return boundary.size() == 0;
    }

    public int getHolesCount() {
        return holes.size();
    }

    public void addHoles(Polygon coordinates) {
        holes.add(coordinates);
    }

    public Polygon getLastHole() {
        return holes.get(holes.size() - 1);
    }

    public boolean haveHole() {
        return getHolesCount() > 0;
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
}
