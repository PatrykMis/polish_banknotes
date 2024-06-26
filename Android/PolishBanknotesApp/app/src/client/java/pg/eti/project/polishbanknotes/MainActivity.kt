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

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import pg.eti.project.polishbanknotes.databinding.ActivityMainBinding
import pg.eti.project.polishbanknotes.fragments.CameraFragment
import pg.eti.project.polishbanknotes.fragments.CameraFragmentDirections
import pg.eti.project.polishbanknotes.fragments.SettingsFragmentDirections


class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Main inflation.
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        val viewMain = activityMainBinding.root
        setContentView(viewMain)


        // Setting toolbar.
        toolbar = activityMainBinding.toolbar
        setSupportActionBar(toolbar)
//        setupActionBarWithNavController(findNavController(R.id.fragment_container))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.activity_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Catch not loading sth.
        try {
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
        } catch (e: IllegalArgumentException) {
//            Log.e("CRASH", "$e")
            return false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment_container)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun isCameraFragment(): Boolean {
        // Get the NavHostFragment associated with the NavHost
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment

        // Get the NavController from the NavHostFragment
        val navController = navHostFragment.navController

        // Get the current destination ID
        val currentDestinationId = navController.currentDestination?.id

        // Check the current destination ID to determine the active fragment
        if (currentDestinationId == R.id.camera_fragment)
            return true

        return false
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            // Workaround for Android Q memory leak issue in IRequestFinishCallback$Stub.
            // (https://issuetracker.google.com/issues/139738913)
            finishAfterTransition()
        }

        // When CameraFragment then close app.
        if (isCameraFragment())
            finish()

        super.onBackPressed()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) AudioManager.ADJUST_RAISE
                else AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI
            )
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
