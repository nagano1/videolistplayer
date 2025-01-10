package org.rokist.videolistplayer

import android.view.MotionEvent
import java.util.*


enum class Direction {
    Up, Down, Left, Right
}


/**
 * ListViewにおけるジェスチャー機能を実装します。 水平フリック操作から始まります。
 */
class CommonGesture {
    private var _downY = 0f
    private var _downX = 0f
    private var _isCanceled = false
    var isGestureStarted = false
        private set
    private var _farPoint // 現在のジェスチャー方向の最も奥の値
            = 0f
    var _directions =
        ArrayList<Direction?>()
    var _currentDirection: Direction? = null
    private var _verticalOriginPoint = 0f

    fun onTouchEvent(event: MotionEvent) {
        val action = event.action
        if (action == MotionEvent.ACTION_MOVE) {
            if (_isCanceled) return
            val x = event.x
            val y = event.y
            if (isGestureStarted) {
                val point = getPoint(_currentDirection!!, x, y)
                val verticalPoint = getVerticalPoint(_currentDirection!!, x, y)

                // 最奥地点の更新
                _farPoint =
                    getFarPoint(_currentDirection, _farPoint, getPoint(_currentDirection!!, x, y))
                if (Math.abs(_farPoint - point) > 100) {
                    // 反対側ジェスチャー
                    _verticalOriginPoint = verticalPoint
                    _farPoint = point
                    _currentDirection = getOppositeDirection(_currentDirection!!)
                    _directions.add(_currentDirection)

                    //Log.d(getDirectionString(_currentDirection), "s");
                } else if (Math.abs(_verticalOriginPoint - verticalPoint) > 50) {
                    // 真横側ジェスチャ
                    _currentDirection =
                        getVerticalDierction(_currentDirection!!, _verticalOriginPoint, verticalPoint)
                    _farPoint = verticalPoint
                    _verticalOriginPoint = point
                    _directions.add(_currentDirection)
                    //Log.d(getDirectionString(_currentDirection), "s");
                }
            } else {
                if (Math.abs(y - _downY) < 30) {
                    if (Math.abs(x - _downX) > 70) {
                        isGestureStarted = true
                        _directions.clear()
                        _currentDirection =
                            if (x > _downX) Direction.Right else Direction.Left
                        _farPoint = x
                        _verticalOriginPoint = y
                        _directions.add(_currentDirection)


                        //Log.d("ジェスチャー開始", "だよね");
                    }
                } else {
                    _isCanceled = true
                }
            }
        } else if (action == MotionEvent.ACTION_DOWN) {
            _downX = event.x
            _downY = event.y
            init()
        } else if (action == MotionEvent.ACTION_UP) {
        }
    }

    fun init() {
        _directions.clear()
        _isCanceled = false
        isGestureStarted = false
    }

    /**
     * 現在の方向に垂直な方向の値を返します。
     */
    private fun getVerticalPoint(
        direction: Direction,
        x: Float,
        y: Float
    ): Float {
        return if (isYAxis(direction)) x else y
    }

    enum class Direction {
        Up, Down, Left, Right
    }

    /**
     * 方向にとって最も遠い値を返す。
     */
    private fun getFarPoint(
        direction: Direction?,
        q: Float,
        p: Float
    ): Float {
        return if (direction == Direction.Right || direction == Direction.Down) Math.max(
            p,
            q
        ) // 大きいほうが遠い
        else Math.min(p, q) // 小さいほうが遠い
    }

    private fun getPoint(
        a: Direction,
        x: Float,
        y: Float
    ): Float {
        return if (isYAxis(a)) y else x
    }

    private fun isYAxis(direction: Direction): Boolean {
        return direction == Direction.Up || direction == Direction.Down
    }

    private fun getVerticalDierction(
        direction: Direction,
        from: Float,
        to: Float
    ): Direction {
        return if (isYAxis(direction)) {
            if (from < to) Direction.Right else Direction.Left
        } else {
            if (from < to) Direction.Down else Direction.Up
        }
    }

    private fun getOppositeDirection(direction: Direction): Direction {
        if (direction == Direction.Down) return Direction.Up
        if (direction == Direction.Up) return Direction.Down
        return if (direction == Direction.Left) Direction.Right else Direction.Left
    }

    fun isGesture(directions: List<Direction>): Boolean {
        return this._isGesture(directions)
    }

    fun _isGesture(directions: List<Direction>): Boolean {
        assert(isGestureStarted)

        if (directions.size != _directions.size) return false
        var i = 0
        for (direction in _directions) {
            if (directions[i++] != direction) {
                return false
            }
        }
        return true
    }

    /**
     * 現在のジェスチャーの方向リストを返します。
     *
     * @return
     */
    val currentGesture: List<Direction?>
        get() = _directions

    val gestureString: String
        get() = getGestureString(_directions)

    /**
     * 次のジェスチャーに進んだかどうかを返します。
     *
     * @return
     */
    val isNext: Boolean
        get() = true

    companion object {
        private fun getDirectionString(direction: Direction?): String {
            if (direction == Direction.Up) return "↑"
            if (direction == Direction.Down) return "↓"
            return if (direction == Direction.Left) "←" else "→"
        }

        fun getGestureString(directions: List<Direction?>): String {
            val sb = StringBuilder()
            for (d in directions) {
                sb.append(getDirectionString(d))
            }
            return sb.toString()
        }
    }
}
