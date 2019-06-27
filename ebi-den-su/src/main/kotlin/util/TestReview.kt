package util

class TestReview {
    enum class RESULT {
        OK, NG
    }

    var result: RESULT = RESULT.NG
    var className: String? = null
    var methodName: String? = null
}