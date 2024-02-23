package org.kontr.generator.core;

/**
 * @author Domingo Gomez
 */
interface IGenerator {
    fun generate(inputPath: String, outputPathRootName: String, packageName: String, fileName: String)
}
