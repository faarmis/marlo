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
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.google.android.gms.maps.SupportMapFragment;

public class MarloFragment extends SupportMapFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addViewFinder();
    }

    private void addViewFinder() {
        ViewGroup rootView = (ViewGroup) getView();
        rootView.addView(getViewFinder(), getViewFinderLayoutParams());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private ImageButton getViewFinder() {
        ImageButton viewFinder = new ImageButton(getContext());
        viewFinder.setBackgroundColor(Color.TRANSPARENT);
        viewFinder.setImageDrawable(ResourceUtils.from(getContext()).getDrawable(R.drawable.view_finder));
        viewFinder.setScaleType(ImageView.ScaleType.FIT_XY);
        return viewFinder;
    }

    private FrameLayout.LayoutParams getViewFinderLayoutParams() {
        int size = getResources().getDimensionPixelOffset(R.dimen.view_finder_size);
        return new FrameLayout.LayoutParams(size, size, Gravity.CENTER);
    }
}
