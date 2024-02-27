package org.kontr.generator.core;

import java.io.InputStream

/**
 * @author Domingo Gomez
 */
interface IParser {
    fun parseGeneratorCollection(collection: InputStream): GeneratorCollection
}
