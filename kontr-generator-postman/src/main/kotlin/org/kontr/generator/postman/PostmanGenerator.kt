package org.kontr.generator.postman

import org.kontr.generator.core.Generator
import org.kontr.generator.core.IGenerator
import java.nio.file.Paths

/**
 * @author Domingo Gomez
 */
class PostmanGenerator(
    private val postmanParser: PostmanParser = PostmanParser(),
    private val generator: Generator = Generator()
) : IGenerator {
    override fun generate(inputPath: String, outputPath: String, packageName: String, fileName: String) {
        val generatorCollection = postmanParser.parseGeneratorCollection(inputPath)
        val file = generator.generate(generatorCollection, packageName, fileName)
        file.writeTo(Paths.get(outputPath))
    }

}