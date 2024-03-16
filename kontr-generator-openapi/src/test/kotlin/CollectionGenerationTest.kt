import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.kontr.generator.core.GenerationOptions
import org.kontr.generator.core.GeneratorFacade
import org.kontr.generator.openapi.OpenApiParser

import java.io.File
import java.io.File.separator

/**
 * @author Domingo Gomez
 */
class CollectionGenerationTest {
    private val generatorFacade: GeneratorFacade = GeneratorFacade(parser = OpenApiParser())

    @Test
    fun `test generate weather api collection`() {
        val fileName = "Collection"
        val collectionPathName = javaClass.getResource("employee.api.json")?.file!!
        val packageName = "org.example.generated"
        val outputPathRootName = "target/generated-test-sources/openapi"
        val outputFile =
            File(outputPathRootName + separator + packageName.replace(".", separator) + separator + fileName + ".kt")

        if (File(outputPathRootName).exists()) {
            File(outputPathRootName).deleteRecursively()
        }

        generatorFacade.generateFromFileToFile(
            collectionPathName,
            outputPathRootName,
            GenerationOptions(fileName = fileName, packageName = packageName)
        )
        assertThat(outputFile.exists()).isTrue()
        outputFile.inputStream().use { it.copyTo(System.out) }
        // TODO improve to have good assertions but for now this is better than nothing to know if output changes
        assertThat(outputFile.readText()).isEqualTo(
            "package org.example.generated\n" +
                    "\n" +
                    "import org.kontr.dsl.CollectionDsl\n" +
                    "import org.kontr.dsl.ResponseDsl\n" +
                    "import org.kontr.dsl.collection\n" +
                    "\n" +
                    "public data object Env\n" +
                    "\n" +
                    "public object BuzzFizz {\n" +
                    "  public fun CollectionDsl.ApiPay(): ResponseDsl = get(\"/api/Pay\"){\n" +
                    "      body = \"\"\"\"\"\"\n" +
                    "      onResponse{ healthy }\n" +
                    "  }\n" +
                    "\n" +
                    "  public fun runCollection0() {\n" +
                    "    val c = collection{\n" +
                    "      `ApiPay`()\n" +
                    "    }\n" +
                    "  }\n" +
                    "}\n"
        )
    }
}