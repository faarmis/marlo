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

import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
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
        viewFinder.setId(R.id.view_finder);
        viewFinder.setBackgroundColor(Color.TRANSPARENT);
        viewFinder.setScaleType(ImageView.ScaleType.FIT_XY);
        viewFinder.setOnClickListener(fragment);
        return viewFinder;
    }

    public static void addUndoButton(MarloFragment fragment) {
        Button undo = new Button(fragment.getContext());
        undo.setId(R.id.undo);
        undo.setText("Undo");
        undo.setOnClickListener(fragment);

        ViewGroup rootView = (ViewGroup) fragment.getView();

        if (rootView != null) {
            LayoutParams params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            int horizonMargin = fragment.getResources().getDimensionPixelOffset(R.dimen.screen_horizontal_margin);
            int verticalMargin = fragment.getResources().getDimensionPixelOffset(R.dimen.screen_vertical_margin);
            params.setMargins(horizonMargin, verticalMargin, 0, 0);
            rootView.addView(undo, params);
        }
    }

    public static void addHoleButton(MarloFragment fragment) {
        Button undo = new Button(fragment.getContext());
        undo.setId(R.id.hole);
        undo.setText("Hole");
        undo.setOnClickListener(fragment);

        ViewGroup rootView = (ViewGroup) fragment.getView();

        if (rootView != null) {
            LayoutParams params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            int horizonMargin = fragment.getResources().getDimensionPixelOffset(R.dimen.screen_horizontal_margin);
            int verticalMargin = fragment.getResources().getDimensionPixelOffset(R.dimen.screen_vertical_margin) * 3;

            params.setMargins(horizonMargin, verticalMargin, 0, 0);
            rootView.addView(undo, params);
        }
    }

    public static void addNewPolygonButton(MarloFragment fragment) {
        Button undo = new Button(fragment.getContext());
        undo.setId(R.id.new_polygon);
        undo.setText("New");
        undo.setOnClickListener(fragment);

        ViewGroup rootView = (ViewGroup) fragment.getView();

        if (rootView != null) {
            LayoutParams params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            int horizonMargin = fragment.getResources().getDimensionPixelOffset(R.dimen.screen_horizontal_margin);
            int verticalMargin = fragment.getResources().getDimensionPixelOffset(R.dimen.screen_vertical_margin) * 6;

            params.setMargins(horizonMargin, verticalMargin, 0, 0);
            rootView.addView(undo, params);
        }
    }

}
