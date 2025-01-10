package org.rokist.videolistplayer.tests

import org.junit.Test

import org.junit.Assert.*
import org.rokist.videolistplayer.models.FileExplorerItems
import org.rokist.videolistplayer.models.Folder

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private fun indentOffsetExponentialDecay(n: Int): Float {
        // relative
        val useLinear = false
        if (useLinear) {
            return n.toFloat()
        }

        // 1 -> 1.0
        // 2 -> 1.8
        // 3 -> 2.6
        // -1 -> -1.0
        // -2 -> -1.8

        val abs = Math.abs(n)
        var sum = 0F
        var salt = if (n < 0) -1F else 1F
        for (i in 0 until abs) {
            sum += salt
            salt *= 0.8F
        }

        return sum
    }


    @Test
    fun folderCountTest() {

        // expand before adding
        run {
            val items = FileExplorerItems()
            val folder = items.root
            folder.addFile("ok")
            folder.addFile("ok2")
            val lastItem = folder.addFolder("ok3")

            assertEquals(4, folder.getExpandedCount())

            assertEquals(0, folder.__childIndex)
            assertEquals(2, lastItem.__childIndex)
            folder.collapse()

            assertEquals(0, folder.__childIndex)
            assertEquals(2, lastItem.__childIndex)

            assertEquals(0, lastItem.depth)

            val n = 1
            val res = n * (Math.pow(Math.E, (-Math.abs(n) / 15.0)) / Math.pow(Math.E, -1 / 15.0))
            assertEquals(res.toFloat(), 1.0F)
            assertEquals(indentOffsetExponentialDecay(2), 1.8F)
            assertEquals(indentOffsetExponentialDecay(1), 1.0F)
            assertEquals(indentOffsetExponentialDecay(-1), -1.0F)
            assertEquals(indentOffsetExponentialDecay(0), 0.0F)
        }

        //
        run {
            val items = FileExplorerItems()

            val root = items.root
            root.addFile("ok")
            root.addFolder("ok2")
            root.addFile("ok3")

            assertEquals(4, root.getExpandedCount())
            root.collapse()
            assertEquals(1, root.getExpandedCount())
        }

        // removeItem : file
        run {
            val items = FileExplorerItems()
            val root = items.root

            root.addFile("ok")
            val file = root.addFile("ok2")


            assertEquals(3, root.getExpandedCount())
            assertEquals(2, items.allList.size)

            root.removeItem(file)

            assertEquals(2, root.getExpandedCount())
            assertEquals(1, items.allList.size)
        }

        // removeItem: folder
        run {
            val items = FileExplorerItems()
            val root = items.root

            root.addFile("ok")
            root.addFolder("ok2")
            val folder = root.addFolder("ok3")
            folder.addFile("fi")
            folder.collapse()

            assertEquals(4, root.getExpandedCount())
            assertEquals(3, items.allList.size)

            root.removeItem(folder)

            assertEquals(3, root.getExpandedCount())
            assertEquals(2, items.allList.size)
        }


        // nested folder
        run {
            val items = FileExplorerItems()

            val root = items.root//Folder("fawe", null, items)
            root.collapse()
            root.addFile("ok")
            root.addFolder("ok2")
            root.addFile("ok3")

            val folder2 = root.addFolder("fawe")
            folder2.addFile("ok")
            folder2.addFolder("ok2")
            folder2.addFile("ok3")
            root.expand()

            assertEquals(8, root.getExpandedCount())
        }

        // nested folder and collapse
        run {
            val items = FileExplorerItems()
            val root = items.root

            root.addFile("ok")
            root.addFolder("ok2")
            root.addFile("ok3")

            val folder2 = root.addFolder("fawe")
            folder2.collapse()

            folder2.addFile("ok")
            folder2.addFolder("ok2")
            val lastItem = folder2.addFile("ok3")

            assertEquals(5, root.getExpandedCount())
            folder2.expand()
            assertEquals(8, root.getExpandedCount())
            folder2.collapse()
            assertEquals(5, root.getExpandedCount())

            root.collapse()
            assertEquals(1, root.getExpandedCount())
            root.expand()
            folder2.expand()
            assertEquals(8, root.getExpandedCount())
        }
    }

    @Test
    fun testAbsoluteIndex() {
        // nested folder and collapse
        run {
            val items = FileExplorerItems()
            val root = items.root
            root.collapse()
            root.expand()
            root.collapse()
            root.expand()
            assertEquals(1, root.getExpandedCount())

            root.addFile("ok")
            root.addFolder("ok2")
            val itemA = root.addFile("ok3")


            run {
                val folder2 = root.addFolder("folder2")

                folder2.addFile("folder2: ok")
                folder2.addFolder("folder2: ok2")


                val folder3 = root.addFolder("folder3")

                folder3.addFile("wow0")
                val folder4 = folder3.addFolder("folder4")
                val lastItem = folder4.addFile("lastitem")


                for (child in items.allList) {
                    println("yes = ${child.name}")
                }
                println("-------------------")

                assertEquals(9, items.allList.indexOf(lastItem))
                folder2.collapse()
                assertEquals(7, items.allList.indexOf(lastItem))
                folder2.expand()
                assertEquals(9, items.allList.indexOf(lastItem))

                folder2.collapse()
                folder3.collapse()
                for (child in items.allList) {
                    println("yes = ${child.name}")
                }
                assertEquals(-1, items.allList.indexOf(folder4))
                assertEquals(-1, items.allList.indexOf(lastItem))

                assertEquals(5, items.count())
                assertEquals(folder2, items.returnItem(3))

                folder2.expand()
                folder4.expand() // folder3 is collapsed
                println("-------------------")


                assertEquals(-1, items.allList.indexOf(lastItem))
                folder3.expand()

                for (child in items.allList) {
                    println("yes = ${child.name}")
                }

                assertEquals(9, items.allList.indexOf(lastItem))
                assertEquals(2, lastItem.depth)

                assertEquals(11, root.getExpandedCount())
                assertEquals(10, items.allList.size)

                root.collapseDescendants()
                assertEquals(6, root.getExpandedCount())

                root.expandDescendants()
                assertEquals(10, items.allList.size)

                println("-------------------")

                for (child in items.allList) {
                    println("yes = ${child.name}")
                }

                assertEquals(11, root.getExpandedCount())

            }
        }
    }

    @Test
    fun empty_test() {
        val items = FileExplorerItems()
        val folder = Folder("fawe", null, items)

        assertEquals(0, folder.getExpandedCount() - 1)
    }
}