package org.kontr.generator.core;

import com.squareup.kotlinpoet.FileSpec

/**
 * @author Domingo Gomez
 */
interface IGenerator {
    fun generate(inputPath: String, outputPath: String, packageName: String, fileName: String)

    fun generate(inputPath: String, packageName: String, fileName: String): FileSpec
}
