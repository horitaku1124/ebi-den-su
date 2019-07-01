package util

class TestReview {
    enum class RESULT {
        OK, NG
    }

    var result: RESULT = RESULT.NG
    var className: String = ""
    var methodName: String = ""
    var checkPoint: String = ""
    var testTarget: String = ""
    var testOverall: String = ""
}