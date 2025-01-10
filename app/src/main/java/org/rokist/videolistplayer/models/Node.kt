package org.rokist.videolistplayer.models

var l = 32

interface Flyable {
    fun fly()
}

abstract class CodeCompo (protected var x:Int = 32) : Flyable {
    fun abc() {

    }

    override fun fly(){

    }

    abstract fun func()
}



class BlockNode : CodeCompo(3) {
    override fun func() {
    }
}





// class {
//
// }
class ClassComponent : CodeCompo(3) {
    init {
        this.x = 4
    }

    public override fun func() {
        /* compiled code */
    }
}

