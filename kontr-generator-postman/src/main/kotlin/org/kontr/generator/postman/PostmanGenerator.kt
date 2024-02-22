package org.kontr.generator.postman

import com.squareup.kotlinpoet.*
import org.kontr.dsl.CollectionDsl
import org.kontr.dsl.RequestDsl
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Domingo Gomez
 */
class PostmanGenerator {
    private val variableSet = mutableSetOf<String>()
    fun generate(
        inputPath: String,
        outputPath: String,
        packageName: String = "org.example.generated",
        fileName: String
    ) {
        val file = generateKotlinCodeFromPostmanCollection(Paths.get(inputPath), fileName, packageName)
        file.writeTo(Paths.get(outputPath))
    }

    private fun generateKotlinCodeFromPostmanCollection(
        inputPath: Path,
        fileName: String,
        packageName: String
    ): FileSpec {
        val collection = PostmanParser().parsePostmanCollection(inputPath)
        val fileSpecBuilder = FileSpec.builder(packageName, fileName)
        val collectionFunctionBuilder = TypeSpec.classBuilder(collection.info["name"]?.toClassName() ?: "collection")

        generateNestedItems(collection.items, collectionFunctionBuilder)
        fileSpecBuilder.addType(generateEnvClass())
        //imports added after generateNestedItems() populates variableSet
        fileSpecBuilder.addImport("net.javacrumbs.jsonunit.assertj", "assertThatJson")
        fileSpecBuilder.addImport("org.assertj.core.api.Assertions", "assertThat")
        fileSpecBuilder.addImport("org.kontr.dsl", "collection")
        variableSet.forEach { variable ->
            fileSpecBuilder.addImport("${packageName}.Env", variable)
        }

        fileSpecBuilder.addType(collectionFunctionBuilder.build())
        return fileSpecBuilder.build()
    }

    private fun generateNestedItems(items: List<CollectionItem>, parentBuilder: TypeSpec.Builder) {
        val functionNames: List<String> = mutableListOf()

        items.forEach { item ->
            when {
                item.request != null -> {
                    parentBuilder.addFunction(getRequestBlock(item.name, item.request))
                    functionNames.addLast(item.name)
                }

                item.item != null -> {
                    val nestedClassBuilder = TypeSpec.classBuilder(item.name.toClassName())
                    generateNestedItems(item.item, nestedClassBuilder)
                    parentBuilder.addType(nestedClassBuilder.build())
                }
            }
        }
        addCollectionWithRequests(functionNames, parentBuilder)
    }

    private fun addCollectionWithRequests(
        functionNames: List<String>,
        parentBuilder: TypeSpec.Builder
    ) {
        if (functionNames.isNotEmpty()) {
            val block = CodeBlock.builder().add("val c = collection{\n").indent()
            functionNames.forEach {
                block.add(if (it.contains(" ")) "`$it`()\n" else "$it()\n")
            }
            block.unindent().add("}").build()

            parentBuilder.addFunction(FunSpec.builder("runCollection").addCode(block.build()).build())
        }
    }

    private fun getRequestBlock(name: String, request: Request): FunSpec {
        val requestFunction = FunSpec.builder(name)
            .receiver(CollectionDsl::class.java)
            .returns(RequestDsl::class)
            .addCode(
                CodeBlock.of(
                    "return ${request.method.name.lowercase()}(\"${
                        replaceAllVars(request)
                    }\"){\n"
                )
            )

        request.variables.forEach { (key, value) ->
            val typedValue = variableWithType(value)
            requestFunction.addParameter(
                ParameterSpec.builder(key, typedValue::class)
                    .defaultValue(value)
                    .build()
            )
        }
        request.query.forEach { (key, value) ->
            val typedValue = variableWithType(value)
            requestFunction.addParameter(
                ParameterSpec.builder(key, typedValue::class)
                    .defaultValue(
                        when (typedValue) {
                            is Long -> "%L"
                            else -> "\"%L\"" // defaults unrecognised types to String
                        }, typedValue
                    )
                    .build()
            )
        }

        request.headers.forEach { (key, value) ->
            requestFunction.addStatement("header(%S, %S)", replaceEnvVariables(key), replaceEnvVariables(value))
        }

        if (request.body.isNotBlank()) {
            requestFunction.addStatement("body = %P", replaceEnvVariables(request.body))
        }
        requestFunction.addCode(CodeBlock.of("}"))

        return requestFunction.build()
    }

    private fun replaceAllVars(request: Request): String {
        var input = replaceEnvVariables(request.url)
        input = replaceUriVariables(input, request.variables.keys)
        input = replaceQueryParams(input, request.query)
        return input
    }

    private fun generateEnvClass(): TypeSpec {
        val properties = variableSet.mapNotNull { variable ->
            PropertySpec.builder(variable, String::class)
                .initializer("%S", "")
                .build()
        }
        val envClass = TypeSpec.objectBuilder("Env")
            .addModifiers(KModifier.DATA)
            .addProperties(properties)
            .build()

        return envClass
    }

    private fun replaceEnvVariables(input: String): String {
        var output = input
        val regex = "\\{\\{([^}]*)}}".toRegex()
        regex.findAll(input).forEach { matchResult ->
            val variable = matchResult.groupValues[1]
            val newValue = "\${$variable}"
            output = output.replace("{{${variable}}}", newValue)
            variableSet.add(variable)
        }
        return output
    }

    private fun replaceUriVariables(input: String, variableNames: Set<String>): String {
        var result = input
        variableNames.forEach { variable ->
            result = result.replace(":$variable", "\${$variable}")
        }
        return result
    }

    private fun replaceQueryParams(input: String, queryParams: Map<String, String>): String {
        var result = input
        queryParams.forEach { (key, value) ->
            result = result.replace("$key=$value", "$key=\${$key}")
        }
        return result
    }


    private fun variableWithType(variable: String) = variable.toLongOrNull() ?: variable

    private fun String.toClassName(): String {
        // Ensure the class name is a valid Kotlin identifier
        return this.replace(Regex("[^A-Za-z0-9_]"), "").capitalize()
    }

}
