package dense

import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class TestCase1 {
    @Test
    fun test1() {
        val a = 1 + 1
        println("1 + 1 = ${a}")
        assertSame(a, 2)
    }
}