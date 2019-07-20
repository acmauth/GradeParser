/*
 * MIT License
 *
 * Copyright (c) 2019 Konstantinos Papakostas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.din0s.parse

import me.din0s.io.CsvWriter
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File

object PdfParser : IParser {
    /*
     * [CODE] [COURSE] [TYPE] ([YEAR] [MONTH] )[ECTS] [UNIT] [COEFF]( [GRADE])
     *
     * CODE:    CCC-NN-NN or NNN
     * COURSE:  CC..CC
     * TYPE:    CCC
     * YEAR:    NNNN - NNNN         {optional}
     * MONTH:   CCCC                {optional}
     * ECTS:    D
     * UNIT:    D
     * COEFF:   D
     * GRADE:   D                   {optional}
     *
     * C: character
     * N: number
     * D: potentially decimal
     */
    private const val code = "(\\w+-\\d+-\\d+|\\d+)"
    private const val word = "(\\D+)"
    private const val year = "(\\d+ - \\d+)"
    private const val month = "(\\D+)"
    private const val number = "(\\d+(?:\\.\\d+)?)"

    private val rowRegex = "$code $word\\s$word (?:$year $month )?$number $number $number(?: $number)?".toRegex()

    override fun parse(source: String) {
        val sb = StringBuilder()

        PDDocument.load(File(source)).use { doc ->
            val stripper = PDFTextStripper()
            val pdf = stripper.getText(doc)

            rowRegex.findAll(pdf)
                .map { row ->
                    row.groupValues.drop(1).joinToString(", ") {
                        it.replace("-\n", "-").replace("\n", " ")
                    }.trim()
                }.forEach {
                    sb.appendln(it)
                }
        }

        CsvWriter.write(sb.toString(), source)
    }
}
