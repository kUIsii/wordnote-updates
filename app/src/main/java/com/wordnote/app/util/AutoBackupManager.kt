package com.wordnote.app.util

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AutoBackupManager(private val context: Context) {

    companion object {
        private const val PREF_NAME = "backup_settings"
        private const val PREF_LAST_BACKUP = "last_auto_backup_time"
        private const val PREF_AUTO_BACKUP_ENABLED = "auto_backup_enabled"
        private const val BACKUP_INTERVAL = 24 * 60 * 60 * 1000L // 24 hours
        private const val MAX_AUTO_BACKUPS = 7
    }

    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun isAutoBackupEnabled(): Boolean {
        return prefs.getBoolean(PREF_AUTO_BACKUP_ENABLED, true)
    }

    fun setAutoBackupEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(PREF_AUTO_BACKUP_ENABLED, enabled).apply()
    }

    fun shouldAutoBackup(): Boolean {
        if (!isAutoBackupEnabled()) return false
        val lastBackup = prefs.getLong(PREF_LAST_BACKUP, 0)
        return System.currentTimeMillis() - lastBackup > BACKUP_INTERVAL
    }

    fun performAutoBackup(): Boolean {
        return try {
            val dbFile = context.getDatabasePath("word_database")
            if (!dbFile.exists()) return false

            val backupDir = getAutoBackupDir()
            if (!backupDir.exists()) backupDir.mkdirs()

            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val fileName = "auto_backup_${dateFormat.format(Date())}.db"
            val backupFile = File(backupDir, fileName)

            dbFile.copyTo(backupFile, overwrite = true)

            // Copy WAL and SHM if exist
            val walFile = File(dbFile.path + "-wal")
            if (walFile.exists()) walFile.copyTo(File(backupFile.path + "-wal"), overwrite = true)
            val shmFile = File(dbFile.path + "-shm")
            if (shmFile.exists()) shmFile.copyTo(File(backupFile.path + "-shm"), overwrite = true)

            // Update last backup time
            prefs.edit().putLong(PREF_LAST_BACKUP, System.currentTimeMillis()).apply()

            // Clean old auto backups
            cleanOldBackups(backupDir)

            android.util.Log.d("AutoBackupManager", "Auto backup created: $fileName")
            true
        } catch (e: Exception) {
            android.util.Log.e("AutoBackupManager", "Auto backup failed: ${e.message}")
            false
        }
    }

    fun getAutoBackupDir(): File {
        return File(context.getExternalFilesDir(null), "auto_backups")
    }

    fun getAutoBackups(): List<File> {
        val dir = getAutoBackupDir()
        return if (dir.exists()) {
            dir.listFiles { file -> file.name.endsWith(".db") }
                ?.sortedByDescending { it.lastModified() }
                ?: emptyList()
        } else {
            emptyList()
        }
    }

    private fun cleanOldBackups(dir: File) {
        val backups = dir.listFiles { file -> file.name.startsWith("auto_backup_") && file.name.endsWith(".db") }
            ?.sortedByDescending { it.lastModified() }
            ?: return

        if (backups.size > MAX_AUTO_BACKUPS) {
            backups.drop(MAX_AUTO_BACKUPS).forEach { file ->
                file.delete()
                File(file.path + "-wal").delete()
                File(file.path + "-shm").delete()
            }
        }
    }
}
