package org.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.coroutines.Continuation

import kotlinx.coroutines.*
import org.rokist.videolistplayer.R

class EmptyDialog(
    var context: Context
) {

    //var _handler = Handler(Looper.getMainLooper())

    private fun getSelectedText(editText: EditText): String {
        val start = editText.selectionStart
        val end = editText.selectionEnd
        val text = editText.text.toString()
        return if (text.length > 0 && start >= 0 && end > start)
            text.substring(start, end)
        else text
    }

    var dialog: Dialog? = null

    suspend inline fun <T> suspendCoroutineWithTimeout(
        timeout: Long,
        crossinline block: (Continuation<T>) -> Unit
    ) : T? {
        var finalValue : T? = null
        withTimeoutOrNull(timeout) {
            finalValue = suspendCancellableCoroutine(block = block)
        }
        return finalValue
    }


    var getCouroutine: Continuation<String?>? = null
    suspend fun show(): String? {
        dialog?.show()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        return suspendCoroutineWithTimeout (1_000_000) {
            getCouroutine = it
        }
    }

    init {
        //this.dialog?.setTitle("title")
        this.dialog = Dialog(context, android.R.style.Theme_Material_Dialog_NoActionBar_MinWidth)
        this.dialog?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val inflater = LayoutInflater.from(context)
        val ll = inflater.inflate(R.layout.empty_dialog, null) as ConstraintLayout


        val copyButton = ll.findViewById<View>(R.id.copyButton) as Button
        val shareButton = ll.findViewById<View>(R.id.shareButton) as Button
        val allSelectButton = ll.findViewById<View>(R.id.allSelectIn) as Button
        val searchButton = ll.findViewById<View>(R.id.searchButtonIn) as Button
        copyButton.setOnClickListener {

            Toast.makeText(context, "コピーしました。", Toast.LENGTH_SHORT).show()
        }
        searchButton.setOnClickListener {

        }
        allSelectButton.setOnClickListener {

        }
        shareButton.setOnClickListener {
        }

        this.dialog?.setContentView(ll)
        //this.setPositiveButton("閉じる", null)
    }
}