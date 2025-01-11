package org.rokist.videolistplayer


import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.DocumentsContract
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.documentfile.provider.DocumentFile
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import org.rokist.videolistplayer.databinding.ActivityMainBinding
import org.rokist.videolistplayer.views.MainWindow
import java.lang.Thread.sleep



typealias OnResumeOrRestartListener = (resume: Boolean) -> Unit

class ResumeOrPauseList constructor(val context: Context) {
    val listeners = mutableListOf<OnResumeOrRestartListener>()
    fun addHandler(listener: OnResumeOrRestartListener) {
        listeners.add(listener)
    }

    fun removeHandler(listener: OnResumeOrRestartListener) {
        listeners.remove(listener)
    }
}


class MainActivity : AppCompatActivity() {

    private var mainWindow: MainWindow? = null
    lateinit var binding: ActivityMainBinding

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_BACK ->
                    return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private val useFullScreen = true;
    private val hideNavBar = useFullScreen && false // Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    private var globalIndex = 0
    override fun onResume() {
        super.onResume()

        globalIndex++;
        val thisSessionIndex = globalIndex;
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        Handler(Looper.myLooper()!!).postDelayed({
            if (globalIndex == thisSessionIndex) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }, 1000 * 60 * 30) // 30 min
    }

    var hasFocus = false
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Log.d("aaa", "focus: ${hasFocus}")
        this.hasFocus = hasFocus
        if (hasFocus) {
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onPause() {
        super.onPause()
//        for (listener in resumeOrPauseList.listeners) {
//            listener(false)
//        }

        sleep(3000)
        binding.videoView.start()
    }

    override fun onStart() {
        super.onStart()

        var d = this.getString(R.string.deggvalue)

    }

    var a = 23423


    override fun onNewIntent(intent: Intent?) {
        Log.d("aaa", "onNewIntent")
        super.onNewIntent(intent)
    }

    protected fun checkAndRequestRequiredPermissions(permission: String) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, so request it from user
            this.requestPermissions(arrayOf(permission), 123)
        }
    }

    var isLoad = false

    class HttpAccessor {
        fun a() {
            // 非同期処理
            "https://www.casareal.co.jp/".httpGet()
                .header()
                .response { request, response, result ->
                    when (result) {
                        is Result.Success -> {
                            println("非同期処理の結果：" + String(response.data))
                        }
                        is Result.Failure -> {
                            println("通信に失敗しました。")
                        }
                    }
                }
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (requestCode == 31 && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            resultData?.data?.also { uri ->

                val pickedDir = DocumentFile.fromTreeUri(this, uri)
                if (pickedDir != null) {
                    var a: Array<DocumentFile> = pickedDir.listFiles()
                    for (fil in a) {
                        //fil.stream
                        if (true == fil.uri.path?.endsWith(("mp4"))) {
                            binding.videoView.setVideoURI((fil.uri))
                            binding.videoView.start()
                            //binding.videoView.curre
                            break;
                        }

                    }
                }
                // Perform operations on the document using its URI.
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = true
        }



        binding = ActivityMainBinding.inflate(this.layoutInflater)

        setContentView(binding.root)

        val m = MediaPlayer()
        //m.setDataSource()
        //m.prepare()
        //m.start()
        //crossfade()
        binding.mainStage.addOnLayoutChangeListener { v, left, top, right, bottom, leftWas, topWas, rightWas, bottomWas ->
            val widthWas = rightWas - leftWas // Right exclusive, left inclusive
            if (v.width != widthWas) {
                // Width has changed
            }
            val heightWas = bottomWas - topWas // Bottom exclusive, top inclusive
            if (v.height != heightWas) {
                // Height has changed
            }
        }
        binding.button2.setOnClickListener({ a ->

            fun openDirectory(pickerInitialUri: Uri) {
                // Choose a directory using the system's file picker.
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                    // Optionally, specify a URI for the directory that should be opened in
                    // the system file picker when it loads.
                    putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
                }

                startActivityForResult(intent, 31

                )
            }
            openDirectory( Uri.parse(""));

        })



        val scr = binding.mainStage
        val observer = scr.viewTreeObserver
        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                scr.viewTreeObserver.removeOnGlobalLayoutListener(this)
                Handler(Looper.getMainLooper()).postDelayed({
                    this@MainActivity.mainWindow = MainWindow(this@MainActivity)
                }, 20)
            }
        }
        observer.addOnGlobalLayoutListener(listener)

        this.setStatusBarColor(Color.argb(55, 0, 0, 11))// Color.TRANSPARENT
        window.navigationBarColor = Color.argb(33, 0, 0, 11)

        //this.hideSystemUI()
        this.setLightStatusBar()
    }

    private lateinit var loadingView: View
    private var shortAnimationDuration: Int = 31101


    private fun setLightStatusBar() {
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
    }


    private fun hideSystemUI() {

        if (useFullScreen) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
        WindowInsetsControllerCompat(window, binding.framelayout).let { controller ->
            if (hideNavBar) {
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                controller.hide(WindowInsetsCompat.Type.navigationBars())
            }
        }

        ViewCompat
            .setOnApplyWindowInsetsListener(binding.framelayout) { view, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
                val navInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())

                /*
                val v = this.testA.topBottomMargin.value
                if (v != null) {
                    v.topMargin = insets.top
                    if (!hideNavBar) {
                        v.bottomMargin = navInsets.bottom
                    }
                    this.testA.topBottomMargin.postValue(v)
                }
                */
                // Log.d("aaa", "bottom = ${navInsets.bottom}")

                WindowInsetsControllerCompat(window, binding.framelayout).let { controller ->
                    if (hideNavBar) {
                        controller.systemBarsBehavior =
                            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                        controller.hide(WindowInsetsCompat.Type.navigationBars())
                    }
                }

                // Return CONSUMED if you don't want want the window insets to keep being
                // passed down to descendant views.
                WindowInsetsCompat.CONSUMED
            }
    }


    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window,
            binding.framelayout
        ).show(WindowInsetsCompat.Type.systemBars())
    }


    private fun AppCompatActivity.setStatusBarColor(color: Int) {
        window.statusBarColor = color
    }


    companion object {
        var abc: Int = 0

        // Used to load the 'native-lib' library on application startup.
        init {
            //System.loadLibrary("native-lib")
        }
    }
}

class LockObj {

}

fun getLockObject(): LockObj {

    return LockObj()
}

tailrec fun Context.activity(): MainActivity? = when (this) {
    is MainActivity -> this
    else -> (this as? ContextWrapper)?.baseContext?.activity()
}

fun Context.toActivity(): MainActivity {
    var context = this
    while (context is ContextWrapper) {
        if (context is MainActivity) {
            return context
        }
        context = context.baseContext
    }
    throw NullPointerException()
    //return null
}