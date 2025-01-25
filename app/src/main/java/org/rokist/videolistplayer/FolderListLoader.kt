package org.rokist.videolistplayer

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.File

class FolderListLoader(context:Context)
{
    private val _sharedPreferences: SharedPreferences
    private val wfe = "folder_list"

    private val _context: Context = context
    init
    {
        _sharedPreferences = context.getSharedPreferences("folder_list2", Context.MODE_PRIVATE)
    }


    fun getFolderList(): ArrayList<VideoPlayFolderItem>
    {
        val set = _sharedPreferences.getStringSet(wfe, null)
        if (set == null) {
            return ArrayList()
        }
        return VideoPlayFolderItem.parseSavedDataList(_context, set)
    }

    fun saveFolderList(list: ArrayList<VideoPlayFolderItem>?)
    {
        if (list == null) {
            return
        }

        val strSet = arrayListOf<String>()
        for (a: VideoPlayFolderItem in list) {
            strSet.add(a.outputSaveData())
        }

        val edit = _sharedPreferences.edit()
        edit?.putStringSet(wfe, strSet.toSet())
        edit?.apply()
    }
}

class VideoPlayFolderItem(folderUri: Uri)
{
    var currentFileName: String = ""
    var index: Int = 0
    var updateTime: Int = 0 // unix timestamp
    var sortTypeNum: Int = 0

    val FolderUri = folderUri
    val folderPath2 = folderUri.path ?: ""


    fun outputSaveData() : String
    {
        return folderPath2 + "|" + currentFileName + "|" + index + "|" + updateTime + "|" + sortTypeNum
    }

    override fun toString(): String
    {
        val aFile = File(folderPath2)
        return aFile.name
    }

    companion object
    {
        public
        fun newFolderEntryFromUri(uri: Uri) : VideoPlayFolderItem
        {
            val newItem = VideoPlayFolderItem(uri)
            try {
                newItem.currentFileName = ""
                newItem.updateTime = 0
                newItem.sortTypeNum = 0
            }
            catch(_: Exception) {}

            return newItem
        }

        fun parseSavedDataEntry(context: Context, savedData: String) : VideoPlayFolderItem?
        {
            val splitted = savedData.split("|")
            if (splitted.size >= 5) {
                try {
                    val path = splitted[0]

                    val folderUri = DocumentFile.fromTreeUri(context, Uri.parse(path))?.uri
                    if (folderUri == null) {
                        return null
                    }

                    val newItem = VideoPlayFolderItem(folderUri)
                    newItem.currentFileName = splitted[1]
                    newItem.index = splitted[2].toInt()
                    newItem.updateTime = splitted[3].toInt()
                    newItem.sortTypeNum = splitted[4].toInt()
                    return newItem
                }
                catch(_: Exception) {}
            }
            return null
        }


        fun parseSavedDataList(context: Context, strSet: Set<String>) : ArrayList<VideoPlayFolderItem>
        {
            val folderItemList = ArrayList<VideoPlayFolderItem>()
            for (k in strSet) {
                val folderItem = parseSavedDataEntry(context, k)
                if (folderItem != null) {
                    folderItemList.add(folderItem)
                }
            }

            folderItemList.sortWith(object: Comparator<VideoPlayFolderItem> {
                override fun compare(o1: VideoPlayFolderItem?, o2: VideoPlayFolderItem?): Int {
                    if (o1 == null && o2 == null) {
                        return 0
                    }

                    val indexA : Int = o1?.index ?: 0
                    val indexB = o2?.index ?: 0

                    return indexA - indexB
                }
            })

            reassignIndex(folderItemList)

            return folderItemList
        }

        private fun reassignIndex(folderItemList: ArrayList<VideoPlayFolderItem>)
        {
            // reassign index
            var idx = 0
            for (item in folderItemList) {
                item.index = idx++
            }
        }
    }
}
