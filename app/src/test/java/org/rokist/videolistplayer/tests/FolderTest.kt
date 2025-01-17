package org.rokist.videolistplayer.tests

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest
{
    private fun indentOffsetExponentialDecay(n: Int): Float
    {
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
    fun empty_test()
    {
        assertEquals(0, 1 - 1)
    }
}