package com.playlistmaker.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {
    private var _binding: ActivityRootBinding? = null
    private val binding: ActivityRootBinding get() = requireNotNull(_binding) { "Binding wasn't initialized" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.playerFragment, R.id.newPlaylistFragment -> {
                    bottomNavigationView.visibility = View.GONE
                    binding.divider.visibility = View.GONE
                }

                else -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    binding.divider.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}


