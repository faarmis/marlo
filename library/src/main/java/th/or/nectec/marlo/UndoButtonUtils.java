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

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

class UndoButtonUtils {

    public static void add(MarloFragment fragment){
        Button undo = new Button(fragment.getContext());
        undo.setId(R.id.undo);
        undo.setText("Undo");
        undo.setOnClickListener(fragment);

        ViewGroup rootView = (ViewGroup) fragment.getView();

        if(rootView != null) {
            LayoutParams params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            int horizonMargin = fragment.getResources().getDimensionPixelOffset(R.dimen.screen_horizontal_margin);
            int verticalMargin = fragment.getResources().getDimensionPixelOffset(R.dimen.screen_vertical_margin);
            params.setMargins(horizonMargin, verticalMargin, 0, 0);
            rootView.addView(undo, params);
        }
    }
}
