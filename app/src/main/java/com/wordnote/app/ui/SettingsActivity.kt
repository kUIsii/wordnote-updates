package com.wordnote.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.wordnote.app.R
import com.wordnote.app.data.WordDatabase
import com.wordnote.app.util.compatOverridePendingTransition
import com.wordnote.app.util.compatOverridePendingTransitionClose
import kotlinx.coroutines.*
import java.io.File
import org.json.JSONObject

class SettingsActivity : AppCompatActivity() {

    private lateinit var darkModeSwitch: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupToolbar()
        setupDarkMode()
        setupBackupRestore()
    }

    private fun setupToolbar() {
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
            compatOverridePendingTransitionClose(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun setupDarkMode() {
        darkModeSwitch = findViewById(R.id.darkModeSwitch)
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        darkModeSwitch.isChecked = prefs.getBoolean("dark_mode", false)

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    private fun setupBackupRestore() {
        findViewById<LinearLayout>(R.id.calendarButton).setOnClickListener {
            startActivity(Intent(this, CalendarViewActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        findViewById<LinearLayout>(R.id.backupButton).setOnClickListener {
            backupDatabase()
        }

        findViewById<LinearLayout>(R.id.restoreButton).setOnClickListener {
            confirmRestore()
        }
    }

    private fun backupDatabase() {
        val dbFile = getDatabasePath("word_database")
        if (!dbFile.exists()) {
            Toast.makeText(this, "数据库不存在", Toast.LENGTH_SHORT).show()
            return
        }

        val backupDir = File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS), "WordNoteBackup")
        if (!backupDir.exists()) backupDir.exists() || backupDir.mkdirs()

        val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
        val backupFile = File(backupDir, "wordnote_$timestamp.db")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Copy main db
                dbFile.copyTo(backupFile, overwrite = true)
                // Copy WAL if exists
                val walFile = File(dbFile.path + "-wal")
                if (walFile.exists()) {
                    walFile.copyTo(File(backupFile.path + "-wal"), overwrite = true)
                }
                // Copy SHM if exists
                val shmFile = File(dbFile.path + "-shm")
                if (shmFile.exists()) {
                    shmFile.copyTo(File(backupFile.path + "-shm"), overwrite = true)
                }

                // Write meta file with version info
                val metaFile = File(backupDir, "wordnote_${timestamp}.meta")
                val meta = JSONObject().apply {
                    put("version", 6)
                    put("timestamp", System.currentTimeMillis())
                    put("appVersion", "1.1")
                }
                metaFile.writeText(meta.toString())

                withContext(Dispatchers.Main) {
                    MaterialAlertDialogBuilder(this@SettingsActivity)
                        .setTitle("备份成功")
                        .setMessage("已保存到:\nDownloads/WordNoteBackup/wordnote_$timestamp.db")
                        .setPositiveButton("确定", null)
                        .show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SettingsActivity, "备份失败: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun confirmRestore() {
        val backupDir = File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS), "WordNoteBackup")
        if (!backupDir.exists()) {
            Toast.makeText(this, "备份目录不存在\n请先备份数据", Toast.LENGTH_SHORT).show()
            return
        }

        val files = backupDir.listFiles()
        if (files == null || files.isEmpty()) {
            Toast.makeText(this, "备份目录为空\n请先备份数据", Toast.LENGTH_SHORT).show()
            return
        }

        val dbFiles = files.filter { it.name.endsWith(".db") }.sortedByDescending { it.lastModified() }
        if (dbFiles.isEmpty()) {
            Toast.makeText(this, "没有找到备份文件\n请先备份数据", Toast.LENGTH_SHORT).show()
            return
        }

        val fileNames = dbFiles.map { it.name }.toTypedArray()

        MaterialAlertDialogBuilder(this)
            .setTitle("选择备份文件")
            .setItems(fileNames) { _, which ->
                confirmRestoreFile(dbFiles[which])
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun confirmRestoreFile(backupFile: File) {
        // Verify file exists before proceeding
        if (!backupFile.exists()) {
            Toast.makeText(this, "备份文件不存在", Toast.LENGTH_SHORT).show()
            return
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("确认恢复")
            .setMessage("恢复将覆盖当前所有数据，确定继续吗？\n\n文件：${backupFile.name}")
            .setPositiveButton("恢复") { _, _ ->
                restoreDatabase(backupFile)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun restoreDatabase(backupFile: File) {
        val dbFile = getDatabasePath("word_database")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Verify backup file exists
                if (!backupFile.exists()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SettingsActivity, "备份文件不存在", Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }

                // Close database and clear singleton to avoid stale instance
                WordDatabase.clearInstance()

                // Copy backup over current db
                backupFile.copyTo(dbFile, overwrite = true)
                // Copy WAL
                val walFile = File(backupFile.path + "-wal")
                if (walFile.exists()) {
                    walFile.copyTo(File(dbFile.path + "-wal"), overwrite = true)
                } else {
                    File(dbFile.path + "-wal").delete()
                }
                // Copy SHM
                val shmFile = File(backupFile.path + "-shm")
                if (shmFile.exists()) {
                    shmFile.copyTo(File(dbFile.path + "-shm"), overwrite = true)
                } else {
                    File(dbFile.path + "-shm").delete()
                }
                // Delete journal files that may conflict
                File(dbFile.path + "-journal").delete()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SettingsActivity, "恢复成功，重启应用生效", Toast.LENGTH_LONG).show()
                    // Restart app
                    val intent = packageManager.getLaunchIntentForPackage(packageName)
                    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    Runtime.getRuntime().exit(0)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SettingsActivity, "恢复失败: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
