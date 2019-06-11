import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.FileInputStream

object Ebi {
    @JvmStatic
    fun main(args: Array<String>) {
        val inputFile = args[0]
        val workbook = WorkbookFactory.create(FileInputStream(inputFile))
        for (sheet in workbook.sheetIterator()) {
            println(sheet.sheetName)
        }

        var testCaseSheet = workbook.getSheet("テストケース")!!


        var row = testCaseSheet.rowIterator().next()
        var headerCells = arrayListOf<Cell>()
        for (cell in row.cellIterator()) {
            headerCells.add(cell)
        }

        println(headerCells)

    }
}