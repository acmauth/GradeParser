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

package me.din0s

import me.din0s.parse.HtmlParser
import me.din0s.parse.PdfParser
import java.io.File
import java.util.logging.Logger

private fun Array<String>.parse(prefix: String = "") {
    forEach {
        val file = when {
            prefix.isNotBlank() -> "$prefix/$it"
            it.endsWith('/') -> it.substringBeforeLast('/')
            else -> it
        }

        val dir = File(file)
        if (dir.isDirectory) {
            val fileList = dir.list()!!
            println("""Detected directory "$file" with ${fileList.size} files:""")
            fileList.parse(file)
            return@forEach
        }

        if (it.endsWith(".pdf")) {
            PdfParser.parse(file)
        } else if (it.endsWith(".html") || it.endsWith(".eml") ){
            HtmlParser.parse(file)
        } else {
            System.err.println("""
                Detected a file in an unsupported format:
                $file
                ------------------
            """.trimIndent())
        }
    }
}

fun main(args: Array<String>) {
    Logger.getLogger("org.apache.pdfbox").level = java.util.logging.Level.SEVERE

    if (args.isEmpty()) {
        print("File name: ")
        val file = readLine() ?: return
        arrayOf(file).parse()
    } else {
        println("Given paths:")
        args.forEach { println(it) }
        args.parse()
    }
    println("Job's done!")
}
