package org.kontr.generator.core

import com.squareup.kotlinpoet.*
import org.kontr.dsl.CollectionDsl
import org.kontr.dsl.Configuration.defaultOnResponseAssertion
import org.kontr.dsl.ResponseDsl

data class GenerationOptions(
    val nestedObjects: Boolean = false,
    val addRunCollection: Boolean = true,
    val envName: String = "Env",
    val addEnv: Boolean = true,
    val packageName: String = "org.example.company",
    val fileName: String = "Collection",
) {
    constructor(formData: Map<String, String>) : this(
        nestedObjects = formData["nestedObjects"] == "on",
        addRunCollection = formData["addRunCollection"] == "on",
        envName = formData["envName"] ?: "Env",
        addEnv = formData["addEnv"] == "on",
        packageName = formData["packageName"] ?: "org.example.company",
        fileName = formData["fileName"] ?: "Collection",
    )
}

/**
 * This class is stateful, so instantiate it everytime to avoid cross-generation issues
 * @author Domingo Gomez
 */
class Generator(private val options: GenerationOptions = GenerationOptions()) {
    private val variableSet = mutableSetOf<String>()
    private var runCollectionIndex = 0
    fun generate(
        collection: GeneratorCollection,
    ): FileSpec {
        val fileSpecBuilder = FileSpec.builder(options.packageName, options.fileName)
        val collectionFunctionBuilder = TypeSpec.objectBuilder(collection.name)

        generateNestedItems(collection.items, collectionFunctionBuilder)
        //imports added after generateNestedItems() populates variableSet
        fileSpecBuilder.addImport("org.kontr.dsl", "collection")
        if (options.addEnv) {
            fileSpecBuilder.addType(generateEnvClass())
            variableSet.forEach { variable ->
                fileSpecBuilder.addImport("${options.packageName}.${options.envName}", variable)
            }
        }

        fileSpecBuilder.addType(collectionFunctionBuilder.build())
        return fileSpecBuilder.build()
    }

    private fun generateNestedItems(items: List<Item>, parentBuilder: TypeSpec.Builder) {
        val functionNames: List<String> = mutableListOf()

        items.forEach { item ->
            if (item.items != null) {
                if (options.nestedObjects) {
                    val nestedObjectBuilder = TypeSpec.objectBuilder(item.name.toClassName())
                    generateNestedItems(item.items, nestedObjectBuilder)
                    parentBuilder.addType(nestedObjectBuilder.build())
                } else {
                    generateNestedItems(item.items, parentBuilder)
                }
            }
            if (item.request != null) {
                parentBuilder.addFunction(getRequestBlock(item.name, item.request))
                functionNames.addLast(item.name)
            }
        }
        if (options.addRunCollection) {
            addRunCollectionMethod(functionNames, parentBuilder)
        }
    }

    private fun addRunCollectionMethod(
        functionNames: List<String>,
        parentBuilder: TypeSpec.Builder
    ) {
        if (functionNames.isNotEmpty()) {
            val block = CodeBlock.builder().add("val c = collection{\n").indent()
            functionNames.forEach {
                block.add("`${it.allowedFunName()}`()\n")
            }
            block.unindent().add("}").build()

            parentBuilder.addFunction(
                FunSpec.builder("runCollection${runCollectionIndex++}").addCode(block.build()).build()
            )
        }
    }

    private fun getRequestBlock(name: String, request: Request): FunSpec {
        val requestFunction = FunSpec.builder(name.allowedFunName())
            .receiver(CollectionDsl::class.java)
            .returns(ResponseDsl::class)
            .addCode(
                CodeBlock.of(
                    "return ${request.method.name.lowercase()}(\"${replaceAllVars(request)}\")"
                )
            )

        request.url.variables.forEach { (key, value) ->
            val typedValue = variableWithType(value)
            requestFunction.addParameter(buildTypedValue(key, typedValue))
        }
        request.url.query.forEach { (key, value) ->
            val typedValue = variableWithType(value)
            requestFunction.addParameter(buildTypedValue(key, typedValue))
        }

        requestFunction.addStatement("{⇥⇥")
        request.header.forEach { (key, value) ->
            requestFunction.addStatement(
                "header(\"%L\", \"%L\")",
                replaceEnvVariables(key),
                replaceEnvVariables(value)
            )
        }

        request.body?.let {
            requestFunction.addStatement("body = %P", replaceEnvVariables(it))
        }
        requestFunction.addStatement(generateOnResponse())
        requestFunction.addCode(CodeBlock.of("⇤⇤}"))

        return requestFunction.build()
    }

    private fun generateOnResponse(): String = defaultOnResponseAssertion ?: ""

    private fun buildTypedValue(key: String, typedValue: Any) =
        ParameterSpec.builder(key, typedValue::class)
            .defaultValue(
                when (typedValue) {
                    is Long -> "%L"
                    else -> "%S" // defaults unrecognised types to String
                }, typedValue
            )
            .build()

    private fun replaceAllVars(request: Request): String {
        var input = replaceEnvVariables(request.url.raw)
        input = replaceUriVariables(input, request.url.variables.keys)
        input = replaceQueryParams(input, request.url.query)
        return input
    }

    private fun generateEnvClass(): TypeSpec {
        val properties = variableSet.mapNotNull { variable ->
            PropertySpec.builder(variable, String::class)
                .initializer("%S", "")
                .build()
        }
        val envClass = TypeSpec.objectBuilder(options.envName)
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

    private fun String.allowedFunName(): String = this.replace("[.:\\\\/\\[\\]<>]".toRegex(), " ")
}


