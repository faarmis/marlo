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

package th.or.nectec.marlo.sample

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import th.or.nectec.marlo.PointMarloFragment

class MultiPointActivity : AppCompatActivity() {

    val marlo by lazy { supportFragmentManager.findFragmentById(R.id.map) as PointMarloFragment }

    @SuppressLint("MissingPermission") override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_point)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        tedPermission(Manifest.permission.ACCESS_COARSE_LOCATION) {
            listener {
                onGranted { marlo.enableMyLocationButton() }
                onDenied {
                    Toast.makeText(this@MultiPointActivity, "Permission Deneid", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        }.check()

        marlo.setStartAtCurrentLocation(true)
//        marlo.setStartLocation(LatLng(14.07716, 100.601103), 15.0f)
        marlo.setMaxPoint(5)
        marlo.setOnPointChange {
            Toast.makeText(this@MultiPointActivity, "${it.size}", Toast.LENGTH_SHORT).show()
        }

    }
}
