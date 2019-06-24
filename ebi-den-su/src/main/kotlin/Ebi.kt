import org.apache.poi.ss.usermodel.WorkbookFactory
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import java.io.FileInputStream
import org.junit.runners.Parameterized
import util.EbiMenu
import java.lang.reflect.Constructor
import java.lang.reflect.Method

object Ebi {
    @JvmStatic
    fun main(args: Array<String>) {
        val inputFile = args[0]
        val workbook = WorkbookFactory.create(FileInputStream(inputFile))
        for (sheet in workbook.sheetIterator()) {
            println(sheet.sheetName)
        }

        var testCaseSheet = workbook.getSheet("テストケース")!!


        var rows = testCaseSheet.rowIterator()
        var headerRow = rows.next()
        var index = 0
        var headerMaps = hashMapOf<String, Int>()
        for (cell in headerRow.cellIterator()) {
            headerMaps.put(cell.stringCellValue, index)
            index += 1
        }
        println(headerMaps)

        var tests = hashMapOf<String, EbiMenu>()
        for (row in rows) {
            var className = row.getCell(headerMaps["クラス"]!!).stringCellValue!!
            var methodName = row.getCell(headerMaps["メソッド"]!!).stringCellValue!!
            println(className)
            println(methodName)

            var meal: EbiMenu
            if (tests.containsKey(className)) {
                meal = tests[className]!!
            } else {
                meal = EbiMenu(className)
                tests[className] = meal
            }

            meal.methods.add(methodName)

        }
        for (className  in tests.keys) {
            var meal = tests[className]!!

            val clazz = Class.forName(className)
            var runWithAnnotation: RunWith? = null
            var parameterizedParametes: Parameterized.Parameters? = null
            var parameters: Iterable<Array<Any>>? = null
            for (anno in clazz.annotations) {
                if (anno is RunWith) {
                    runWithAnnotation = anno
                }
            }
            if (runWithAnnotation != null) {
                var value = runWithAnnotation.value
                var runWithName = value.java.canonicalName
                if (runWithName == "org.junit.runners.Parameterized") {

                    for (method in clazz.declaredMethods) {
                        for (anno in method.annotations) {
                            var annoName = anno.toString()
                            var cannoName = anno.javaClass.canonicalName
                            var isAnno = anno is Parameterized.Parameters
                            if (isAnno) {
                                var obj = Object()
                                parameters = method.invoke(obj) as Iterable<Array<Any>>?
                            }
                        }
                    }
                }
            }

            var beforeMethod: Method? = null
            var afterMethod: Method? = null
            for (method in clazz.methods) {
                for (anno in method.annotations) {
                    if (anno is Before) {
                        beforeMethod = method
                        break
                    }
                    if (anno is After) {
                        afterMethod = method
                        break
                    }
                }
            }


            var constructor: Constructor<out Any>
            if (runWithAnnotation == null) {
                constructor = clazz.getConstructor()
            } else {
                constructor = clazz.getConstructor(String::class.java)
            }

            for (methodName in meal.methods) {
                var method = clazz.getDeclaredMethod(methodName)
                var instance: Any
                if (runWithAnnotation == null) {
                    instance = constructor.newInstance()
                    if (beforeMethod != null) {
                        beforeMethod.invoke(instance)
                    }
                    method.invoke(instance)
                    if (afterMethod != null) {
                        afterMethod.invoke(instance)
                    }
                } else {
                    for (param in parameters!!) {
                        var browser = param[0] as String
                        instance = constructor.newInstance(browser)
                        if (beforeMethod != null) {
                            beforeMethod.invoke(instance)
                        }
                        method.invoke(instance)
                        if (afterMethod != null) {
                            afterMethod.invoke(instance)
                        }
                    }
                }
            }
        }
    }
}