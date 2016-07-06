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
    private final List<List<Coordinate>> holes;

    public Polygon(List<Coordinate> boundary, List<List<Coordinate>> holes) {
        this.boundary = boundary;
        this.holes = holes;
    }

    protected Polygon(Parcel in) {
        this.boundary = in.createTypedArrayList(Coordinate.CREATOR);

        int holesCount = in.readInt();
        this.holes = new ArrayList<>();
        for (int hole = 0; hole < holesCount; hole++) {
            this.holes.add(in.createTypedArrayList(Coordinate.CREATOR));
        }
    }

    public static Polygon fromPolygonData(PolygonData polyData) {
        List<Coordinate> boundaryCoordinate = new ArrayList<>();
        Stack<Marker> boundary = polyData.getBoundary();
        for (Marker marker :
                boundary) {
            boundaryCoordinate.add(Coordinate.fromMarker(marker));
        }

        List<List<Coordinate>> holeCoordinate = new ArrayList<>();
        Stack<Stack<Marker>> holes = polyData.getHoles();
        for (Stack<Marker> hole : holes) {
            ArrayList<Coordinate> coHole = new ArrayList<>();
            for (Marker marker : hole) {
                coHole.add(Coordinate.fromMarker(marker));
            }
            holeCoordinate.add(coHole);
        }

        return new Polygon(boundaryCoordinate, holeCoordinate);
    }

    public List<Coordinate> getBoundary() {
        return boundary;
    }

    public List<Coordinate> getHole(int holeIndex) {
        return holes.get(holeIndex);
    }

    public List<List<Coordinate>> getAllHoles() {
        return holes;
    }

    public int getHolesCount() {
        return holes.size();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.boundary);

        dest.writeInt(this.holes.size());
        for (List<Coordinate> hole : this.holes) {
            dest.writeTypedList(hole);
        }
    }

}
