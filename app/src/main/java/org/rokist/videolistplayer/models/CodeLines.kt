package org.rokist.videolistplayer.models

import android.content.Context
import org.rokist.videolistplayer.toActivity


abstract class HierarchyItem {
    abstract val depth: Int
}

class HierarchyItemIndexInfo {
    var done: Boolean = false
    var beforeIndex:  Int = 0
    var afterIndex:  Int = 0
}

interface HierarchyItems {
    fun returnItem(i:Int) : HierarchyItem
    fun count() : Int
    fun getIndexInfo(item: HierarchyItem) : HierarchyItemIndexInfo?
    fun prepareForItemsModification()
    fun prepareForDeleteItems()

}


class CodeLines(doc: CodeDocument) : HierarchyItems {
    var nativeCodeDocument: CodeDocument? = null
    val lines = ArrayList<Line>()

    val indexInfoMap = mutableMapOf<HierarchyItem, HierarchyItemIndexInfo>()


    private val lineCount = doc.lineNums// 8000//viewSettings.lines
    init {

        var targetLine = doc.firstLine
        this.nativeCodeDocument = doc;

        for (i in 0..lineCount) {
            if (targetLine == null) {
                break;
            }
            //val line = Line()

/*
            var indentText = ""
            indentText += " Dum = ${itemView.idx + 1}"
            for (i in 0..6) {
                indentText += " Dum = ${i + 1}"
            }
 */

            //line.text = targetLine.text;// "AWEFAWFE jiowaioefjaoiwj o"
            //line.pointer = 3
            //line.indent = 0

            targetLine.owner = this

            lines.add(targetLine)
            val indexInfo = HierarchyItemIndexInfo()
            indexInfo.beforeIndex = i.toInt()
            indexInfoMap[targetLine] = indexInfo

            var node = targetLine.firstCodeNode;
//            var tempText = ""
//            while (node != null) {
//                tempText += node.text
//                node = node.nextCodeNode
//            }
//            targetLine.text = tempText

            targetLine = targetLine.next
        }
    }


    override fun returnItem(i:Int): Line {
        return this.lines[i]
    }

    override fun count(): Int {
        return this.lines.size
    }

    override fun getIndexInfo(item: HierarchyItem): HierarchyItemIndexInfo {
        return indexInfoMap[item]!!
    }

    override fun prepareForItemsModification() {
    }

    override fun prepareForDeleteItems() {
    }

}


class CodeDocument {
    var pointer: Long = 0

    var firstLine: Line? = null

    var lineNums: Long = 50

    fun performCodingOperation(context: Context, codingOp: CodingOperations) {
        val result = context.toActivity().performCodingOperation(codingOp.id)
    }
}

class CodingOperationResult {

}



class NewLines {

}

class CodeChangeInfo {



}

enum class CodingOperations(val id:Int) {
    AutoIndentSelection(1),
    AutoIndentForSpacingRule(2),
    BreakLine(3),
    Deletion(4),
    //AddMapItem
};


enum class CodeNodeType(val id: Int) {
    Document(0),
    EndOfDoc(1),
    StringLiteral(2),
    Symbol(3),
    Class(4),
    Name(5),
    SimpleText(6),
    JsonArrayItem(7),
    JsonArrayStruct(8),

    Number(9),
    LineBreak(10),
    Bool(11),

    JsonObject(12),
    JsonObjectKey(13),
    JsonKeyValueItem(14),

    Space(15),

    Func(17),

    Type(18),
    NULL(16),

    Body (19),
    AssignStatement(20),
    ReturnStatement(24),

    LineComment(21),
    BlockComment(22),
    BlockCommentFragment(23),

    Variable(25),
    Parentheses(26),
    CallFunc(27),
    FuncArgument(28),
    FuncParameter(29),
    BinaryOperation(30)

}


class Line : HierarchyItem() {
    var owner: CodeLines? = null
    var prev: Line? = null
    var next: Line? = null

    var pointer: Long = 0

    var indent: Int = 0
    var text: String = ""

    var firstCodeNode: CodeNode? = null;
    override val depth: Int
        get() = indent

}


class CodeNode {
    var pointer: Long = 0
    var prevSpaces: Int = 0
    fun setType(typeId:Int, parentTypeId: Int) {
        _type = CodeNodeType.values().firstOrNull { it.id == typeId }!!
        _parentType = CodeNodeType.values().firstOrNull { it.id == parentTypeId }!!
    }
    var _type: CodeNodeType = CodeNodeType.values()[2] //CodeNodeType.Symbol
    var _parentType: CodeNodeType = CodeNodeType.values()[2] //CodeNodeType.Symbol

    var nextCodeNode: CodeNode? = null
    var text: String = ""

    fun textWithSpaces(): String {
        if (prevSpaces > 0) {
            var spaces = " "
            /*
            for (i in 0..prevSpaces) {
                spaces += " "
            }*/
            return spaces + text
        }

        return text

    }
}
