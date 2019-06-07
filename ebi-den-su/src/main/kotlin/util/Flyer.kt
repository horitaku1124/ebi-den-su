package util

import java.lang.AssertionError

fun Int.mustBe(expected: Int) {
    if (this != expected) {
        throw AssertionError("Expected: ${expected} But: ${this}")
    }
}

fun String.mustBe(expected: String) {
    if (this != expected) {
        throw AssertionError("Expected: ${expected} But: ${this}")
    }
}
