/**
 * The main activity for the app. Activates
 * a Toolbar, NavigationDrawer and BottomNavigationView
 * @author Chris Loftus
 * @version 2
 */
package uk.ac.aber.dcs.cs31620.faa.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import uk.ac.aber.dcs.cs31620.faa.R
import uk.ac.aber.dcs.cs31620.faa.databinding.ActivityMainBinding
import uk.ac.aber.dcs.cs31620.faa.ui.cats.ToggleState


class MainActivity : AppCompatActivity(), ToggleState {

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawer: DrawerLayout
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        drawer = binding.drawerLayout
        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.nav_open_drawer,
            R.string.nav_close_drawer
        )

        drawer.addDrawerListener(toggle)
        toggle.syncState()

        // Needed to handle tapping the Up button in fragments
        toggle.toolbarNavigationClickListener = View.OnClickListener {
            onBackPressed()
        }

        val navView = binding.bottomNavView

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_cats
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Example of how to handle an incoming Intent that wants to display
        // the list of cats in the Cats Fragment
        intent?.let {
            if (it.action != null && it.action == getString(R.string.action_view_cats)) {
                if (it.data != null && it.data.toString() == getString(R.string.cats_uri)){
                    // Navigate to the cats tab
                    navController.navigate(R.id.navigation_cats)
                }
            }
        }
    }

    override fun setNavigationDrawerState(isEnabled: Boolean){
        if (isEnabled){
            toggle.isDrawerIndicatorEnabled = true
        } else {
            toggle.isDrawerIndicatorEnabled = false
            // There is a night and day version on this under res
            toggle.setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24)
        }
        toggle.syncState()
    }
}