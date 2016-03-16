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

package th.or.nectec.marlo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

final class ViewUtils {

    private ViewUtils() {
    }

    public static void addViewFinder(MarloFragment fragment) {
        addViewFinder(fragment, R.drawable.view_finder);
    }

    public static void addViewFinder(MarloFragment fragment, @DrawableRes int viewFinderDrawableId) {
        ViewGroup rootView = (ViewGroup) fragment.getView();
        if (rootView != null) {
            ImageButton viewFinder = getViewFinder(fragment);
            viewFinder.setImageDrawable(ResourceUtils.from(fragment.getContext()).getDrawable(viewFinderDrawableId));
            int size = fragment.getResources().getDimensionPixelOffset(R.dimen.view_finder_size);
            LayoutParams layoutParams = new LayoutParams(size, size, Gravity.CENTER);
            rootView.addView(viewFinder, layoutParams);
        }
    }

    private static ImageButton getViewFinder(MarloFragment fragment) {
        ImageButton viewFinder = new ImageButton(fragment.getContext());
        viewFinder.setId(R.id.marlo_view_finder);
        viewFinder.setBackgroundColor(Color.TRANSPARENT);
        viewFinder.setScaleType(ImageView.ScaleType.FIT_XY);
        viewFinder.setOnClickListener(fragment);
        return viewFinder;
    }

    @SuppressLint("InflateParams")
    public static void addPolygonToolsMenu(MarloFragment fragment) {
        View tools = fragment.getLayoutInflater(null).inflate(R.layout.multi_polygon_tool, null);
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

    public static void addGpsLocationButton(MarloFragment fragment) {
        FloatingActionButton fab = new FloatingActionButton(fragment.getContext());
        fab.setId(R.id.marlo_gps);
        fab.setImageResource(R.drawable.ic_gps);
        fab.setOnClickListener(fragment);

        ViewGroup rootView = (ViewGroup) fragment.getView();
        if (rootView != null) {
            LayoutParams params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.END);
            int horizonMargin = fragment.getResources().getDimensionPixelOffset(R.dimen.screen_horizontal_margin);
            int verticalMargin = fragment.getResources().getDimensionPixelOffset(R.dimen.screen_vertical_margin);
            params.setMargins(0, verticalMargin, horizonMargin, 0);
            rootView.addView(fab, params);
        }
    }

}
