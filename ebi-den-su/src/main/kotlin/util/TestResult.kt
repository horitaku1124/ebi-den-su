package util

import java.lang.Exception
import java.time.LocalDateTime

class TestResult {
    enum class STATUS {
        YET, SKIPPED, PENDING, RUNNING, COMPLETED, FAILED
    }
    var status = STATUS.YET
    var error: Exception? = null
    var startedAt: LocalDateTime? = null
    var endedAt: LocalDateTime? = null
}