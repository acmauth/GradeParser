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

import java.io.File
import kotlin.system.exitProcess

abstract class IWriter(private val extension: String) {
    private val dirName = extension + File.separator

    init {
        val dir = File(dirName)
        dir.mkdir()
    }

    internal fun write(data: List<String>, source: String) {
        val fileName = File(source).nameWithoutExtension
        val out = File("$dirName${fileName}_results.$extension")
        if (!out.exists() && !out.createNewFile()) {
            System.err.println("Could not create file!")
            exitProcess(1)
        } else {
            saveToFile(out, data)
            println("Parsed $source\n------------------")
        }
    }

    abstract fun saveToFile(out: File, data: List<String>)
}