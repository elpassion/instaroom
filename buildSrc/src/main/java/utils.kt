@file:JvmName("Utils")
@file:Suppress("unused")

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

fun getHeadCommitHash() = runCommand("git rev-parse --short HEAD")?.trim()

fun runCommand(command: String, workingDir: File? = null, timeout: Long = 3L) = try {
    val parts = command.split("\\s".toRegex())
    val proc = ProcessBuilder(*parts.toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()
    proc.waitFor(timeout, TimeUnit.SECONDS)
    proc.inputStream.bufferedReader().readText()
} catch (e: IOException) {
    e.printStackTrace()
    null
}

