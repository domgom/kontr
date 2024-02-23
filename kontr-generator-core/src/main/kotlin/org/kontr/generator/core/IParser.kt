package org.kontr.generator.core;

/**
 * @author Domingo Gomez
 */
interface IParser {
    fun parseGeneratorCollection(inputPath: String): GeneratorCollection
}
