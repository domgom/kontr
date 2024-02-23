import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.kontr.generator.postman.PostmanGenerator
import java.io.File
import java.io.File.separator

/**
 * @author Domingo Gomez
 */
class CollectionGenerationTest {
    private val postmanGenerator: PostmanGenerator = PostmanGenerator()
    @Test
    fun `test generate weather api collection`() {
        val fileName = "Collection"
        val collectionPathName = javaClass.getResource("weather.api.json")?.file!!
        val packageName = "org.example.generated"
        val outputPathRootName = "target/generated-sources/postman"
        val outputFile =
            File(outputPathRootName + separator + packageName.replace(".", separator) + separator + fileName + ".kt")

        if (File(outputPathRootName).exists()) {
            File(outputPathRootName).deleteRecursively()
        }

        postmanGenerator.generate(collectionPathName, outputPathRootName, packageName, fileName)
        assertThat(outputFile.exists()).isTrue()
        outputFile.inputStream().use { it.copyTo(System.out) }
        // TODO improve to have good assertions but for now this is better than nothing to know if output changes
        assertThat(outputFile.readText()).isEqualTo(
            "package org.example.generated\n\n" +
                    "import kotlin.Long\n" +
                    "import kotlin.String\n" +
                    "import org.example.generated.Env.baseUrl\n" +
                    "import org.example.generated.Env.token\n" +
                    "import org.kontr.dsl.CollectionDsl\n" +
                    "import org.kontr.dsl.RequestDsl\n" +
                    "import org.kontr.dsl.collection\n" +
                    "\n" +
                    "public data object Env {\n" +
                    "  public val baseUrl: String = \"\"\n" +
                    "\n" +
                    "  public val token: String = \"\"\n" +
                    "}\n" +
                    "\n" +
                    "public class WeatherAPI {\n" +
                    "  public fun CollectionDsl.`Get Current Weather`(city: String = \"San Francisco\"): RequestDsl =\n" +
                    "      get(\"\${baseUrl}/current?city=\${city}\"){ onResponse{ healthy }\n" +
                    "  }\n" +
                    "\n" +
                    "  public fun CollectionDsl.`Get Forecast`(daysAhead: Long = 5): RequestDsl =\n" +
                    "      get(\"\${baseUrl}/forecast?daysAhead=\${daysAhead}\"){ onResponse{ healthy }\n" +
                    "  }\n" +
                    "\n" +
                    "  public fun CollectionDsl.`Get Historical Weather Data`(): RequestDsl = get(\"\${baseUrl}/history\"){\n" +
                    "      onResponse{ healthy }\n" +
                    "  }\n" +
                    "\n" +
                    "  public fun CollectionDsl.`Update Weather Information`(cityId: Long = 1_542, date: String =\n" +
                    "      \"2024-07-01T00:00:00Z\"): RequestDsl = put(\"\${baseUrl}/weather/\${cityId}/\${date}\"){ onResponse{\n" +
                    "      healthy }\n" +
                    "  header(\"Authorization\", \"Bearer \${token}\")\n" +
                    "  body = \"\"\"{'temperature': 25, 'conditions': 'Partly Cloudy'}\"\"\"\n" +
                    "  }\n" +
                    "\n" +
                    "  public fun CollectionDsl.`Delete Weather Data`(): RequestDsl = delete(\"\${baseUrl}/weather\"){\n" +
                    "      onResponse{ healthy }\n" +
                    "  header(\"Authorization\", \"Bearer \${token}\")\n" +
                    "  }\n" +
                    "\n" +
                    "  public fun runCollection() {\n" +
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