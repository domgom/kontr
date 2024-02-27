package org.kontr.generator.postman

import com.squareup.kotlinpoet.FileSpec
import org.kontr.generator.core.Generator
import org.kontr.generator.core.IGenerator
import org.kontr.generator.core.IParser
import java.io.File
import java.io.InputStream
import java.nio.file.Paths

/**
 * @author Domingo Gomez
 */
class PostmanGenerator(
    private val postmanParser: IParser = PostmanParser(),
    private val generator: Generator = Generator()
) : IGenerator {
    override fun generateFromStreamToStream(collection: InputStream, packageName: String, fileName: String): FileSpec =
        generator.generate(postmanParser.parseGeneratorCollection(collection), packageName, fileName)

    override fun generateFromFileToFile(inputPath: String, outputPath: String, packageName: String, fileName: String) {
        val generatorCollection = postmanParser.parseGeneratorCollection(File(inputPath).inputStream())
        val file = generator.generate(generatorCollection, packageName, fileName)
        file.writeTo(Paths.get(outputPath))
    }


}