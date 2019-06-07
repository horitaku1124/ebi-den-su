import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.opentest4j.AssertionFailedError
import util.*

class TestOfTest {
    @Test
    fun `1 + 1 = 2`() {
        val a = 1 + 1
        a.mustBe(2)
        assertSame(2, a)
    }

    @Test
    fun `1 + 1 != 1`() {
        assertThrows(AssertionFailedError::class.java) {
            val a = 1 + 1
            assertSame(1, a)
        }
        assertThrows(AssertionError::class.java) {
            val a = 1 + 1
            a.mustBe(1)
        }
    }
}