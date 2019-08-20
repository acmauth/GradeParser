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
import org.jsoup.Jsoup
import java.io.File

object HtmlParser : IParser {
    override fun parse(source: String) {
        val courses = mutableListOf<String>()

        val lines = File(source)
            .readLines()
            .joinToString("")
            .substringAfter("<html>")
            .substringBefore("</html>")
        val html = Jsoup.parse("<html>$lines</html>").body()
        html.getElementsByClass("mytableSIS").forEach { table ->
            val rows = table.select("tbody").select("tr")
            rows.forEach { row ->
                val cols = row.select("td")
                courses.add(cols.joinToString(", ") { it.text() })
            }
        }

        val passed = lines
            .substringAfter("Σύνολο περασμένων μαθημάτων: <b>")
            .substringBefore("</b>")
            .toInt()

        validate(courses, passed)
        CsvWriter.write(courses, source)
    }
}
