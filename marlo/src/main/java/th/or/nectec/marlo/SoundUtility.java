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

import android.content.Context;
import android.media.MediaPlayer;
import androidx.annotation.RawRes;

final class SoundUtility {

    private SoundUtility() {}

    public static void play(Context context, @RawRes int mediaRawId) {
        MediaPlayer soundEffect = MediaPlayer.create(context, mediaRawId);
        if (soundEffect != null) { //WTF, It should never be null!
            soundEffect.start();
        }
    }
}
