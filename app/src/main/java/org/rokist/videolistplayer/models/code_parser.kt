package org.rokist.videolistplayer.models

class ParseContext {
    var text: String = ""
    var currentPosition: Int = 0
    var parseTags = arrayListOf(ArrayTag(""), IfTag(""), UnIfTag(""), DataTag(""))
}

class ResolveContext {
    var contextObject: Map<String, Any> = mapOf()
    var contextPath: String = ""
    var rootObject: Map<String, Any> = mapOf()
    var result: String = ""
}

open class TagBasis(text: String) {
    var regex: String = ""
    var closeTag: String? = null
    var matchText: String = ""
    var childs: ArrayList<Any> = arrayListOf()
    var value: String = text
    var shouldReturn: Boolean = false
    var startPosition: Int = 0
    var endPosition: Int = 0

    open fun newInstance(text: String): TagBasis {
        return TagBasis(text)
    }

    private fun searchNext(context: ParseContext) {
        val nsstr = context.text + ""

        for (node in context.parseTags) {
            node.startPosition = -1

            val regex = Regex(node.regex)
            val matchResult = regex.find(context.text, context.currentPosition)
            if (matchResult != null) {
                node.startPosition = matchResult.range.first
                node.endPosition = matchResult.range.last + 1

                for (g in matchResult.groupValues) {
                    node.matchText = g
                }
            }
        }

        val sortedList = context.parseTags.sortedWith(compareBy {
            val pos = if (it.startPosition == -1) Int.MAX_VALUE else it.startPosition
            pos
        })

        val nearestTag = sortedList[0]

        closeTag?.let {
            val pos = nsstr.indexOf(it, context.currentPosition, false)
            if (pos > -1 && (nearestTag.startPosition == -1 || nearestTag.startPosition >= pos)) {
                childs.add(nsstr.substring(context.currentPosition, pos))
                context.currentPosition = pos + it.length
                shouldReturn = true
                return
            }
        }

        if (nearestTag.startPosition == -1) {
            childs.add(nsstr.substring(context.currentPosition) as Any)
            context.currentPosition = nsstr.length
            shouldReturn = true
            return
        }

        val loc = nearestTag.startPosition
        if (context.currentPosition != loc - context.currentPosition) {
            childs.add(nsstr.substring(context.currentPosition, loc))
        }

        val newTag = nearestTag.newInstance(nearestTag.matchText)
        childs.add(newTag)

        context.currentPosition = nearestTag.endPosition

        newTag.closeTag?.let {
            newTag.run(context)
        }
    }

    fun run(context: ParseContext) {
        while (!shouldReturn && context.currentPosition < context.text.length) {
            searchNext(context)
        }
    }

    protected open fun resolveInternal(context: ResolveContext) {
        for (child in childs) {
            if (child is String) {
                context.result += child
            } else if (child is TagBasis) {
                child.resolveInternal(context)
            }
        }
    }

    fun getContextObjectWithPath(context: ResolveContext): Any? {
        if (value == context.contextPath) {
            return context.contextObject
        }
        if (context.contextPath == "" || (value).startsWith(context.contextPath)) {

            val dict = context.contextObject
            val words =
                if (context.contextPath == "")
                    listOf(value)
                else
                    value.substring(context.contextPath.length + 1).split("/")

            var currentDict = dict
            for (i in words.indices) {
                val term = words[i]
                if (term in currentDict) {
                    if (i == words.size - 1) {
                        return currentDict[term]
                    } else if (term in currentDict) {
                        @Suppress("UNCHECKED_CAST")
                        val dd = currentDict[term] as? Map<String, Any>
                        if (dd != null) {
                            currentDict = dd
                            continue
                        }
                    }
                }
                break
            }

        }
        val dic = context.rootObject as? Map<String, Any>
        dic?.let {
            val words = value.split('/')
            var currentDict = it

            for (i in words.indices) {
                val term = words[i]
                if (i == words.size - 1) {
                    return currentDict[term]
                } else if (term in currentDict) {
                    @Suppress("UNCHECKED_CAST")
                    val dd = currentDict[term] as? Map<String, Any>
                    if (dd != null) {
                        currentDict = dd
                        continue
                    }
                }
                return null
            }
        }

        return null
    }
}

class ArrayTag(value: String) : TagBasis(value) {
    init {
        regex = "\\[loop ([^\\]]+)\\]"
        closeTag = "[/loop]"
    }

    override fun newInstance(text: String): TagBasis {
        return ArrayTag(text)
    }

    override fun resolveInternal(context: ResolveContext) {
        val currentContextPath = context.contextPath
        val currentContextObject: Map<String, Any> = context.contextObject

        val mutableList = mutableListOf<Any>()
        @Suppress("UNCHECKED_CAST")
        val arrays = getContextObjectWithPath(context) as? Array<Any>
        if (arrays != null) {
            mutableList.addAll(arrays)
        } else {
            @Suppress("UNCHECKED_CAST")
            val arraysTemp = getContextObjectWithPath(context) as? ArrayList<Any>
            if (arraysTemp != null) {
                mutableList.addAll(arraysTemp)
            }
        }

        mutableList.let {
            context.contextPath = value

            for (item in it) {
                @Suppress("UNCHECKED_CAST")
                val dic = item as? Map<String, Any>
                if (dic != null) {
                    context.contextObject = dic
                    super.resolveInternal(context)
                } else {
                    context.contextObject =
                        mapOf("String" to item.toString())
                    super.resolveInternal(context)
                }
            }
        }

        context.contextPath = currentContextPath
        context.contextObject = currentContextObject
    }
}

class IfTag(value: String) : TagBasis(value) {
    init {
        regex = "\\[if ([^\\]]+)\\]"
        closeTag = "[/if]"
    }

    override fun newInstance(text: String): TagBasis {
        return IfTag(text)
    }

    override fun resolveInternal(context: ResolveContext) {
        val obj: Any? = getContextObjectWithPath(context)
        obj?.let {
            (it as? Boolean).let {
                if (it == false) {
                    return
                }
            }
            (it as? String)?.let {
                if (it == "") {
                    return
                }
            }
            super.resolveInternal(context)
        }
    }
}

class UnIfTag(value: String) : TagBasis(value) {
    init {
        regex = "\\[unif ([^\\]]+)\\]"
        closeTag = "[/unif]"
    }

    override fun newInstance(text: String): TagBasis {
        return UnIfTag(text)
    }

    override fun resolveInternal(context: ResolveContext) {
        val obj: Any? = getContextObjectWithPath(context)
        if (obj != null) {
            (obj as? Boolean)?.let {
                if (!it) {
                    super.resolveInternal(context)
                }
            }
            (obj as? String)?.let {
                if (it == "") {
                    super.resolveInternal(context)
                }
            }
        } else {
            super.resolveInternal(context)
        }
    }
}

class DataTag(text: String) : TagBasis(text) {
    init {
        regex = "\\{\\$([^\\}]+)\\}"
    }

    override fun newInstance(text: String): TagBasis {
        return DataTag(text)
    }

    override fun resolveInternal(context: ResolveContext) {
        val obj = getContextObjectWithPath(context)
        if (obj != null) {
            if (obj is String) {
                context.result += obj
            } else if (obj is Array<*>) {
                context.result += "Array"
            } else if (obj is Map<*, *>) {
                val dict = obj
                for ((_, v) in dict) {
                    context.result += "$v"
                }
            } else {
                context.result += "$obj"
            }
        }
    }
}

class HtmlTemplate() : TagBasis("") {

    fun parse(text: String, obj: Map<String, Any>): String {
        val regex = Regex(
            "\\n[\\ ]*(\\[loop ([^\\]]+)\\]|\\[/loop\\]|\\[if ([^\\]]+)\\]|\\[/if\\]|\\[unif ([^\\]]+)\\]|\\[/unif\\])"
            , RegexOption.MULTILINE
        )
        val context = ParseContext()
        context.text = regex.replace(text, "$1")
        run(context)
        return resolve(obj)
    }

    private fun resolve(obj: Map<String, Any>): String {
        val resolveContext = ResolveContext()
        resolveContext.rootObject = obj
        resolveContext.contextObject = obj
        resolveInternal(resolveContext)
        return resolveContext.result
    }
}

fun main() {
    val template = """<!DOCTYPE html>
<html lang="ja">
<body>

{$\title}<br>

[if bool]{$\bool}<br>[/if]

[unif bool]FALSE<br>[/unif]

[loop array]
    {$\array}<br>
[/loop]

[loop array2]
    [loop array2}<br>
[/loop]

</body>
</html>
""".replace("\$\\", "\$")

    val ht = HtmlTemplate()
    val map = mutableMapOf<String, Any>()

    map["title"] = "TestHTML"

    map["bool"] = true

    map["array"] = arrayListOf(
        "1", "2", "3"
    )
    val result = ht.parse(template, map)
    println(result)
}