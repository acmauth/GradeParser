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

package me.din0s.io

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object JsonWriter : IWriter("json") {
    private val splitRegex = "\\s*,\\s*".toRegex()

    override fun saveToFile(out: File, data: List<String>) {
        val json = JSONObject()
        val courses = JSONArray()
        data.forEachIndexed { index, str ->
            val values = str.split(splitRegex)
            val course = JSONObject()
            course.put("code", values[0])
            course.put("name", values[1])
            course.put("type", values[2])
            course.put("year", values[3])
            course.put("month", values[4])
            course.put("ects", values[5])
            course.put("unit", values[6])
            course.put("coeff", values[7])
            course.put("grade", values[8])
            courses.put(index, course)
        }
        json.put("courses", courses)
        out.writeText(json.toString(4))
    }
}
