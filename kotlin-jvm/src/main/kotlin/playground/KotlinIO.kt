@file:Suppress("PackageDirectoryMismatch")

package playground.kotlinio

import java.io.File
import playground.shouldBe
import playground.shouldHaveSameElementsAs

/**
 * Kotlin/IO - Kotlin IO API
 *
 * - [Kotlin Documentation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/)
 */

fun main() {
    println()
    println("# Kotlin IO - API for working with files and streams")

    // FileTreeWalk
    val directory = loadReferenceFile("references/kotlinio")

    run {
        val actual = directory.walkTopDown().map { file -> file.name }.toList()
        val expected =  listOf("kotlinio", "nested-dir", "tada.txt", "long-file.txt", ".hidden.txt", "hello.txt", )
        actual shouldHaveSameElementsAs expected
    }

    run {
        val actual = directory.walkBottomUp().map { file -> file.name }.toList()
        val expected = listOf(".hidden.txt", "hello.txt", "tada.txt", "nested-dir", "long-file.txt", "kotlinio")
        actual shouldHaveSameElementsAs expected
    }

    run {
        val actual = directory.walk(direction = FileWalkDirection.BOTTOM_UP).map { file -> file.name }.toList()
        val expected =  listOf(".hidden.txt", "hello.txt", "tada.txt", "nested-dir", "long-file.txt", "kotlinio")
        actual shouldHaveSameElementsAs expected
    }

    run {
        val actual = directory.walkBottomUp().maxDepth(1).map { file -> file.name }.toList()
        val expected = listOf(".hidden.txt", "hello.txt", "nested-dir", "long-file.txt", "kotlinio")
        actual shouldHaveSameElementsAs expected
    }

    run {
        val actual = directory.walkBottomUp().filter { !it.isDirectory }.map { file -> file.name }.toList()
        val expected = listOf(".hidden.txt", "hello.txt", "tada.txt", "long-file.txt")
        actual shouldHaveSameElementsAs expected
    }

    // File
    val helloTxt = loadReferenceFile("references/kotlinio/hello.txt")

    helloTxt.extension shouldBe "txt"
    helloTxt.nameWithoutExtension shouldBe "hello"

    helloTxt.readLines() shouldBe listOf("Hello World!", "")

    helloTxt.useLines { lines ->
        val content = mutableListOf<String>()

        lines.forEach { line -> if (line.isNotBlank()) content.add(line) }
        TestFileContent(content)
    } shouldBe TestFileContent(listOf("Hello World!"))

    val newDir = createTempDir(prefix = "new", suffix = "dir", directory = directory)
    val newFile = createTempFile(prefix = "new", directory = newDir)

    newFile.writeText("Hi, I'm a new file")
    newFile.readLines() shouldBe listOf("Hi, I'm a new file")

    newFile.appendText("\nand this is a new line")
    newFile.readLines() shouldBe listOf("Hi, I'm a new file", "and this is a new line")

    newDir.deleteRecursively()
    newFile.exists() shouldBe false
    newDir.exists() shouldBe false
}

internal data class TestFileContent(val content: List<String>)

private fun loadReferenceFile(path: String): File {
    return File(
        Unit.javaClass.classLoader
            .getResource(path)!!.file
    )
}
