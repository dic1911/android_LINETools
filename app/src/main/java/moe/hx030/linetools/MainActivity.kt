package moe.hx030.linetools

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import moe.hx030.linetools.Constants.Companion.LINE_DATABASE_NAME
import moe.hx030.linetools.Utils.Companion.cmdOutput
import moe.hx030.linetools.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    var dbExists = false
    lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_groups, R.id.nav_chats
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                Log.d("030-res", "OK")
                val source = contentResolver.openInputStream(it.data!!.data!!)
                val target = getDatabasePath("naver_line")
                target.delete()
                target.createNewFile()
                val outStream = target.outputStream()
                val buf = ByteArray(1024)
                while (source!!.read(buf) > 0) {
                    outStream.write(buf)
                }
                source.close()
                outStream.flush()
                outStream.close()
                if (Storage.loadDb(this)) {
                    val groups = Storage.listGroups()
                    Utils.showToast("db loaded, joined ${groups?.size} group chats")
                }
            } else {
                Log.d("030-res", "not ok? ${it.resultCode}")
            }
        }

        Utils.init(this)
        dbExists = Utils.databaseExists(LINE_DATABASE_NAME)
        Storage.prefs = getSharedPreferences(Constants.MAIN_PREFS, MODE_PRIVATE)
    }

    override fun onResume() {
        super.onResume()
        Log.d("030-db", "$dbExists $dataDir")
//        if (!Storage.prefs.getBoolean(Constants.PREFS_IS_LOADED, false)) {
        if (!dbExists) {
            showLoadDialogPopup()
        } else if (Storage.loadDb(this)){
            val groups = Storage.listGroups()
            Utils.showToast("db loaded, joined ${groups?.size} group chats")
        } else {
            Storage.deleteDb(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun showLoadDialogPopup() {
        val content = TextView(this)
        content.text = getString(R.string.info_load_db_desc)
        content.setPadding(Utils.dp(12))
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.info_load_db_title)
            .setView(content)

        builder.setPositiveButton(R.string.load_db_root) { _: DialogInterface, _: Int ->
            val s = Utils.executorSvc.submit(Utils.copyDB)
            s.get()
            Utils.showToast(String.format(getString(R.string.load_db_root_result), Utils.cmdExitCode, cmdOutput))
            if (Utils.cmdExitCode != 0) showLoadDialogPopup()

        }

        builder.setNeutralButton(R.string.load_db_non_root) { _: DialogInterface, _: Int ->
            Utils.showToast("Select naver_line...")
            readDatabase()
        }

        builder.show()
    }

    private fun readDatabase() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setType("*/*")
        launcher.launch(intent)
    }
}