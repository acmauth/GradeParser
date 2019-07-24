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
import java.io.File

object HtmlParser : IParser {
    private fun StringBuilder.isBefore(next: String, end: String) : Boolean {
        return contains(next) && indexOf(next) < indexOf(end)
    }

    private fun StringBuilder.drop(str: String) : StringBuilder {
        return delete(0, indexOf(str) + str.length)
    }

    private fun StringBuilder.getRow() : String {
        when {
            isBefore("<td>", "<td style") -> drop("<td>")
            else -> drop("\">")
        }

        return substring(0, indexOf("</td>")).trim().replace("&amp;", "&")
    }

    override fun parse(source: String) {
        val courses = mutableListOf<String>()

        val lines = File(source)
            .readLines()
            .joinToString("")
            .substringAfter("<div><h3>")
            .substringBefore("</html>")
        val html = StringBuilder(lines)

        while (html.contains("</h3></div>")) {
            html.drop("<tbody>")
            while (html.isBefore("<tr>", "</tbody>")) {
                val values = mutableListOf<String>()
                while(html.isBefore("<td", "</tr>")) {
                    values.add(html.getRow())
                }

                courses.add(values.joinToString(", "))
                html.drop("</tr>")
            }
        }

        html.drop("Σύνολο περασμένων μαθημάτων: <b>")
        val end = html.indexOf("</b>")
        val passed = html.substring(0, end).toInt()
        validate(courses, passed)
        CsvWriter.write(courses, source)
    }
}
