package com.wordnote.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.wordnote.app.R
import com.wordnote.app.data.WordDatabase
import com.wordnote.app.util.compatOverridePendingTransition
import com.wordnote.app.util.compatOverridePendingTransitionClose
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import org.json.JSONObject

class SettingsActivity : AppCompatActivity() {

    private lateinit var darkModeSwitch: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupToolbar()
        setupVersion()
        setupDarkMode()
        setupBackupRestore()
    }

    private var updateDialogShown = false

    private fun setupVersion() {
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val versionText = findViewById<android.widget.TextView>(R.id.versionText)
            versionText.text = packageInfo.versionName ?: "1.0"

            versionText.setOnClickListener {
                updateDialogShown = false
                checkForUpdate()
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun checkForUpdate() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val packageInfo = packageManager.getPackageInfo(packageName, 0)
                val currentVersionName = packageInfo.versionName ?: "1.0"

                Toast.makeText(this@SettingsActivity, "正在检查更新...", Toast.LENGTH_SHORT).show()

                val updateInfo = withContext(Dispatchers.IO) {
                    com.wordnote.app.util.UpdateChecker.checkForUpdate(currentVersionName)
                }

                if (updateInfo != null) {
                    showUpdateDialog(updateInfo)
                } else {
                    Toast.makeText(this@SettingsActivity, "当前已是最新版本", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@SettingsActivity, "检查失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showUpdateDialog(updateInfo: com.wordnote.app.util.UpdateChecker.UpdateInfo) {
        if (updateDialogShown) return
        updateDialogShown = true

        MaterialAlertDialogBuilder(this)
            .setTitle("发现新版本 ${updateInfo.versionName}")
            .setMessage(updateInfo.body.ifBlank { "有新版本可用，是否更新？" })
            .setPositiveButton("更新") { _, _ ->
                startUpdate(updateInfo)
            }
            .setNegativeButton("稍后") { _, _ ->
                updateDialogShown = false
            }
            .setOnCancelListener {
                updateDialogShown = false
            }
            .show()
    }

    private fun startUpdate(updateInfo: com.wordnote.app.util.UpdateChecker.UpdateInfo) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                com.wordnote.app.util.UpdateChecker.downloadAndInstall(this@SettingsActivity, updateInfo) { }
                Toast.makeText(this@SettingsActivity, "下载完成，请安装", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                MaterialAlertDialogBuilder(this@SettingsActivity)
                    .setTitle("下载失败")
                    .setMessage("错误信息：\n${e.message}\n\n请检查网络连接后重试")
                    .setPositiveButton("确定", null)
                    .show()
            }
        }
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
            showBackupManager()
        }

        findViewById<LinearLayout>(R.id.recycleBinButton).setOnClickListener {
            startActivity(Intent(this, RecycleBinActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        findViewById<LinearLayout>(R.id.statisticsButton).setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        findViewById<LinearLayout>(R.id.quizButton).setOnClickListener {
            startActivity(Intent(this, QuizSetupActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        findViewById<LinearLayout>(R.id.changelogButton).setOnClickListener {
            startActivity(Intent(this, ChangelogActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    // ==================== Unified Backup Manager ====================

    private fun showBackupManager() {
        val dialog = BottomSheetDialog(this, R.style.Theme_WordNoteApp_BottomSheet)
        val sheetView = layoutInflater.inflate(R.layout.sheet_backup_manager, null)

        val fileList = sheetView.findViewById<RecyclerView>(R.id.backupFileList)
        val emptyText = sheetView.findViewById<TextView>(R.id.emptyText)
        val newBackupButton = sheetView.findViewById<com.google.android.material.button.MaterialButton>(R.id.newBackupButton)
        val cancelButton = sheetView.findViewById<com.google.android.material.button.MaterialButton>(R.id.cancelButton)

        fileList.layoutManager = LinearLayoutManager(this)

        newBackupButton.setOnClickListener {
            dialog.dismiss()
            backupDatabase()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(sheetView)
        dialog.show()

        loadBackupFiles(fileList, emptyText)
    }

    private fun loadBackupFiles(fileList: RecyclerView, emptyText: TextView) {
        val backupDir = File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS), "WordNoteBackup")

        CoroutineScope(Dispatchers.IO).launch {
            val dbFiles = if (backupDir.exists()) {
                backupDir.listFiles()?.filter { it.name.endsWith(".db") }?.sortedByDescending { it.lastModified() } ?: emptyList()
            } else {
                emptyList()
            }

            withContext(Dispatchers.Main) {
                if (dbFiles.isEmpty()) {
                    fileList.visibility = View.GONE
                    emptyText.visibility = View.VISIBLE
                } else {
                    fileList.visibility = View.VISIBLE
                    emptyText.visibility = View.GONE
                    fileList.adapter = BackupFileAdapter(
                        files = dbFiles,
                        onRestore = { file -> confirmRestoreFile(file) },
                        onRename = { file -> showRenameDialog(file, fileList, emptyText) },
                        onDelete = { file -> confirmDeleteFile(file, fileList, emptyText) }
                    )
                }
            }
        }
    }

    private fun showRenameDialog(file: File, fileList: RecyclerView, emptyText: TextView) {
        val input = EditText(this).apply {
            hint = "输入新文件名"
            setText(file.nameWithoutExtension)
            setSelection(file.nameWithoutExtension.length)
            setPadding(48, 32, 48, 32)
        }

        MaterialAlertDialogBuilder(this, R.style.Theme_WordNoteApp_Dialog)
            .setTitle("重命名备份")
            .setView(input)
            .setPositiveButton("确定") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isBlank()) {
                    Toast.makeText(this, "文件名不能为空", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (newName == file.nameWithoutExtension) return@setPositiveButton

                val newFile = File(file.parent, "$newName.db")
                if (newFile.exists()) {
                    Toast.makeText(this, "已存在同名文件", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Rename main db file
                        file.renameTo(newFile)
                        // Rename associated files
                        File(file.path + "-wal").let { if (it.exists()) it.renameTo(File(newFile.path + "-wal")) }
                        File(file.path + "-shm").let { if (it.exists()) it.renameTo(File(newFile.path + "-shm")) }
                        File(file.path.replace(".db", ".meta")).let { if (it.exists()) it.renameTo(File(newFile.path.replace(".db", ".meta"))) }

                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@SettingsActivity, "已重命名", Toast.LENGTH_SHORT).show()
                            loadBackupFiles(fileList, emptyText)
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@SettingsActivity, "重命名失败: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun confirmDeleteFile(file: File, fileList: RecyclerView, emptyText: TextView) {
        MaterialAlertDialogBuilder(this, R.style.Theme_WordNoteApp_Dialog)
            .setTitle("删除备份")
            .setMessage("确定要删除 ${file.name} 吗？")
            .setPositiveButton("删除") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        file.delete()
                        File(file.path + "-wal").delete()
                        File(file.path + "-shm").delete()
                        File(file.path.replace(".db", ".meta")).delete()

                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@SettingsActivity, "已删除", Toast.LENGTH_SHORT).show()
                            loadBackupFiles(fileList, emptyText)
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@SettingsActivity, "删除失败: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun confirmRestoreFile(backupFile: File) {
        if (!backupFile.exists()) {
            Toast.makeText(this, "备份文件不存在", Toast.LENGTH_SHORT).show()
            return
        }

        MaterialAlertDialogBuilder(this, R.style.Theme_WordNoteApp_Dialog)
            .setTitle("确认恢复")
            .setMessage("恢复将覆盖当前所有数据，确定继续吗？\n\n文件：${backupFile.name}")
            .setPositiveButton("恢复") { _, _ ->
                restoreDatabase(backupFile)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun backupDatabase() {
        val dbFile = getDatabasePath("word_database")
        if (!dbFile.exists()) {
            Toast.makeText(this, "数据库不存在", Toast.LENGTH_SHORT).show()
            return
        }

        val backupDir = File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS), "WordNoteBackup")
        if (!backupDir.exists()) backupDir.mkdirs()

        val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
        val backupFile = File(backupDir, "wordnote_$timestamp.db")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                dbFile.copyTo(backupFile, overwrite = true)
                val walFile = File(dbFile.path + "-wal")
                if (walFile.exists()) {
                    walFile.copyTo(File(backupFile.path + "-wal"), overwrite = true)
                }
                val shmFile = File(dbFile.path + "-shm")
                if (shmFile.exists()) {
                    shmFile.copyTo(File(backupFile.path + "-shm"), overwrite = true)
                }

                val metaFile = File(backupDir, "wordnote_${timestamp}.meta")
                val meta = JSONObject().apply {
                    put("version", 6)
                    put("timestamp", System.currentTimeMillis())
                    put("appVersion", "1.1")
                }
                metaFile.writeText(meta.toString())

                withContext(Dispatchers.Main) {
                    MaterialAlertDialogBuilder(this@SettingsActivity, R.style.Theme_WordNoteApp_Dialog)
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

    private fun restoreDatabase(backupFile: File) {
        val dbFile = getDatabasePath("word_database")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (!backupFile.exists()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SettingsActivity, "备份文件不存在", Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }

                WordDatabase.clearInstance()

                backupFile.copyTo(dbFile, overwrite = true)
                val walFile = File(backupFile.path + "-wal")
                if (walFile.exists()) {
                    walFile.copyTo(File(dbFile.path + "-wal"), overwrite = true)
                } else {
                    File(dbFile.path + "-wal").delete()
                }
                val shmFile = File(backupFile.path + "-shm")
                if (shmFile.exists()) {
                    shmFile.copyTo(File(dbFile.path + "-shm"), overwrite = true)
                } else {
                    File(dbFile.path + "-shm").delete()
                }
                File(dbFile.path + "-journal").delete()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SettingsActivity, "恢复成功，重启应用生效", Toast.LENGTH_LONG).show()
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

    // ==================== Backup File Adapter ====================

    private class BackupFileAdapter(
        private val files: List<File>,
        private val onRestore: (File) -> Unit,
        private val onRename: (File) -> Unit,
        private val onDelete: (File) -> Unit
    ) : RecyclerView.Adapter<BackupFileAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val fileNameText: TextView = itemView.findViewById(R.id.fileNameText)
            val fileSizeText: TextView = itemView.findViewById(R.id.fileSizeText)
            val fileDateText: TextView = itemView.findViewById(R.id.fileDateText)
            val renameButton: ImageView = itemView.findViewById(R.id.renameButton)
            val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_backup_file, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val file = files[position]
            holder.fileNameText.text = file.name

            val sizeKB = file.length() / 1024
            val sizeText = if (sizeKB > 1024) "${sizeKB / 1024}MB" else "${sizeKB}KB"
            holder.fileSizeText.text = sizeText

            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            holder.fileDateText.text = sdf.format(java.util.Date(file.lastModified()))

            holder.itemView.setOnClickListener { onRestore(file) }
            holder.renameButton.setOnClickListener { onRename(file) }
            holder.deleteButton.setOnClickListener { onDelete(file) }
        }

        override fun getItemCount() = files.size
    }
}
