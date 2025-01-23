package org.rokist.videolistplayer


import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.provider.DocumentsContract
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.documentfile.provider.DocumentFile
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import org.rokist.videolistplayer.databinding.ActivityMainBinding


typealias OnResumeOrRestartListener = (resume: Boolean) -> Unit

class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding

    override fun dispatchKeyEvent(event: KeyEvent): Boolean
    {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_BACK ->
                    return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private var _folderList = ArrayList<String>()
    private val useFullScreen = true
    private val hideNavBar = useFullScreen && false // Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    private var globalIndex = 0


    override fun onResume()
    {
        super.onResume()

        globalIndex++
        val thisSessionIndex = globalIndex
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        Handler(Looper.myLooper()!!).postDelayed({
            if (globalIndex == thisSessionIndex) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }, 1000 * 60 * 30) // 30 min
    }


    @Suppress("RedundantOverride")
    override fun onConfigurationChanged(newConfig: Configuration)
    {
        super.onConfigurationChanged(newConfig)
    }

    var position = 0
    var _currentVideUri: Uri? = null

    override fun onRestart()
    {
        super.onRestart()
        /*
        if (_currentVideUri == null) {
            loadVideoUrl()
        }
        val uri = _currentVideUri
        if (uri != null && uri.path != null) {
            startVideo(uri)
        }
         */
    }

    override fun onPause()
    {
        super.onPause()

        updatePositionToVariable()
        savePosition()
        binding.videoView.pause()
    }

    public override
    fun onStart()
    {
        super.onStart()
        startTimerForUpdatingCurrentPos()
    }

    override fun onNewIntent(intent: Intent?)
    {
        Log.d("aaa", "onNewIntent")
        super.onNewIntent(intent)
    }

    class HttpAccessor
    {
        fun a()
        {
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

    private lateinit var _prefManager: PrefManager


    private fun updatePositionToVariable()
    {
        if (binding.videoView.isPlaying) {
            val pos = binding.videoView.currentPosition
            if (pos > 0) {
                position = pos
            }
        }
    }

    private fun savePosition()
    {
        val uri = _currentVideUri
        if (uri != null) {
            _prefManager.putInt(getPositionKey(uri), position)
        }
    }

    private fun saveVideUri()
    {
        val uri = _currentVideUri
        if (uri != null) {
            val str = uri.toString()
            _prefManager.putString("url", str)
        }
    }

    private fun getPositionKey(uri: Uri): String
    {
        val pa =  uri.path!! + "_position"
        return pa
    }

    private fun loadVideoUrl()
    {
        val st = _prefManager.getStringPref("url", "")
        if (st != "") {
            try {
                _currentVideUri = DocumentFile.fromTreeUri(this, Uri.parse(st))!!.uri
            } catch(e: Exception) {

            }
        }
    }

    private fun startVideo(uri: Uri)
    {
        return
        val pos = _prefManager.getInt(getPositionKey(uri), 0)
        if (pos > 0) {
            position = pos
        }
        _currentVideUri = uri
        saveVideUri()

        binding.videoView.setVideoURI((uri))

        binding.videoView.start()
        if (position > 0) {
            binding.videoView.seekTo(position)
        }
        binding.videoView.setOnCompletionListener(object: OnCompletionListener {
            override fun onCompletion(mediaPlayer: MediaPlayer?)
            {

            }
        })

        /*
        Handler(Looper.myLooper()!!).postDelayed({
            if (globalIndex == thisSessionIndex) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }, 1000 * 60 * 30) // 30 min

         */
    }

    private var timer: CountDownTimer? = null
    private fun startTimerForUpdatingCurrentPos()
    {
        /*
        Handler(Looper.myLooper()!!).postDelayed({

            saveCurrentPos()
        }, 1000 * 60) // 30 min
        */

        if (timer != null) {
            timer?.cancel()
        }

        timer = object: CountDownTimer(10*60*1000, 5000) {
            override fun onTick(millisUntilFinished: Long)
            {
                updatePositionToVariable()
            }
            override fun onFinish()
            {

            }
        }
        timer?.start()
    }


    lateinit var _folderListLoader: FolderListLoader;
    override fun onCreate(savedInstanceState: Bundle?)
    {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = true
        }

        _prefManager = PrefManager(this)
        _folderListLoader = FolderListLoader(this)

        binding = ActivityMainBinding.inflate(this.layoutInflater)

        setContentView(binding.root)

        setupUIs()

        this.setStatusBarColor(Color.argb(55, 0, 0, 11))// Color.TRANSPARENT
        window.navigationBarColor = Color.argb(33, 0, 0, 11)

        //this.hideSystemUI()
        this.setLightStatusBar()

        if (_currentVideUri == null) {
            loadVideoUrl()
        }
        val uri = _currentVideUri
        if (uri != null && uri.path != null) {
            startVideo(uri)
        }
    }

    private fun setupUIs()
    {
        //binding.controllerLinearLayout.visibility = View.INVISIBLE
        val folderListView =  binding.folderList
        val list = ArrayList<String>()
        list.add("jfiowe")
        folderListView.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list)
        folderListView.onItemClickListener = object: AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.controllerLinearLayout.visibility = View.INVISIBLE

                val k = 3
            }
        }

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
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                    putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
                }

                videoFolderSelectingLauncher.launch(intent)
            }
            openDirectory(Uri.parse(""));
        })
    }

    private val videoFolderSelectingLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    { result: ActivityResult? ->
        if (result?.resultCode == Activity.RESULT_OK) {
            result.data?.also { intent ->
                contentResolver.takePersistableUriPermission(
                    intent.data!!,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                //val value = data.getIntExtra(SubActivity.KEY_VALUE, 0)
                //Toast.makeText(this, "$value", Toast.LENGTH_LONG).show()
                val pickedDir = DocumentFile.fromTreeUri(this, intent.data!!)
                if (pickedDir != null) {
                    val a: Array<DocumentFile> = pickedDir.listFiles()
                    for (fil in a) {
                        if (true == fil.uri.path?.endsWith(("mp4"))) {
                            startVideo(fil.uri)
                            break
                        }
                    }
                }
            }
        }
    }

    private fun setLightStatusBar()
    {
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
    }


    private fun hideSystemUI()
    {
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


    private fun showSystemUI()
    {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window,
            binding.framelayout
        ).show(WindowInsetsCompat.Type.systemBars())
    }


    private fun AppCompatActivity.setStatusBarColor(color: Int)
    {
        window.statusBarColor = color
    }

    companion object
    {
        var abc: Int = 0
    }
}

class LockObj
{

}

fun getLockObject(): LockObj {

    return LockObj()
}

tailrec fun Context.activity(): MainActivity? = when (this) {
    is MainActivity -> this
    else -> (this as? ContextWrapper)?.baseContext?.activity()
}

fun Context.toActivity(): MainActivity
{
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