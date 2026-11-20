package com.mygame

import com.badlogic.gdx.Gdx
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Main {
    private var logFile: File? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    fun init(baseDir: File?) {
        try {
            val dir = baseDir ?: File(Gdx.files.externalStoragePath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            logFile = File(dir, "logs.txt")
            log("INFO", "=== GameLogger initialized at ${logFile?.absolutePath} ===")
            installUncaughtExceptionHandler()
        } catch (e: Exception) {
            Gdx.app?.error("GameLogger", "Failed to init log file", e)
        }
    }

    fun log(level: String, message: String) {
        val line = "[${dateFormat.format(Date())}] [$level] $message\n"
        try {
            Gdx.app?.log("GameLogger", "[$level] $message")
            logFile?.appendText(line, Charsets.UTF_8)
        } catch (e: Exception) {
            Gdx.app?.error("GameLogger", "Failed to write log line", e)
        }
    }

    fun error(message: String, throwable: Throwable? = null) {
        val details = if (throwable != null) {
            "$message\n${throwable.stackTraceToString()}"
        } else {
            message
        }
        log("ERROR", details)
    }

    private fun installUncaughtExceptionHandler() {
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                log(
                    "FATAL",
                    "Uncaught exception on thread '${thread.name}':\n${throwable.stackTraceToString()}"
                )
            } catch (_: Exception) {
                // Ігноруємо помилки логування під час аварійного завершення
            } finally {
                previousHandler?.uncaughtException(thread, throwable)
            }
        }
    }
}
