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

package th.or.nectec.marlo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

final class ViewUtils {

    private ViewUtils() {}

    public static View addViewFinder(MarloFragment fragment) {
        return addViewFinder(fragment, R.drawable.view_finder);
    }

    public static View addViewFinder(MarloFragment fragment, @DrawableRes int viewFinderDrawableId) {
        ViewGroup rootView = (ViewGroup) fragment.getView();
        if (rootView != null) {
            ImageButton viewFinder = new ImageButton(fragment.getContext());
            viewFinder.setId(R.id.marlo_view_finder);
            viewFinder.setBackgroundColor(Color.TRANSPARENT);
            viewFinder.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            viewFinder.setOnClickListener(fragment);
            viewFinder.setImageResource(viewFinderDrawableId);
            int size = fragment.getResources().getDimensionPixelOffset(R.dimen.marlo_view_finder_size);
            LayoutParams layoutParams = new LayoutParams(size, size, Gravity.CENTER);
            rootView.addView(viewFinder, layoutParams);
            return viewFinder;
        }
        return null;
    }


    @SuppressLint("InflateParams")
    public static void addPolygonToolsMenu(MarloFragment fragment) {
        View tools = fragment.getActivity().getLayoutInflater().inflate(R.layout.multi_polygon_tool, null);
        tools.findViewById(R.id.marlo_undo).setOnClickListener(fragment);
        tools.findViewById(R.id.marlo_mark).setOnClickListener(fragment);
        tools.findViewById(R.id.marlo_hole).setOnClickListener(fragment);
        tools.findViewById(R.id.marlo_boundary).setOnClickListener(fragment);

        ViewGroup rootView = (ViewGroup) fragment.getView();
        if (rootView != null) {
            LayoutParams params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.BOTTOM | Gravity.END);
            rootView.addView(tools, params);
        }
    }

    public static void setPolygonToolsMenuVisibility(MarloFragment fragment, int visibility) {
        View view = fragment.getView();
        view.findViewById(R.id.marlo_undo).setVisibility(visibility);
        view.findViewById(R.id.marlo_mark).setVisibility(visibility);
        view.findViewById(R.id.marlo_hole).setVisibility(visibility);
        view.findViewById(R.id.marlo_boundary).setVisibility(visibility);
    }

    public static View addMyLocationButton(MarloFragment fragment) {
        FloatingActionButton fab = new FloatingActionButton(fragment.getContext());
        fab.setSize(FloatingActionButton.SIZE_MINI);
        fab.setId(R.id.marlo_gps);
        fab.setImageResource(R.drawable.ic_gps_fixed_white_24dp);
        fab.setOnClickListener(fragment);
        fab.setVisibility(View.GONE);

        ViewGroup rootView = (ViewGroup) fragment.getView();
        if (rootView != null) {
            LayoutParams params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.END);
            int horizonMargin = fragment.getResources().getDimensionPixelOffset(R.dimen.marlo_screen_horizontal_margin);
            int verticalMargin = fragment.getResources().getDimensionPixelOffset(R.dimen.marlo_screen_vertical_margin);
            params.setMargins(0, verticalMargin, horizonMargin, 0);
            rootView.addView(fab, params);
        }
        return fab;
    }

    public static CompoundButton addMapTypeButton(MarloFragment fragment) {
        ToggleButton toggleButton = new ToggleButton(fragment.getContext());
        toggleButton.setId(R.id.marlo_map_toggle);
        toggleButton.setBackgroundResource(R.drawable.selector_toggle_button);
        toggleButton.setTextOff("");
        toggleButton.setTextOn("");
        toggleButton.setChecked(true);
        toggleButton.setOnCheckedChangeListener(fragment.onMapTypeButtonChange);

        ViewGroup rootView = (ViewGroup) fragment.getView();
        if (rootView != null) {
            int px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 60,
                    fragment.getResources().getDisplayMetrics());
            LayoutParams params = new LayoutParams(px, (int) (px * 0.6), Gravity.START | Gravity.BOTTOM);
            int horizonMargin = fragment.getResources()
                    .getDimensionPixelOffset(R.dimen.marlo_screen_horizontal_margin);
            int verticalMargin = fragment.getResources()
                    .getDimensionPixelOffset(R.dimen.marlo_over_google_margin);
            params.setMargins(horizonMargin, 0, 0, verticalMargin);
            rootView.addView(toggleButton, params);
            return toggleButton;
        }
        return null;
    }

}
