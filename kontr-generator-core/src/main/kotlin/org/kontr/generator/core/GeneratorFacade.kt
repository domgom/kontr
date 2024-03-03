package org.kontr.generator.core

import com.squareup.kotlinpoet.FileSpec
import java.io.File
import java.io.InputStream
import java.nio.file.Paths

/**
 * @author Domingo Gomez
 */
class GeneratorFacade(val parser: IParser) {
    fun generateFromStreamToStream(collection: InputStream, generationOptions: GenerationOptions): FileSpec =
        Generator(generationOptions).generate(parser.parseGeneratorCollection(collection))

    fun generateFromFileToFile(inputPath: String, outputPath: String, generationOptions: GenerationOptions) {
        generateFromStreamToStream(File(inputPath).inputStream(), generationOptions).writeTo(Paths.get(outputPath))
    }
}