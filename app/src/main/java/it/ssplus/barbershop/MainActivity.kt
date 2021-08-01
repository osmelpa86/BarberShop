package it.ssplus.barbershop

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import it.ssplus.barbershop.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.appBarMain.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        drawerLayout = binding.drawerLayout
        val navView: NavigationView =binding.navView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_client,
                R.id.nav_reservation,
                R.id.nav_service,
                R.id.nav_expense,
                R.id.nav_turn,
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val currentFragment: Int =navHostFragment.navController.currentDestination!!.id

            if (currentFragment != R.id.nav_home) {
                super.onBackPressed()
            } else {
                finishAndRemoveTask()
//                val inflater = this.layoutInflater
//                val convertView =
//                    inflater.inflate(R.layout.view_exit_sheet, null) as View
//
//                var exitBottomSheetDialog: BottomSheetDialog =
//                    BottomSheetDialog(this, R.style.BaseBottomSheetDialogExit)
//                exitBottomSheetDialog.setContentView(convertView)
//
//                var btExit = convertView.findViewById(R.id.btExit) as Button
//                btExit.setOnClickListener {
////                    finishAndRemoveTask()
//                    var intent = Intent(this@MainActivity, LoginActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }
//
//                var btCancelExit = convertView.findViewById(R.id.btCancelExit) as Button
//                btCancelExit.setOnClickListener {
//                    exitBottomSheetDialog.dismiss()
//                }
//
//                exitBottomSheetDialog.show()
            }
        }
    }
}