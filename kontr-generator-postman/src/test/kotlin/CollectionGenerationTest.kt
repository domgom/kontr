import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.kontr.generator.core.GenerationOptions
import org.kontr.generator.core.GeneratorFacade
import org.kontr.generator.postman.PostmanParser
import java.io.File
import java.io.File.separator

/**
 * @author Domingo Gomez
 */
class CollectionGenerationTest {
    private val generatorFacade: GeneratorFacade = GeneratorFacade(parser = PostmanParser())
    @Test
    fun `test generate weather api collection`() {
        val fileName = "Collection"
        val collectionPathName = javaClass.getResource("weather.api.json")?.file!!
        val packageName = "org.example.generated"
        val outputPathRootName = "target/generated-test-sources/postman"
        val outputFile =
            File(outputPathRootName + separator + packageName.replace(".", separator) + separator + fileName + ".kt")

        if (File(outputPathRootName).exists()) {
            File(outputPathRootName).deleteRecursively()
        }

        generatorFacade.generateFromFileToFile(collectionPathName, outputPathRootName, GenerationOptions(fileName = fileName, packageName = packageName))
        assertThat(outputFile.exists()).isTrue()
        outputFile.inputStream().use { it.copyTo(System.out) }
        // TODO improve to have good assertions but for now this is better than nothing to know if output changes
        assertThat(outputFile.readText()).isEqualTo(
            "package org.example.generated\n" +
                    "\n" +
                    "import kotlin.Long\n" +
                    "import kotlin.String\n" +
                    "import org.example.generated.Env.baseUrl\n" +
                    "import org.example.generated.Env.token\n" +
                    "import org.kontr.dsl.CollectionDsl\n" +
                    "import org.kontr.dsl.ResponseDsl\n" +
                    "import org.kontr.dsl.collection\n" +
                    "\n" +
                    "public data object Env {\n" +
                    "  public val baseUrl: String = \"\"\n" +
                    "\n" +
                    "  public val token: String = \"\"\n" +
                    "}\n" +
                    "\n" +
                    "public object WeatherAPI {\n" +
                    "  public fun CollectionDsl.`Get Current Weather`(city: String = \"San Francisco\"): ResponseDsl =\n" +
                    "      get(\"\${baseUrl}/current?city=\${city}\"){\n" +
                    "      onResponse{ healthy }\n" +
                    "  }\n" +
                    "\n" +
                    "  public fun CollectionDsl.`Get Forecast`(daysAhead: Long = 5): ResponseDsl =\n" +
                    "      get(\"\${baseUrl}/forecast?daysAhead=\${daysAhead}\"){\n" +
                    "      onResponse{ healthy }\n" +
                    "  }\n" +
                    "\n" +
                    "  public fun CollectionDsl.`Get Historical Weather Data`(): ResponseDsl = get(\"\${baseUrl}/history\"){\n" +
                    "      onResponse{ healthy }\n" +
                    "  }\n" +
                    "\n" +
                    "  public fun CollectionDsl.`Update Weather Information`(cityId: Long = 1_542, date: String =\n" +
                    "      \"2024-07-01T00:00:00Z\"): ResponseDsl = put(\"\${baseUrl}/weather/\${cityId}/\${date}\"){\n" +
                    "      header(\"Authorization\", \"Bearer \${token}\")\n" +
                    "      body = \"\"\"{'temperature': 25, 'conditions': 'Partly Cloudy'}\"\"\"\n" +
                    "      onResponse{ healthy }\n" +
                    "  }\n" +
                    "\n" +
                    "  public fun CollectionDsl.`Delete Weather Data`(): ResponseDsl = delete(\"\${baseUrl}/weather\"){\n" +
                    "      header(\"Authorization\", \"Bearer \${token}\")\n" +
                    "      onResponse{ healthy }\n" +
                    "  }\n" +
                    "\n" +
                    "  public fun runCollection0() {\n" +
                    "    val c = collection{\n" +
                    "      `Get Current Weather`()\n" +
                    "      `Get Forecast`()\n" +
                    "      `Get Historical Weather Data`()\n" +
                    "      `Update Weather Information`()\n" +
                    "      `Delete Weather Data`()\n" +
                    "    }\n" +
                    "  }\n" +
                    "}\n"
        )
    }
}