import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.kontr.generator.postman.PostmanGenerator
import java.io.File
import java.io.File.separator

/**
 * @author Domingo Gomez
 */
class CollectionGenerationTest {
    @Test
    fun `test generate weather api collection`() {
        val fileName = "Collection"
        val collectionPathName = javaClass.getResource("weather.api.json")?.file!!
        val packageName = "org.example.generated"
        val outputPathRootName = "target/generated-sources/postman"

        val outputFile = File(outputPathRootName + separator + packageName.replace(".", separator) + separator + fileName + ".kt")

        if(File(outputPathRootName).exists()){
            File(outputPathRootName).deleteRecursively()
        }

        PostmanGenerator().generate(collectionPathName, outputPathRootName, packageName, fileName)
        assertThat(outputFile.exists()).isTrue()
        outputFile.inputStream().use { it.copyTo(System.out) }
    }
}