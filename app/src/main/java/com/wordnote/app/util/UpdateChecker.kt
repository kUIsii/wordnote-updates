package com.wordnote.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.URL

object UpdateChecker {

    private const val GITHUB_OWNER = "kUIsii"
    private const val GITHUB_REPO = "wordnote-updates"
    private const val GITHUB_TOKEN = "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"

    data class UpdateInfo(
        val versionName: String,
        val versionCode: Int,
        val downloadUrl: String,
        val body: String
    )

    suspend fun checkForUpdate(currentVersionName: String): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.github.com/repos/$GITHUB_OWNER/$GITHUB_REPO/releases/latest")
            val connection = url.openConnection().apply {
                setRequestProperty("Authorization", "token $GITHUB_TOKEN")
                setRequestProperty("Accept", "application/vnd.github.v3+json")
            }

            val response = connection.inputStream.bufferedReader().readText()
            val json = JSONObject(response)

            val tagName = json.getString("tag_name")
            val remoteVersionName = tagName.removePrefix("v")
            val body = json.optString("body", "")

            // Compare versions
            if (compareVersions(remoteVersionName, currentVersionName) <= 0) {
                return@withContext null
            }

            val assets = json.getJSONArray("assets")
            if (assets.length() == 0) return@withContext null

            val apkUrl = assets.getJSONObject(0).getString("browser_download_url")

            UpdateInfo(
                versionName = remoteVersionName,
                versionCode = 0,
                downloadUrl = apkUrl,
                body = body
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun compareVersions(v1: String, v2: String): Int {
        val parts1 = v1.split(".").map { it.toIntOrNull() ?: 0 }
        val parts2 = v2.split(".").map { it.toIntOrNull() ?: 0 }
        val maxParts = maxOf(parts1.size, parts2.size)

        for (i in 0 until maxParts) {
            val p1 = parts1.getOrElse(i) { 0 }
            val p2 = parts2.getOrElse(i) { 0 }
            if (p1 > p2) return 1
            if (p1 < p2) return -1
        }
        return 0
    }

    suspend fun downloadAndInstall(context: Context, updateInfo: UpdateInfo, onProgress: (Int) -> Unit) = withContext(Dispatchers.IO) {
        try {
            val url = URL(updateInfo.downloadUrl)
            val connection = url.openConnection().apply {
                setRequestProperty("Authorization", "token $GITHUB_TOKEN")
            }

            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val apkFile = File(downloadsDir, "wordnote_update.apk")

            val inputStream = connection.inputStream
            val outputStream = apkFile.outputStream()

            val totalSize = connection.contentLength
            var downloadedSize = 0
            val buffer = ByteArray(8192)

            inputStream.use { input ->
                outputStream.use { output ->
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedSize += bytesRead
                        if (totalSize > 0) {
                            withContext(Dispatchers.Main) {
                                onProgress((downloadedSize * 100 / totalSize).toInt())
                            }
                        }
                    }
                }
            }

            withContext(Dispatchers.Main) {
                installApk(context, apkFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun installApk(context: Context, apkFile: File) {
        if (!apkFile.exists()) return

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    apkFile
                ),
                "application/vnd.android.package-archive"
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(intent)
    }
}
