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
    fun `test generate auth collection`() {
        val fileName = "Collection"
        val collectionPathName = javaClass.getResource("auth.postman_collection.json")?.file!!
        val packageName = "org.example.generated"
        val outputPathRootName = "target/generated-sources/postman"

        // target/generated-sources/PostmanCollection/org/example/generated/Collection.kt
        val outputFile = File(outputPathRootName + separator + packageName.replace(".", separator) + separator + fileName + ".kt")

        assertThat(File(outputPathRootName).exists()).isFalse()
        PostmanGenerator().generate(collectionPathName, outputPathRootName, packageName, fileName)
        assertThat(outputFile.exists()).isTrue()
        outputFile.inputStream().use { it.copyTo(System.out) }
        File(outputPathRootName).deleteRecursively()
    }


    @Test
    fun `test generate commission collection`() {
        val fileName = "Collection"
        val collectionPathName = javaClass.getResource("commission.postman_collection.json")?.file!!
        val packageName = "org.example.generated"
        val outputPathRootName = "src/test/kotlin/postman"

        // target/generated-sources/PostmanCollection/org/example/generated/Collection.kt
        val outputFile = File(outputPathRootName + separator + packageName.replace(".", separator) + separator + fileName + ".kt")

//        assertThat(File(outputPathRootName).exists()).isFalse()
        PostmanGenerator().generate(collectionPathName, outputPathRootName, packageName, fileName)
        assertThat(outputFile.exists()).isTrue()
        outputFile.inputStream().use { it.copyTo(System.out) }
        //File(outputPathRootName).deleteRecursively()
    }
}