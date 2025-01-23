package org.rokist.videolistplayer

import android.content.Context
import android.content.SharedPreferences
import java.util.ArrayList

class FolderListLoader(context:Context)
{
    private val _sharedPreferences: SharedPreferences

    init
    {
        _sharedPreferences = context.getSharedPreferences("folder_list", Context.MODE_PRIVATE)
        val list = _sharedPreferences.getStringSet("folderList", null)
        if (list != null) {
            for (a: String in list) {

            }
        }
    }

    private val wfe = "folder_list"

    fun getFolderList(): ArrayList<VideoPlayFolderItem>?
    {
        val set = _sharedPreferences.getStringSet(wfe, null)
        if (set == null) {
            return null
        }
        return VideoPlayFolderItem.parseSavedDataList(set)
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

class VideoPlayFolderItem(s: String) {
    var folderPath: String = ""
    var currentFileName: String = ""
    var index: Int = 0
    var updateTime: Int = 0 // unix timestamp
    var sortTypeNum: Int = 0

    val Title = s

    fun outputSaveData() : String
    {
        return folderPath + "|" + currentFileName + "|" + index + "|" + updateTime + "|" + sortTypeNum
    }

    override fun toString(): String {
        return Title
    }

    companion object
    {
        fun parseSavedDataEntry(savedData: String) : VideoPlayFolderItem?
        {
            val splitted = savedData.split("|")
            if (splitted.size >= 5) {
                try {
                    val newItem = VideoPlayFolderItem("jfiowe")

                    newItem.folderPath = splitted[0]
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


        fun parseSavedDataList(strSet: Set<String>) : ArrayList<VideoPlayFolderItem>
        {
            val folderItemList = ArrayList<VideoPlayFolderItem>()
            for (k in strSet) {
                val folderItem = parseSavedDataEntry(k)
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
