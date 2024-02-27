package org.kontr.generator.core;

import com.squareup.kotlinpoet.FileSpec
import java.io.InputStream

/**
 * @author Domingo Gomez
 */
interface IGenerator {
    fun generateFromFileToFile(inputPath: String, outputPath: String, packageName: String, fileName: String)
    fun generateFromStreamToStream(collection: InputStream, packageName: String, fileName: String): FileSpec

}
