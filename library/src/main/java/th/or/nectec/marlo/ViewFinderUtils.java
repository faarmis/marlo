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

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;

class ViewFinderUtils {

    public static void addViewFinder(MarloFragment fragment) {
        ViewGroup rootView = (ViewGroup) fragment.getView();

        if (rootView != null) {
            int size = fragment.getResources().getDimensionPixelOffset(R.dimen.view_finder_size);
            LayoutParams layoutParams = new LayoutParams(size, size, Gravity.CENTER);
            rootView.addView(getViewFinder(fragment), layoutParams);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static ImageButton getViewFinder(MarloFragment fragment) {
        ImageButton viewFinder = new ImageButton(fragment.getContext());
        viewFinder.setId(R.id.view_finder);
        viewFinder.setBackgroundColor(Color.TRANSPARENT);
        viewFinder.setImageDrawable(ResourceUtils.from(fragment.getContext()).getDrawable(R.drawable.view_finder));
        viewFinder.setScaleType(ImageView.ScaleType.FIT_XY);
        viewFinder.setOnClickListener(fragment);
        return viewFinder;
    }

}
