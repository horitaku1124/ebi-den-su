package util

import java.lang.Exception

class TestResult {
    enum class STATUS {
        YET, SKIPPED, PENDING, RUNNING, COMPLETED, FAILED
    }
    var status = STATUS.YET
    var error: Exception? = null
}