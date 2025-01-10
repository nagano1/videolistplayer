package org.rokist.videolistplayer.models


abstract class FileBase(
    private val path_: String,
    private val _parent: Folder?
) : HierarchyItem() {
    private var _name: String = ""
    private val _depth: Int
    var infoKey: Int = 0
    var __childIndex = 0 // pos in siblings, no expand considered

    override val depth: Int
        get() = _depth


    init {
        _depth = _parent?.depth?.plus(1) ?: -1

        if (this.path_ == "/") {
            _name = ""
        } else if (this.path_.endsWith('/')) {
            val s = this.path_.split('/')
            _name = s[s.size - 2]
        } else {
            val s = this.path_.split('/')
            _name = s[s.size - 1]
        }
    }

    val name: String
        get() = _name

    val path: String
        get() = path_

}

class Folder(
    path: String,
    private val _parent: Folder?,
    private val items: FileExplorerItems
) : FileBase(path, _parent) {

    private var _isExpanded = true
    private var _descendantsCount = 1 // self included
    private val _children = mutableListOf<FileBase>()

    fun getIsExpanded(): Boolean {
        return _isExpanded
    }

    fun getExpandedCount(): Int {
        return if (_isExpanded) {
            _descendantsCount
        } else {
            1
        }
    }

    private fun changeDescendantsCount(n: Int) {
        if (_isExpanded) {
            _descendantsCount += n
            _parent?.changeDescendantsCount(n)
        }
    }

    fun expand() {
        if (_isExpanded) {
            return
        }

        _isExpanded = true

        if (_children.size > 0) {
            var lastInsertedIndex = items.allList.indexOf(this)
            val firstIndex = lastInsertedIndex + 1
            val tempList = mutableListOf<FileBase>()
            if (_parent == null || lastInsertedIndex > -1) {
                for (child in _children) {
                    if (child is Folder) {
                        lastInsertedIndex = child.insertRecurse(lastInsertedIndex, tempList)
                    } else {
                        tempList.add(child)
                        lastInsertedIndex += 1
                    }
                }
                items.allList.addAll(firstIndex, tempList)
            }

            _descendantsCount = 1
            for (child in _children) {
                if (child is Folder) {
                    _descendantsCount += child.getExpandedCount()
                } else {
                    _descendantsCount += 1
                }
            }
            _parent?.changeDescendantsCount(_descendantsCount - 1)
        }
    }

    /*
     *  Let's use indexOfList as a salt to avoid Stack overflow
     */
    private fun insertRecurse(indexOfList: Int, list: MutableList<FileBase>): Int {
        var lastInsertedIndex = indexOfList + 1
        list.add(this)

        if (_isExpanded) {
            for (child in _children) {
                if (child is Folder) {
                    lastInsertedIndex = child.insertRecurse(lastInsertedIndex, list)
                } else {
                    list.add(child)
                    lastInsertedIndex += 1
                }
            }
        }

        return lastInsertedIndex
    }

    fun collapse() {
        if (!_isExpanded) {
            return
        }

        _isExpanded = false

        if (_children.size > 0) {
            val indexOfList = items.allList.indexOf(this)
            if (_parent == null || indexOfList > -1) {
                items.allList.subList(indexOfList + 1, indexOfList + _descendantsCount).clear()
            }

            _parent?.changeDescendantsCount(1 - _descendantsCount)
            _descendantsCount = 1
        }
    }


    fun addFile(name: String): File {
        val newFile = File("$path$name", this)
        this.addItem(newFile)
        return newFile
    }


    fun addFolder(name: String): Folder {
        val newFolder = Folder("$path$name/", this, this.items)
        this.addItem(newFolder)
        return newFolder
    }

    fun removeItem(fileBase: FileBase) {
        _children.remove(fileBase)

        if (_isExpanded) {
            val deleteCount = if (fileBase is Folder) {
                fileBase.getExpandedCount()
            } else {
                1
            }
            val indexOfFile = items.allList.indexOf(fileBase)
            if (indexOfFile > -1) {
                items.allList.subList(indexOfFile, indexOfFile + deleteCount).clear()
            }

            this.changeDescendantsCount(-deleteCount)
        }
    }


    private fun addItem(fileBase: FileBase) {
        _children.add(fileBase)
        fileBase.__childIndex = _children.size - 1

        items.lastInfoKey += 1
        fileBase.infoKey = items.lastInfoKey
        items.indexInfoMap[fileBase.infoKey] = HierarchyItemIndexInfo()


        if (_isExpanded) {
            val idx = items.allList.indexOf(this)
            if (_parent == null || idx > -1) {
                items.allList.add(idx + _descendantsCount, fileBase)
            }
            this.changeDescendantsCount(1)
        }
    }


    fun expandDescendants() {
        expandOrCollapseDescendants(this, true)
    }

    fun collapseDescendants() {
        expandOrCollapseDescendants(this, false)
    }

    fun expandChildren(expandOr: Boolean) {
        for (child in this.childFiles) {
            if (child is Folder) {
                if (expandOr) {
                    child.expand()
                } else {
                    child.collapse()
                }
            }
        }
    }


    private fun expandOrCollapseDescendants(folder: Folder, expandOr: Boolean) {
        for (child in folder.childFiles) {
            if (child is Folder) {
                if (expandOr) {
                    child.expand()
                } else {
                    child.collapse()
                }
                expandOrCollapseDescendants(child, expandOr)
            }
        }
    }


    val childFiles: List<FileBase> get() = _children
}

class File(path: String, parent: Folder?) : FileBase(path, parent) {

}

class FileExplorerItems : HierarchyItems {
    val root: Folder
    val allList: MutableList<FileBase> = ArrayList(10000)
    val indexInfoMap = mutableMapOf<Int, HierarchyItemIndexInfo>()
    var lastInfoKey: Int = 0

    init {
        val file = File("/ajfowei/fjioaiewf", null)
        this.root = Folder("/", null, this)
    }

    fun addRandom() {
        root.collapse()
        addRandomChildren(root, 0, 0)
        root.expand()

//        root.collapseDescendants()
        root.expandChildren(false)

        showChildren(root)
    }

    override fun returnItem(i: Int): HierarchyItem {
        return allList[i]
    }

    override fun count(): Int {
        return allList.size
    }

    override fun getIndexInfo(item: HierarchyItem): HierarchyItemIndexInfo? {
        return indexInfoMap[(item as FileBase).infoKey]
    }

    override fun prepareForItemsModification() {
        //
        // assume that indexInfoMap contains all files and folders
        //
        for ((idx, child) in allList.withIndex()) {
            indexInfoMap[child.infoKey]?.also {
                it.beforeIndex = idx
                it.done = true
            }
        }

        for ((_, v) in indexInfoMap) {
            if (!v.done) {
                v.beforeIndex = -1
            } else {
                v.done = false // keep it false always
            }
        }
    }

    override fun prepareForDeleteItems() {
        //
        // assume that indexInfoMap contains all files and folders
        //
        for ((idx, child) in allList.withIndex()) {
            indexInfoMap[child.infoKey]?.also {
                it.afterIndex = idx
                it.done = true
            }
        }

        for ((_, v) in indexInfoMap) {
            if (!v.done) {
                v.afterIndex = -1
            } else {
                v.done = false // keep it false always
            }
        }
    }

    fun randomStr(): String {
        val alphaNumericString =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz"

        // create StringBuffer size of AlphaNumericString
        val sb = StringBuilder()
        val max = (Math.random() * 30).toInt() + 5
        for (i in 0.rangeTo(max)) {
            // generate a random number between
            // 0 to AlphaNumericString variable length
            val index = (alphaNumericString.length * Math.random()).toInt()
            // add Character one by one in end of sb
            sb.append(alphaNumericString[index])
        }

        return sb.toString()
    }


    fun addRandomChildren(folder: Folder, depth: Int, currentEntry: Int): Int {
        val max = 50000
        var currentEntry2 = currentEntry
        if (currentEntry2 > max) {
            return currentEntry2
        }

        for (i in 3.rangeTo(3 + (Math.random() * 13).toInt())) {
            currentEntry2 += 1
            if (currentEntry2 > max) {
                return currentEntry2
            }

            if (depth < 17 && Math.random() < 0.4) {
                val newFolder = folder.addFolder(randomStr())
                currentEntry2 = addRandomChildren(newFolder, depth + 1, currentEntry2 + 1)
            } else {
                currentEntry2 += 1
                folder.addFile(randomStr())
            }
        }

        return currentEntry2
    }


    fun showChildren(folder: Folder) {
        for (child in folder.childFiles) {
            //Log.d("aaa", "${child.depth}:${child.path}")
            if (child is Folder) {
                showChildren(child as Folder)
            }
        }
    }
}