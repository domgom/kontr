package org.kontr.cli

/**
 * @author Domingo Gomez
 */
//TODO migrate kotlinx.cli (unmaintained) to another cli tool.
import KONTR_VERSION
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import org.kontr.generator.core.GeneratorFacade
import org.kontr.generator.postman.PostmanParser
import java.io.BufferedReader
import java.io.InputStreamReader


private const val programName = "kontr-cli"
private val generatorFacade = GeneratorFacade(parser = PostmanParser())

class Cli {
    companion object {
        @JvmStatic
        @OptIn(ExperimentalCli::class)
        fun main(args: Array<String>) {
            val parser = ArgParser(programName = programName, strictSubcommandOptionsOrder = true)
            val exit by parser.option(ArgType.Boolean, shortName = "e", description = "Exit application")
            val version by parser.option(ArgType.Boolean, shortName = "v", description = "Display version information")
            val collection by parser.option(ArgType.String, shortName = "c", description = "Select a Collection")
            val generatorPostmanSubcommand = GeneratorPostmanSubcommand()
            parser.subcommands(generatorPostmanSubcommand)
            parser.parse(args)

            exit?.let {
                return
            }
            version?.let {
                doVersion()
                return
            }
            collection?.let {
                doCollection(it)
                return
            }
            if (generatorPostmanSubcommand.isPopulated()) {
                doGeneratorPostman(generatorPostmanSubcommand)
                return
            }
            // Setup shutdown hook for exit
            Runtime.getRuntime().addShutdownHook(Thread {
                println("Bye...")
                // Cleanup or finalize any resources if needed
            })
            // Listen for keyboard events until exit command is received
            val reader = BufferedReader(InputStreamReader(System.`in`))

            if (args.isEmpty()) {
                var input: String?
                while (true) {
                    try {
                        println(
                            """Enter command: 
                    | collection, c -> Select a Collection { String } 
                    |       Eg: file name "MyCollection.kt": "c MyCollectionKt"
                    | exit, e -> Exit application                  
                """.trimMargin()
                        )
                        var captured: List<String>? = null
                        operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text).also {
                            if (captured == null) {
                                captured = find(text)?.destructured?.toList()
                            }
                        }

                        input = reader.readLine()?.trim()
                        when (input) {
                            null -> Unit
                            "exit", "e" -> break
                            "version", "v" -> {
                                doVersion()
                                break
                            }

                            in Regex("^collection\\s+(\\S+)\$"), in Regex("^c\\s+(\\S+)\$") -> {
                                if (captured == null || captured?.size!! > 1) {
                                    println("Invalid collection name")
                                } else {
                                    doCollection(captured!!.first())
                                }
                            }

                            else -> println("Invalid command format")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }

        }

        private fun doVersion() {
            println("kontr-cli version $KONTR_VERSION")
        }

        private fun doCollection(collection: String) {
            println("Running $collection...")
            // val collection = "FoobarKt"
            val clazz = Class.forName(collection).kotlin
            val main = clazz.java.methods.find { it.name == "main" }
            main?.invoke(null)
            println("$collection finished")
        }

        private fun doGeneratorPostman(generatorPostmanSubcommand: GeneratorPostmanSubcommand) {
            with(generatorPostmanSubcommand) {
                generatorFacade.generateFromFileToFile(
                    inputPath = inputPath,
                    outputPath = outputDir,
                    packageName = packageName,
                    fileName = fileName
                )
            }
        }

        @OptIn(kotlinx.cli.ExperimentalCli::class)
        class GeneratorPostmanSubcommand : Subcommand("gp", "Postman Generator") {
            val inputPath by argument(ArgType.String, description = "Input collection path")
            val outputDir by argument(ArgType.String, description = "Output directory")
            val packageName by argument(
                ArgType.String,
                description = "Package name, remember to use '.' separator, eg: com.example.generated"
            )
            val fileName by argument(ArgType.String, description = "Output fileName")
            var populated = false
            override fun execute() {
                populated = true
                println(
                    "Loading file $inputPath, generated DSL file will be " +
                            "$outputDir/${packageName.replace(".", "/")}/$fileName.kt"
                )
            }

            fun isPopulated(): Boolean = populated
        }
    }


}



