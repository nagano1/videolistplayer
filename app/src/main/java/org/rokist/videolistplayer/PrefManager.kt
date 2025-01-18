package org.rokist.videolistplayer

import android.content.Context
import android.content.SharedPreferences
import java.util.*

class PrefManager constructor(private var context:Context){
    var _listeners: ArrayList<OnPrefChangedListener>? = ArrayList()

    fun addOnPrefChangedListener(listener: OnPrefChangedListener) {
        _listeners!!.add(listener)
    }

    fun notifyPrefChanged() {
        if (_listeners != null) {
            for (listener in _listeners!!) {
                listener.onPrefChanged()
            }
        }
    }

    private var _sharedPreferences: SharedPreferences

    fun getStringPref(key: String, defaultStr: String): String {
        return _sharedPreferences.getString(key, defaultStr) ?: defaultStr
    }

    /**
     * 設定変更状態を格納します。 true: changed; false: unchanged
     */
    private val _prefChangeStatus = HashMap<String, Boolean?>()

    //最適化のための値保持機構を用いる設定値たち
    var useNotifyResReplay = false
    var eliminateBEIDOnList //スレ一覧でのBEIDの除去
            = false
    var currentLiminateBEID = false
    var checkMyResAuto = false

    /**
     * すべての変更あり状態にします。
     */
    fun resetAsPrefChanged() {
        for (entry in _prefChangeStatus.entries) {
            entry.setValue(true)
        }
    }

    /**
     * 指定したキーの設定値が変更した可能性があることを示します。
     */
    fun isPrefChanged(key: String): Boolean {
        return if (_prefChangeStatus.containsKey(key)) {
            val changed = _prefChangeStatus[key]
            _prefChangeStatus[key] = false
            changed!!
        } else {
            _prefChangeStatus[key] = false
            true
        }
    }

    //
    val scrollFriction: Float
        get() {
            var friction = getFloatPref("scrollfriction", 16f)
            if (friction < 1f) {
                friction = 1f
            }
            if (friction > 100) {
                friction = 100f //
            }
            return (16 / (friction * friction * friction))
        }


    /**
     * 文字列で設定されている値からFloatに変換して返します。
     */
    fun getFloatPref(key: String?, defaultFloat: Float): Float {
        return try {
            val str = _sharedPreferences.getString(key, null) ?: return defaultFloat
            str.toFloat()
        } catch (ee: ClassCastException) {
            defaultFloat
        } catch (ex: NumberFormatException) {
            defaultFloat
        }
    }

    fun getIntPref(key: String?, defaultInt: Int): Int {
        return try {
            val str = _sharedPreferences.getString(key, null) ?: return defaultInt
            str.toInt()
        } catch (ee: ClassCastException) {
            defaultInt
        } catch (a: NumberFormatException) {
            defaultInt
        }
    }

    fun getInt(key: String, int: Int): Int {
        return _sharedPreferences.getInt(key, 0)
    }

    fun putInt(key: String?, intVal: Int) {
        val edit = _sharedPreferences.edit()
        edit?.putInt(key, intVal)
        edit?.apply()
    }

    fun putString(key: String?, str: String) {
        val edit = _sharedPreferences.edit()
        edit?.putString(key, str)
        edit?.apply()
    }

    fun putStringSet(key: String?, str: Set<String>) {
        val edit = _sharedPreferences.edit()
        edit?.putStringSet(key, str)
        edit?.apply()
    }

    fun getStringSet(key: String): MutableSet<String>? {
        return _sharedPreferences.getStringSet(key, null)
    }



    fun getBooleanPref(string: String?, b: Boolean): Boolean {
        return _sharedPreferences.getBoolean(string, b)
    }

    interface OnPrefChangedListener {
        fun onPrefChanged()
    }

    init {
        _sharedPreferences =
            context.getSharedPreferences("pref_manager", Context.MODE_PRIVATE)
        useNotifyResReplay = _sharedPreferences.getBoolean("use_notify_res_reply", true)
        eliminateBEIDOnList = _sharedPreferences.getBoolean("eliminateBEIDOnList", true)
        currentLiminateBEID = eliminateBEIDOnList
        checkMyResAuto = _sharedPreferences.getBoolean("auto_check_myres", true)
    }
}
