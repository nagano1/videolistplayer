package org.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.github.kittinunf.fuel.httpPost
import org.rokist.videolistplayer.R
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

import com.github.kittinunf.result.Result
import kotlinx.coroutines.*
import kotlin.concurrent.thread

/*
class LocalWebView : WebView {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        privateBrowsing: Boolean
    ) : super(context, attrs, defStyleAttr, privateBrowsing) {
    }

    override fun onCheckIsTextEditor(): Boolean {
        return true
    }
}
*/


class GithubAuthDialog(
    var context: Context,
    text: String
) {

    private val webview: WebView
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
        thread {
            loadUrlToWebview()
        }
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        return suspendCoroutineWithTimeout (1_000_000) {
            getCouroutine = it
        }
        /*
        return suspendCoroutine { coroutine ->
            getCouroutine = coroutine
        }
        */
    }



    init {
        //this.dialog?.setTitle("title")
        this.dialog = Dialog(context, android.R.style.Theme_Material_Dialog_NoActionBar)

        val inflater = LayoutInflater.from(context)
        val ll = inflater.inflate(R.layout.github_auth_webview_dialog, null) as LinearLayout
        webview = ll.findViewById<View>(R.id.github_webview) as WebView



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

    private fun loadUrlToWebview() {
/*
        // If you don't specify the GitHub user id then the sdk will retrieve it via /user endpoint
        val github = GitHubBuilder()
            .withOAuthToken("f4b6fee761ecda5d126fe0bd2820dbf94caf87a3")
            .build();
        /*
        val repo = github.createRepository(
            "newwow", "this is my new2 repository",
            "https://www.test2.org/", true /*public*/
        )
         */

        for ((key, repo) in github.myself.repositories) {
            Log.d("aaa", "" + repo.forksCount);
            Log.d("aaa", "" + github.myself.email);
            Log.d("aaa", "" + github.myself.login);
            //val builder = repo.createTree()
            //builder.create()
// git clone https://${GIT_USER}:${GIT_TOKEN}@github.com/${GIT_REPOSITORY}
// git clone https://miru32:f4b6fee761ecda5d126fe0bd2820dbf94caf87a3@github.com/new-repository
        }
 */


        val appClientId = "6f00db964d197b735d2d"
        val clientSecretKey = "6296759d3cbe91b1a1b6d5ee549fd2bdb3e9cc1d"
        val callback_url = Uri.encode("https://okcom.180r.com/callback_r.php");


        val alphaNumericString =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz"

        // create StringBuffer size of AlphaNumericString
        val sb = StringBuilder(20)
        for (i in 0..19) {
            // generate a random number between
            // 0 to AlphaNumericString variable length
            val index = (alphaNumericString.length * Math.random()).toInt()
            // add Character one by one in end of sb
            sb.append(alphaNumericString[index])
        }


        val scopemap = mutableMapOf<String, String>()
        scopemap["(no scope)"] = "Grants read-only access to public information (including user profile info, repository info, and gists)";
        scopemap["repo"] = "Grants full access to repositories, including private repositories. That includes read/write access to code, commit statuses, repository and organization projects, invitations, collaborators, adding team memberships, deployment statuses, and repository webhooks for repositories and organizations. Also grants ability to manage user projects."
        scopemap["repo:status"] = "Grants read/write access to commit statuses in public and private repositories. This scope is only necessary to grant other users or services access to private repository commit statuses without granting access to the code."
        scopemap["repo_deployment"] = "Grants access to deployment statuses for public and private repositories. This scope is only necessary to grant other users or services access to deployment statuses, without granting access to the code."
        scopemap["public_repo"] = "Limits access to public repositories. That includes read/write access to code, commit statuses, repository projects, collaborators, and deployment statuses for public repositories and organizations. Also required for starring public repositories."
        scopemap["repo:invite"] = "Grants accept/decline abilities for invitations to collaborate on a repository. This scope is only necessary to grant other users or services access to invites without granting access to the code."
        scopemap["security_events"] = "Grants: read and write access to security events in the code scanning API read and write access to security events in the secret scanning API This scope is only necessary to grant other users or services access to security events without granting access to the code."
        scopemap["admin:repo_hook"] = "Grants read, write, ping, and delete access to repository hooks in public or private repositories. The repo and public_repo scopes grant full access to repositories, including repository hooks. Use the admin:repo_hook scope to limit access to only repository hooks."
        scopemap["write:repo_hook"] = "Grants read, write, and ping access to hooks in public or private repositories."
        scopemap["read:repo_hook"] = "Grants read and ping access to hooks in public or private repositories."
        scopemap["admin:org"] = "Fully manage the organization and its teams, projects, and memberships."
        scopemap["write:org"] = "Read and write access to organization membership, organization projects, and team membership."
        scopemap["read:org"] = "Read-only access to organization membership, organization projects, and team membership."
        scopemap["admin:public_key"] = "Fully manage public keys."
        scopemap["write:public_key"] = "Create, list, and view details for public keys."
        scopemap["read:public_key"] = "List and view details for public keys."
        scopemap["admin:org_hook"] = "Grants read, write, ping, and delete access to organization hooks. Note: OAuth tokens will only be able to perform these actions on organization hooks which were created by the OAuth App. Personal access tokens will only be able to perform these actions on organization hooks created by a user."
        scopemap["gist"] = "Grants write access to gists."
        scopemap["notifications"] = "Grants: read access to a user's notifications mark as read access to threads watch and unwatch access to a repository, and read, write, and delete access to thread subscriptions."
        scopemap["user"] = "Grants read/write access to profile info only. Note that this scope includes user:email and user:follow."
        scopemap["read:user"] = "Grants access to read a user's profile data."
        scopemap["user:email"] = "Grants read access to a user's email addresses."
        scopemap["user:follow"] = "Grants access to follow or unfollow other users."
        scopemap["delete_repo"] = "Grants access to delete adminable repositories."
        scopemap["write:discussion"] = "Allows read and write access for team discussions."
        scopemap["read:discussion"] = "Allows read access for team discussions."
        scopemap["write:packages"] = "Grants access to upload or publish a package in GitHub Packages. For more information, see \"Publishing a package\"."
        scopemap["read:packages"] = "Grants access to download or install packages from GitHub Packages. For more information, see \"Installing a package\"."
        scopemap["delete:packages"] = "Grants access to delete packages from GitHub Packages. For more information, see \"Deleting and restoring a package.\""
        scopemap["admin:gpg_key"] = "Fully manage GPG keys."
        scopemap["write:gpg_key"] = "Create, list, and view details for GPG keys."
        scopemap["read:gpg_key"] = "List and view details for GPG keys."
        scopemap["codespace"] = "Grants the ability to create and manage codespaces. Codespaces can expose a GITHUB_TOKEN which may have a different set of scopes. For more information, see \"Security in Codespaces.\""
        scopemap["workflow"] = "Grants the ability to add and update GitHub Actions workflow files. Workflow files can be committed without this scope if the same file (with both the same path and contents) exists on another branch in the same repository. Workflow files can expose GITHUB_TOKEN which may have a different set of scopes. For more information, see \"Authentication in a workflow.\""



        var reqScope = ""
        for (entry in scopemap.keys) {
            reqScope += "$entry%20"
        }

        //val WEBSERVER_PORT = 3737
        val state = sb.toString() // unguessable random temporary string for use in API
        val login: String? = null;
        val doRequest =
            ("https://github.com/login/oauth/authorize?"
                    + "client_id=${appClientId}&"
                    + "redirect_uri=${callback_url}&response_type=code&"
                    + (if (login != null) "login=" + login.replace("@", "%40")
                    + "&" else "login=tset&")
                    + "allow_signup=true&"
                    + "state=${state}&scope=repo")//${reqScope}")//repo%20user")


        GlobalScope.launch(Dispatchers.Main) {

        val settings: WebSettings = webview.settings
        settings.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")

        //settings.setUserAgentString("Mozilla/5.0 (Android; Mobile; rv:21.0) Gecko/21.0 Firefox/21.0");

        //_contentGroup =
        settings.javaScriptEnabled = true;
        webview.setWebViewClient(object : WebViewClient() {
            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                println("onPageStarted:$url")
                val uri = Uri.parse(url)
                if (uri.host!!.endsWith(".youtube.com")) {
                    if (uri.path!!.contains("watch")) {
                        val vParam = uri.getQueryParameter("v")
                        if (vParam != null && vParam.length > 5) {
                            val videoId: String = vParam

                        }
                    }
                }
            }

            override fun onPageStarted(
                view: WebView?,
                url: String?,
                favicon: Bitmap?
            ) {
                //String str = "https://m.youtube.com/watch?v=BWXyDAXRY9o&itct=CCYQpDAYACITCOySwZCE4sgCFUUiWAodVXgC0zIGcmVsbWZ1SPTD8_CQw7Tf8gE%3D";
                super.onPageStarted(view, url, favicon)
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                println("onPageFinished: $url")

                if (url != null && url.contains("okcom.180r.com/callback_r.php")) {
                    val code = Uri.parse(url).getQueryParameter("code")
                    val state = Uri.parse(url).getQueryParameter("state")

                    code.let {
                        /*
    client_id 	string 	Required. The client ID you received from GitHub for your GitHub App.
    client_secret 	string 	Required. The client secret you received from GitHub for your GitHub App.
    code 	string 	Required. The code you received as a response to Step 1.
    redirect_uri 	string 	The URL in your application where users are sent after authorization.
    state 	string 	The unguessable random string you provided in Step 1.
                         */
                        val appClientId = "6f00db964d197b735d2d"
                        val clientSecretKey = "6296759d3cbe91b1a1b6d5ee549fd2bdb3e9cc1d"

                        "https://github.com/login/oauth/access_token".httpPost(
                            listOf(
                                "client_id" to appClientId,
                                "client_secret" to clientSecretKey,
                                "code" to code,
                                "state" to state
                            )
                        )
                            .response { request, response, result ->
                                when (result) {
                                    is Result.Success -> {
                                        val uri = Uri.parse(
                                            "https://test.com?${String(response.data)}"
                                        )

                                        val para = uri.getQueryParameter("access_token")
                                        val wwf = uri.getQueryParameter("token_type")
                                        val kk = uri.getQueryParameter("scope")

                                        getCouroutine?.resume(para)
                                        this@GithubAuthDialog.dialog?.dismiss()
                                    }
                                    is Result.Failure -> {
                                        getCouroutine?.resumeWithException(result.getException())
                                        println("通信に失敗しました。")
                                    }
                                }

                            }

                    }


                }

                // view.loadUrl("javascript:window.activity.viewSource(document.documentElement.outerHTML);");
                var script =
                    "javascript:plel=document.getElementById('player');if(plel)plel.parentNode.removeChild(plel);"
                //                script += "plel=document.getElementById('koya_elem_0_9');if(plel)plel.parentNode.removeChild(plel);";
    //                script += "plel=document.getElementById('koya_elem_0_8');if(plel)plel.parentNode.removeChild(plel);";
    //                script += "plel=document.getElementById('koya_elem_0_7');if(plel)plel.parentNode.removeChild(plel);";
    //                script += "plel=document.getElementById('koya_elem_0_6');if(plel)plel.parentNode.removeChild(plel);";

    //                script += "plel=document.getElementById('koya_elem_0_5');if(plel)plel.parentNode.removeChild(plel);";
    //                script += "plel=document.getElementById('koya_elem_0_4');if(plel)plel.parentNode.removeChild(plel);";
    //                script += "plel=document.getElementById('koya_elem_0_3');if(plel)plel.parentNode.removeChild(plel);";
    //                script += "plel=document.getElementById('koya_elem_0_2');if(plel)plel.parentNode.removeChild(plel);";

    //                script += "plel=document.getElementById('koya_elem_0_1');if(plel)plel.parentNode.removeChild(plel);";
    //                script += "plel=document.getElementById('koya_elem_0_10');if(plel)plel.parentNode.removeChild(plel);";
    //                script += "plel=document.getElementById('koya_elem_0_11');if(plel)plel.parentNode.removeChild(plel);";
                script += "plel=document.getElementById('koya_elem_0_12');if(plel)plel.parentNode.removeChild(plel);"
                script += "plel=document.getElementById('koya_elem_0_13');if(plel)plel.parentNode.removeChild(plel);"
                script += "plel=document.getElementById('koya_elem_0_14');if(plel)plel.parentNode.removeChild(plel);"
                script += "plel=document.getElementById('koya_child_5');if(plel)plel.scrollIntoView(true);"
                //script += "if(plel==null)plel=document.getElementById('koya_child_5');if(plel)plel.scrollIntoView(true);";
                val script2 = script
                // view.loadUrl(script2);
            } //            @Override
            //            public void onPageFinished(WebView view, String url) {
            //                super.onPageFinished(view, url);
            //
            //                //mParent.setProgressBarIndeterminateVisibility(false);
            //                String script = "javascript:document.forms['trackingForm'].inputDenpyo.value='abc';";
            //                view.loadUrl(script);
            //            }
        })
        Log.d("aaa", "jfoiwe: " + doRequest);
            if (doRequest != null) {
                webview.loadUrl(doRequest);
            }
        }
    }
}