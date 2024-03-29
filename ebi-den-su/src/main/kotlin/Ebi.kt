import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.velocity.Template
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import util.EbiMenu
import util.TestResult
import util.TestReview
import java.io.File
import java.io.FileInputStream
import java.io.StringWriter
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.time.LocalDateTime
import java.util.*
import java.time.format.DateTimeFormatter




object Ebi {
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /**
     * http://velocity.apache.org/engine/2.0/developer-guide.html
     */
    private fun getVelocity(): VelocityContext {
        val p = Properties()
        p.setProperty("file.resource.loader.path", "./src/main/resources")
        Velocity.init(p)
        return VelocityContext()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val inputFile = args[0]
        val workbook = WorkbookFactory.create(FileInputStream(inputFile))
        for (sheet in workbook.sheetIterator()) {
            println(sheet.sheetName)
        }
        var resultDir = File("./out/test-result")
        resultDir.mkdirs()
        if (!resultDir.exists()) {
            throw RuntimeException("failed to create dir")
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

        var tests = studyRecipe(headerMaps, rows)
        var allTestResults = cookAllTests(tests)

        rows = testCaseSheet.rowIterator()
        rows.next()

        var testResultForHtml = arrayListOf<TestReview>()

        var lastResult = ""
        var lastResultEnum = TestReview.RESULT.NG
        var lastStartedAt = ""
        for (row in rows) {
            val testNodeStr = row.getCell(headerMaps["テストNO"]!!)
            if (testNodeStr == null) {
                continue
            }
            var verifyTarget = headerMaps["テスト項目"]!!.let{ if (row.getCell(it) == null ) "" else row.getCell(it)!!.stringCellValue}
            var verifyOverall = headerMaps["テスト概要"]!!.let{ if (row.getCell(it) == null ) "" else row.getCell(it)!!.stringCellValue}

            var className = row.getCell(headerMaps["クラス"]!!).let{ if (it ==  null) "" else it.stringCellValue!!}
            var methodName = row.getCell(headerMaps["メソッド"]!!).let{ if (it ==  null) "" else it.stringCellValue!!}
            var checkPoint = headerMaps["検証項目"]!!.let{ if (row.getCell(it) == null ) "" else row.getCell(it)!!.stringCellValue}
//            println(className)
//            println(methodName)

            if (allTestResults.containsKey(className) && allTestResults[className]!!.containsKey(methodName)) {
                var tests = allTestResults[className]!![methodName]!!
                var passed = tests.stream().allMatch{ t -> t.status ==  TestResult.STATUS.COMPLETED}
                lastResult = if (passed) "OK" else "NG"
                lastStartedAt = tests[0].startedAt!!.format(formatter)

                var res = TestReview()
                lastResultEnum = if (passed) TestReview.RESULT.OK else TestReview.RESULT.NG
                res.result = lastResultEnum
                res.className = className
                res.methodName = methodName
                res.checkPoint = checkPoint
                res.testTarget = verifyTarget
                res.testOverall = verifyOverall
                testResultForHtml.add(res)
            } else {
                println("no test")
                var res = TestReview()
                res.result = lastResultEnum
                res.className = className
                res.methodName = methodName
                res.checkPoint = checkPoint
                res.testTarget = verifyTarget
                res.testOverall = verifyOverall
                testResultForHtml.add(res)
            }

            headerMaps["テスト結果"]!!.also {
                var cell = row.getCell(it) ?: row.createCell(it)
                cell.setCellValue(lastResult)
            }
            headerMaps["実施日"]!!.also {
                var cell = row.getCell(it) ?: row.createCell(it)
                cell.setCellValue(lastStartedAt)
            }
        }
        workbook.write(File(resultDir, "テスト結果.xlsx").outputStream())

        getVelocity().let { velocity ->
            velocity.put("results", testResultForHtml)
            var template: Template = Velocity.getTemplate("top.vm")
            val sw = StringWriter()
            template.merge(velocity, sw)

            File(resultDir, "index.html").bufferedWriter().use { writer ->
                writer.write(sw.toString())
            }
        }
        for (className in allTestResults.keys) {
            var classTests: HashMap<String, List<TestResult>> = allTestResults[className] ?: continue
            val context = getVelocity()
            var template: Template = Velocity.getTemplate("detail.vm");

            for (methodName in classTests.keys) {

            }
            context.put("results", testResultForHtml)
            val sw = StringWriter()
            template.merge(context, sw)
        }

        println("Test completed")
    }

    private fun studyRecipe(headerMaps: HashMap<String, Int>, rows: Iterator<Row>): HashMap<String, EbiMenu> {
        val tests = hashMapOf<String, EbiMenu>()
        var lastClassName: String? = null
        var lastMethodName: String? = null
        for (row in rows) {
            val testNodeStr = row.getCell(headerMaps["テストNO"]!!)
            val className = row.getCell(headerMaps["クラス"]!!).let { if (it == null) lastClassName else it.stringCellValue!! }
            val methodName = row.getCell(headerMaps["メソッド"]!!).let { if (it == null) lastMethodName else it.stringCellValue!! }

            if (testNodeStr == null || className == null || methodName == null) {
                continue
            }
//            val testNo = testNodeStr.numericCellValue.toInt()
//            println("No ${testNo} - " + className + "#" + methodName)

            var meal: EbiMenu
            if (tests.containsKey(className)) {
                meal = tests[className]!!
            } else {
                meal = EbiMenu(className)
                tests[className] = meal
            }
            if (!meal.methods.contains(methodName)) {
                meal.methods.add(methodName)
                lastClassName = className
                lastMethodName = methodName
            }
        }
        return tests
    }

    private fun cookAllTests(tests: HashMap<String, EbiMenu>): HashMap<String, HashMap<String, List<TestResult>>> {
        var allTestResults = hashMapOf<String, HashMap<String, List<TestResult>>>()
        for (className  in tests.keys) {
            val recipe = tests[className]!!

            val clazz = Class.forName(className)
            var runWithAnnotation: RunWith? = null
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

            for (method in clazz.methods) {
                for (anno in method.annotations) {
                    if (anno is Before) {
                        recipe.beforeMethod = method
                        break
                    }
                    if (anno is After) {
                        recipe.afterMethod = method
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

            for (methodName in recipe.methods) {
                var method = clazz.getDeclaredMethod(methodName)
                var instances = arrayListOf<Any>()
                if (runWithAnnotation == null) {
                    instances.add(constructor.newInstance())
                } else {
                    for (param in parameters!!) {
                        instances.add(constructor.newInstance(param[0] as String))
                    }
                }
                var methodResuls = arrayListOf<TestResult>()
                for (instance in instances) {
                    var testResult = runTestCase(instance, recipe, method)
                    methodResuls.add(testResult)
                }
                var methodTest: HashMap<String, List<TestResult>>
                if (allTestResults.containsKey(className)) {
                    methodTest = allTestResults[className]!!
                } else {
                    methodTest = hashMapOf()
                    allTestResults[className] = methodTest
                }
                methodTest[methodName] = methodResuls
            }
        }
        return allTestResults
    }

    private fun runTestCase(ingredient: Any, recipe: EbiMenu, method: Method): TestResult {
        var result = TestResult()
        result.startedAt = LocalDateTime.now()
        try {
            if (recipe.beforeMethod != null) {
                recipe.beforeMethod!!.invoke(ingredient)
            }
        } catch (e: Exception) {
            result.status = TestResult.STATUS.FAILED
            result.error = e
            e.printStackTrace()
        }
        if (result.status == TestResult.STATUS.YET) {
            try {
                method.invoke(ingredient)
            } catch (e: Exception) {
                e.printStackTrace()
                result.status = TestResult.STATUS.FAILED
                result.error = e
            }
        }
        try {
            if (recipe.afterMethod != null) {
                recipe.afterMethod!!.invoke(ingredient)
            }
            result.status = TestResult.STATUS.COMPLETED
        } catch (e: Exception) {
            e.printStackTrace()
            result.status = TestResult.STATUS.FAILED
            result.error = e
        }
        result.endedAt = LocalDateTime.now()
        return result
    }
}