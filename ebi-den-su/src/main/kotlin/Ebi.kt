import org.apache.poi.ss.usermodel.WorkbookFactory
import org.junit.runner.RunWith
import java.io.FileInputStream
import org.junit.runner.JUnitCore

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

        for (row in rows) {
            var className = row.getCell(headerMaps["クラス"]!!).stringCellValue!!
            var methodName = row.getCell(headerMaps["メソッド"]!!).stringCellValue!!
            println(className)
            println(methodName)
            val clazz = Class.forName(className)

            var runWithAnnotation: Annotation? = null
            for (anno in clazz.annotations) {
                if (anno is RunWith) {
                    runWithAnnotation = anno
                    println(anno)
                }
            }
            var instance: Any
            if (runWithAnnotation == null) {
                instance = clazz.getConstructor().newInstance()

                var method = clazz.getDeclaredMethod(methodName)
                method.invoke(instance)
            } else {
                JUnitCore.runClasses(clazz)
            }
        }
    }
}