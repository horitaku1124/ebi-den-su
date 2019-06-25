package util

import java.lang.reflect.Method

class EbiMenu(var className: String) {
    var methods = arrayListOf<String>()

    var beforeMethod: Method? = null
    var afterMethod: Method? = null
}