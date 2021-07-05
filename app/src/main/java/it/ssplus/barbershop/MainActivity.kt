package it.ssplus.barbershop

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

//        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_client,
                R.id.nav_service,
                R.id.nav_expense,
                R.id.nav_turn,
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment)
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return NavigationUI.navigateUp(navController, drawerLayout)
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