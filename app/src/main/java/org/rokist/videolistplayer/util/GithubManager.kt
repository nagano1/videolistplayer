package org.rokist.videolistplayer.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.dialogs.GithubAuthDialog
import kotlin.concurrent.thread



class GithubManager constructor(private val context: Context) {

    fun startGithubAuth() {


        GlobalScope.launch(Dispatchers.Main) {
            val builder = GithubAuthDialog(
                this@GithubManager.context, "wow" + "\n" + "url"
            )

            builder.show().let {
                when (it) {
                    null -> Log.d("aaa", "null")
                    else -> {
                        thread {
                            /*
                            appDb.githubAccessTokenDao().deleteAll()
                            appDb.githubAccessTokenDao().insertAll(
                                GithubAccessToken(0, it, "wow")
                            )
                            Log.d(
                                "aaa",
                                "" + appDb.githubAccessTokenDao().getAll().map { it.accessToken })

                             */
                        }
                        null;
                    }
                }
            }
        }
/*
            if (dl != null) {
                //int height = ViewGroup.LayoutParams.MATCH_PARENT;
                val width = ViewGroup.LayoutParams.MATCH_PARENT
                val display: Display = windowManager.defaultDisplay
                val size = Point()
                display.getSize(size)
                //int width = (int)(size.x * 0.96);
                //dl.getWindow()?.setLayout(width, ((size.y * 0.96).toInt()))
            }
 */
        Handler(Looper.getMainLooper()).post {

        }

        //github.createOrGetAuth()
        //val github = GitHub.connect()
/*
        val repo = github.createRepository(
            "new-repository", "this is my new repository",
            "https://www.test.org/", true /*public*/
        )

 */
//        Log.d("aaa", "+" + repo.description);
        //f4b6fee761ecda5d126fe0bd2820dbf94caf87a3
        //repo.addCollaborators(github.getUser("abayer"), github.getUser("rtyler"))
        //repo.delete()

    }
}