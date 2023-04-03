/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pg.eti.project.polishbanknotes

import android.os.*
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import pg.eti.project.polishbanknotes.accesability.Haptizer
import pg.eti.project.polishbanknotes.accesability.TalkBackSpeaker
import pg.eti.project.polishbanknotes.databinding.ActivityMainBinding
import pg.eti.project.polishbanknotes.fragments.CameraFragmentDirections
import pg.eti.project.polishbanknotes.fragments.SettingsFragmentDirections
import pg.eti.project.polishbanknotes.sensors.TorchManager

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding

    // TODO SCOPE?
    lateinit var talkBackSpeaker: TalkBackSpeaker
    lateinit var haptizer: Haptizer
    lateinit var torchManager: TorchManager
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Main inflation.
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        val viewMain = activityMainBinding.root
        setContentView(viewMain)

        // Accessibility features initialization.
        talkBackSpeaker = TalkBackSpeaker(this)
        haptizer = Haptizer(this)

        // Sensors initialization.
        torchManager = TorchManager(this)

        // Setting toolbar.
        toolbar = activityMainBinding.toolbar
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.activity_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // Going to the settings by nav graph.
            R.id.action_settings -> {
                Navigation.findNavController(this, R.id.fragment_container)
                    .navigate(CameraFragmentDirections.actionCameraFragmentToSettingsFragment())
                true
            }
            R.id.action_done -> {
                Navigation.findNavController(this, R.id.fragment_container)
                    .navigate(SettingsFragmentDirections.actionSettingsFragmentToCameraFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            // Workaround for Android Q memory leak issue in IRequestFinishCallback$Stub.
            // (https://issuetracker.google.com/issues/139738913)
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()

        torchManager.unregisterSensorListener()
    }

    override fun onResume() {
        super.onResume()

        torchManager.registerSensorListener()
    }

    override fun onDestroy() {
        // TextToSpeech service must be stopped before closing the app.
        talkBackSpeaker.stop()

        // Stopping the haptizer service.
        haptizer.stop()

        super.onDestroy()
    }
}