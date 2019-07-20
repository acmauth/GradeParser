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
    private const val code = "(\\D+-\\d+-\\d+)"
    private const val word = "(\\D+)"
    private const val year = "(\\d+ - \\d+)"
    private const val month = "(\\D+)"
    private const val optDecimal = "(\\d+(?:.\\d+)?)"
    private val rowRegex = "$code $word\\s$word $year $month $optDecimal $optDecimal $optDecimal $optDecimal".toRegex()

    private fun StringBuilder.getRow() : String {
        val end = indexOf("Εξάμηνο:")
        val region = when {
            end != -1 -> substring(0, end).toString()
            else -> toString()
        }

        val match = rowRegex.find(region) ?: throw IllegalArgumentException()
        drop(match.value)

        return match.groupValues.drop(1).joinToString(", ") {
            it.replace("-\n", "-").replace("\n", " ")
        }.trim()
    }

    override fun parse(source: String) {
        val sb = StringBuilder()
        val pdf = StringBuilder()

        PDDocument.load(File(source)).use {
            val stripper = PDFTextStripper()
            pdf.append(stripper.getText(it))
        }

        while (pdf.contains("Εξάμηνο:")) {
            pdf.drop("Βαθμ")

            while (pdf.isBefore("-", " - ")) {
                if (pdf.isBefore("Βαθμ", "-")) {
                    pdf.drop("Βαθμ")
                }

                sb.appendln(pdf.getRow())
            }
        }

        CsvWriter.write(sb.toString(), source)
    }
}
