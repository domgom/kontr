package org.kontr.generator.core

import com.squareup.kotlinpoet.FileSpec
import java.io.File
import java.io.InputStream
import java.nio.file.Paths

/**
 * @author Domingo Gomez
 */
class GeneratorFacade(val parser: IParser) {
    fun generateFromStreamToStream(collection: InputStream, packageName: String, fileName: String): FileSpec =
        Generator().generate(parser.parseGeneratorCollection(collection), packageName, fileName)

    fun generateFromFileToFile(inputPath: String, outputPath: String, packageName: String, fileName: String) {
        generateFromStreamToStream(File(inputPath).inputStream(), packageName, fileName).writeTo(Paths.get(outputPath))
    }
}