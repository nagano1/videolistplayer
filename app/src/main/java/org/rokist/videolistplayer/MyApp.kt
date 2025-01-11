package org.rokist.videolistplayer

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.net.NetworkInterface
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Arrays
import java.util.Collections


class MyApp : Application() {

    private var isMyDevice: Boolean = false

    init {
        //Instance = this
        // setDirs();

        // _databaseHelper = new DatabaseHelper(this.getContext());
    }

    override fun onCreate() {
        super.onCreate()
        isMyDevice = this.getString(R.string.deggvalue) == "AJOPFVOIOFIWE_";
    }

    /*
         val myAndroidDeviceId =
            Settings.Secure.getString(
                applicationContext.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        Log.d("aaa", "print = ${myAndroidDeviceId}")
        if (myAndroidDeviceId == "35662aea7f6f50e9") {
        }
        // 35662aea7f6f50e9 for  SH-M08
        // 53dda112bc6a6ee4 for SH-M15

     */
}

class DeviceInfo constructor(private val context:Context) {

        var APP_ROOT_DIR_NAME = "DoorLang"
        private var _datFolder: File? = null
        private var _rootFolder: File? = null
        private var _imageFolder: File? = null
        private var _themeFolder: File? = null
        private var _fontFolder: File? = null
        private var _saveImageFolder: File? = null
        private var _externalCookieFolder: File? = null
        var isMyDevice = false
            private set
        var _handler: Handler? = null
        private var _postNames: ArrayList<String>? = null
        private var _postMails: ArrayList<String>? = null


        val rootFolder: File?
            get() {
                if (_rootFolder == null) setDirs()
                return _rootFolder
            }

        val datFolder: File?
            get() {
                if (_datFolder == null) setDirs()
                return _datFolder
            }

        val themeFolder: File?
            get() {
                if (_themeFolder == null) {
                    setDirs()
                }
                return _themeFolder
            }

        val fontFolder: File?
            get() {
                if (_fontFolder == null) {
                    setDirs()
                }
                return _fontFolder
            }

        val imageFolder: File?
            get() {
                if (_imageFolder == null) setDirs()
                return _imageFolder
            }

        val externalCookieFolder: File?
            get() {
                if (_externalCookieFolder == null) {
                    setDirs()
                }
                return _externalCookieFolder
            }

        @SuppressLint("NewApi")
        fun getDeviceInfo(withSerial: Boolean): String {
            var version = ""
            try {
                var packageInfo: PackageInfo? = null
                packageInfo = context.packageManager
                    .getPackageInfo(context.packageName, 0)
                if (packageInfo != null) {
                    version = packageInfo.versionName
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            var serial = ""
            if (withSerial && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                serial = "-" + Build.SERIAL
            }
            return (Build.MANUFACTURER + "/"
                    + Build.MODEL + serial + "/" + Build.VERSION.RELEASE + "/2chBox " + version)
        }

        /**
         * 保存時用画像フォルダを取得します。
         */
        /**
         * 保存時用画像フォルダを取得します。
         */
        val saveImageFolder: File?
            get() {
                if (_saveImageFolder == null) setDirs()
                return _saveImageFolder
            }

        private fun setDirs() {
            val storageDir = Environment.getExternalStorageDirectory()
            if (storageDir != null && storageDir.isDirectory) {
                val files = storageDir.listFiles()
                if (files != null) {
                    for (candidate in storageDir.listFiles()) {
                        if (candidate.name == APP_ROOT_DIR_NAME) {
                            _rootFolder = candidate
                        }
                    }
                }
                if (_rootFolder == null) {
                    _rootFolder = File(
                        storageDir.absolutePath + "/"
                                + APP_ROOT_DIR_NAME
                    )
                    if (!_rootFolder!!.mkdir()) {
                        // Toast.makeText(App.getContext(),
                        // "SDカードに" + APP_ROOT_DIR_NAME + "フォルダを作成できませんでした。",
                        // 500).show();
                        _rootFolder = null
                    } else {
                        // _handler.post(new Runnable() {
                        // @Override
                        // public void run() {
                        // Toast.makeText(
                        // App.getContext(),
                        // "SDカードに" + APP_ROOT_DIR_NAME
                        // + "フォルダを作成しました。", Toast.LENGTH_LONG)
                        // .show();
                        // }
                        // });
                    }
                }
            }
            // datフォルダの取得/初期化
            if (_rootFolder != null) {
                for (file in _rootFolder!!.listFiles()) {
                    if (file.isDirectory && file.name == "dat") {
                        _datFolder = file
                    }
                }
                if (_datFolder == null) {
                    _datFolder = File(
                        _rootFolder!!.absolutePath + "/"
                                + "dat"
                    )
                    if (!_datFolder!!.mkdir()) {
                        _datFolder = null
                    }
                }
            }

            //fontフォルダの取得
            if (_rootFolder != null) {
                for (file in _rootFolder!!.listFiles()) {
                    if (file.isDirectory && file.name == "font") {
                        _fontFolder = file
                    }
                }
                if (_fontFolder == null) {
                    _fontFolder =
                        File(_rootFolder!!.absolutePath + "/" + "font")
                    if (!_fontFolder!!.mkdir()) {
                        _fontFolder = null
                    }
                }
            }

            // imageフォルダの取得
            if (_rootFolder != null) {
                for (file in _rootFolder!!.listFiles()) {
                    if (file.isDirectory && file.name == "image") {
                        _imageFolder = file
                    }
                }
                if (_imageFolder == null) {
                    _imageFolder = File(
                        _rootFolder!!.absolutePath + "/"
                                + "image"
                    )
                    if (!_imageFolder!!.mkdir()) {
                        _imageFolder = null
                    }
                }
            }
            // imageフォルダの取得
            if (_rootFolder != null) {
                for (file in _rootFolder!!.listFiles()) {
                    if (file.isDirectory && file.name == "theme") {
                        _themeFolder = file
                    }
                }
                if (_themeFolder == null) {
                    _themeFolder = File(
                        _rootFolder!!.absolutePath + "/"
                                + "theme"
                    )
                    if (!_themeFolder!!.mkdir()) {
                        _themeFolder = null
                    }
                }
            }


            // 外部クッキー保存場所の取得
            if (_rootFolder != null) {
                for (file in _rootFolder!!.listFiles()) {
                    if (file.isDirectory && file.name == "cookie") {
                        _externalCookieFolder = file
                    }
                }
                if (_externalCookieFolder == null) {
                    _externalCookieFolder = File(
                        _rootFolder!!.absolutePath
                                + "/" + "cookie"
                    )
                    if (!_externalCookieFolder!!.mkdir()) {
                        _externalCookieFolder = null
                    }
                }
            }

            // 保存画像フォルダの生成
            if (_rootFolder != null) {
                for (file in _rootFolder!!.listFiles()) {
                    if (file.isDirectory && file.name == "SavedImages") {
                        _saveImageFolder = file
                    }
                }
                if (_saveImageFolder == null) {
                    _saveImageFolder = File(
                        _rootFolder!!.absolutePath + "/"
                                + "SavedImages"
                    )
                    if (!_saveImageFolder!!.mkdir()) {
                        _saveImageFolder = null
                    }
                }

                // ユーザー指定
//                if (prefManager.getBooleanPref("change_save_directory", false)) {
//                    val saveDirePath: String = prefManager.getStringPref(
//                        "image_save_directory",
//                        "/mnt/sdcard/2chBox/SavedImages/"
//                    )
//                    if (saveDirePath != null
//                        && (saveDirePath
//                                == "/mnt/sdcard/2chBox/SavedImages/") == false
//                    ) {
//                        _saveImageFolder = File(saveDirePath)
//                        if (_saveImageFolder!!.exists() == false) {
//                        }
//                    }
//                }
            }
        }

        fun savePostNameHistory() {
            val postNames = postNameHistory
            val sh = context.getSharedPreferences(
                "postNames",
                Context.MODE_PRIVATE
            )
            val editor = sh.edit()
            editor.clear()
            var i = 0
            for (str in postNames!!) {
                editor.putString("item$i", str)
                i++
            }
            editor.commit()
        }

        fun savePostMailHistory() {
            val postMails = postMailHistory
            val sh = context.getSharedPreferences(
                "postMails",
                Context.MODE_PRIVATE
            )
            val editor = sh.edit()
            editor.clear()
            var i = 0
            for (str in postMails!!) {
                editor.putString("item$i", str)
                i++
            }
            editor.commit()
        }

        val postNameHistory: ArrayList<String>?
            get() {
                if (_postNames == null) {
                    _postNames = ArrayList()
                    val sh = context.getSharedPreferences(
                        "postNames", Context.MODE_PRIVATE
                    )
                    val all = sh.all
                    val size = all.size
                    for (i in 0 until size) {
                        val a = sh.getString("item$i", null)
                        if (a != null) {
                            _postNames!!.add(a)
                        }
                    }
                    if (_postNames!!.size == 0) {
                        _postNames!!.add("!ninja")
                        _postNames!!.add("")
                    }
                }
                return _postNames
            }

        val postMailHistory: ArrayList<String>?
            get() {
                if (_postMails == null) {
                    _postMails = ArrayList()
                    val sh = context.getSharedPreferences(
                        "postMails", Context.MODE_PRIVATE
                    )
                    val all = sh.all
                    val size = all.size
                    for (i in 0 until size) {
                        val a = sh.getString("item$i", null)
                        if (a != null) {
                            _postMails!!.add(a)
                        }
                    }
                }
                return _postMails
            }

        fun addToNameHistory(expression: String?) {
            if (expression == null) return
            //Log.d("add name ", expression);
            val expHistory = postNameHistory
            val it = expHistory!!.iterator()
            var count = 0
            while (it.hasNext()) {
                val exp = it.next()
                if (expression == exp) {
                    it.remove()
                } else if (count++ > 5) {
                    it.remove()
                }
            }
            expHistory.add(0, expression)
        }

        fun addToMailHistory(expression: String?) {
            if (expression == null) return
            //Log.d("add mail", expression);
            val expHistory = postMailHistory
            val it = expHistory!!.iterator()
            var count = 0
            while (it.hasNext()) {
                val exp = it.next()
                if (expression == exp) {
                    it.remove()
                } else if (count++ > 5) {
                    it.remove()
                }
            }
            expHistory.add(0, expression)
        }

        fun canUsePropertyAnimation(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
        }
    }

