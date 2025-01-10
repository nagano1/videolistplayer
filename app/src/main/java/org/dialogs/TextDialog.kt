package org.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.text.ClipboardManager
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import org.rokist.videolistplayer.R

class TextDialog(
    context: Context,
    title: String?,
    text: String
) :
    AlertDialog.Builder(context) {
    private val editText: EditText
    var _handler = Handler()

    //@SuppressLint("NewApi")
    internal inner class QuoteCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {}
    }

    private fun getSelectedText(editText: EditText): String {
        val start = editText.selectionStart
        val end = editText.selectionEnd
        val text = editText.text.toString()
        return if (text.length > 0 && start >= 0 && end > start) text
            .substring(start, end) else text
    }

    //@SuppressLint("NewApi")
    init {
        //this.setTitle(title)

        val inflater = LayoutInflater.from(context)
        val ll = inflater.inflate(R.layout.text_dialog, null) as LinearLayout
        editText = ll.findViewById<View>(R.id.textEditText) as EditText
        editText.setText(text)
        //editText.setTextIsSelectable(true);
        editText.setSelection(0, text.length)
        editText.post {
            editText.requestFocus()
            val imm = context.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            imm.showSoftInput(
                editText,
                InputMethodManager.SHOW_IMPLICIT
            )
            editText.selectAll()

            editText.customSelectionActionModeCallback = QuoteCallback();
        }
        val copyButton = ll.findViewById<View>(R.id.copyButton) as Button
        val shareButton = ll.findViewById<View>(R.id.shareButton) as Button
        val allSelectButton = ll.findViewById<View>(R.id.allSelectIn) as Button
        val searchButton = ll.findViewById<View>(R.id.searchButtonIn) as Button
        copyButton.setOnClickListener {
            val str = getSelectedText(editText)
            // @SuppressWarnings("deprecation")
            val Manager = context
                .getSystemService(Activity.CLIPBOARD_SERVICE) as ClipboardManager
            Manager.text = str
            Toast.makeText(context, "コピーしました。", Toast.LENGTH_SHORT).show()
        }
        searchButton.setOnClickListener {
            val str = getSelectedText(editText)
            val search = Intent(Intent.ACTION_WEB_SEARCH)
            search.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            search.putExtra(SearchManager.QUERY, str)
            context.startActivity(search)
        }
        allSelectButton.setOnClickListener {
            val text = editText.text.toString()
            val length = editText.length()
            if (length > 0) {
                editText.setSelection(0, length)
            }
        }
        shareButton.setOnClickListener {
            val str = getSelectedText(editText)
            val i = Intent(Intent.ACTION_SEND)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.type = "text/plain" // typeを指定して
            i.putExtra(Intent.EXTRA_TEXT, str) // TEXTも指定。メールとかを意識するならsubjectも可能
            try {
                context.startActivity(Intent.createChooser(i, null)) // 常に連携先を選択させる
            } catch (e: ActivityNotFoundException) {
                // handle error
            }
        }
        this.setView(ll)
        //this.setPositiveButton("閉じる", null)
    }
}