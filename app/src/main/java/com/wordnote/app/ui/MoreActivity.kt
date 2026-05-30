package com.wordnote.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wordnote.app.R
import com.wordnote.app.data.Word
import com.wordnote.app.databinding.ActivityMoreBinding
import com.wordnote.app.util.AutoBackupManager
import com.wordnote.app.util.compatOverridePendingTransition
import com.wordnote.app.util.compatOverridePendingTransitionClose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMoreBinding
    private lateinit var viewModel: WordViewModel

    private val exportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let { exportWordsToCSV(it) }
    }

    private val importLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { importWordsFromCSV(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

        binding.backButton.setOnClickListener {
            finish()
            compatOverridePendingTransitionClose(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        binding.calendarButton.setOnClickListener {
            startActivity(Intent(this, CalendarViewActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.statisticsButton.setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.quizButton.setOnClickListener {
            startActivity(Intent(this, QuizSetupActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.sentenceButton.setOnClickListener {
            startActivity(Intent(this, SentenceListActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.categoryButton.setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.exportButton.setOnClickListener {
            val fileName = "wordnote_export_${System.currentTimeMillis()}.csv"
            exportLauncher.launch(fileName)
        }

        binding.importButton.setOnClickListener {
            importLauncher.launch(arrayOf("text/*"))
        }

        binding.backupRestoreButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        updateBackupStatus()
    }

    override fun onResume() {
        super.onResume()
        updateBackupStatus()
    }

    private fun updateBackupStatus() {
        val autoBackupManager = AutoBackupManager(this)
        val backups = autoBackupManager.getAutoBackups()
        val statusText = if (backups.isNotEmpty()) {
            val sdf = java.text.SimpleDateFormat("MM-dd HH:mm", java.util.Locale.getDefault())
            "最近备份: ${sdf.format(java.util.Date(backups.first().lastModified()))}"
        } else {
            "暂无自动备份"
        }
        binding.backupStatusText.text = statusText
    }

    private fun exportWordsToCSV(uri: Uri) {
        lifecycleScope.launch {
            try {
                val words = withContext(Dispatchers.IO) {
                    viewModel.getAllActiveWordsSync()
                }

                val csvContent = StringBuilder()
                // BOM for Excel UTF-8 compatibility
                csvContent.append('\uFEFF')
                csvContent.append("Word,Meaning,Category,Note\n")

                words.forEach { word ->
                    val category = word.categoryId?.let { viewModel.getCategoryById(it)?.name } ?: ""
                    val escapedWord = escapeCSV(word.word)
                    val escapedMeaning = escapeCSV(word.meaning)
                    val escapedCategory = escapeCSV(category)
                    val escapedNote = escapeCSV(word.note ?: "")
                    csvContent.append("$escapedWord,$escapedMeaning,$escapedCategory,$escapedNote\n")
                }

                withContext(Dispatchers.IO) {
                    contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(csvContent.toString().toByteArray(Charsets.UTF_8))
                    }
                }

                Toast.makeText(this@MoreActivity, "成功导出 ${words.size} 个单词", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@MoreActivity, "导出失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun importWordsFromCSV(uri: Uri) {
        lifecycleScope.launch {
            try {
                val lines = withContext(Dispatchers.IO) {
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        inputStream.bufferedReader().readLines()
                    }
                }

                if (lines.isNullOrEmpty()) {
                    Toast.makeText(this@MoreActivity, "文件为空", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Skip header line
                val dataLines = lines.drop(1)
                val categories = withContext(Dispatchers.IO) {
                    viewModel.allCategories.value ?: emptyList()
                }
                val categoriesMap = categories.associateBy { it.name }

                var importedCount = 0
                dataLines.forEach { line ->
                    val parts = parseCSVLine(line)
                    if (parts.size >= 2 && parts[0].isNotBlank()) {
                        val wordText = parts[0].trim()
                        val meaning = parts[1].trim()
                        val categoryName = if (parts.size > 2) parts[2].trim() else ""
                        val note = if (parts.size > 3) parts[3].trim().ifEmpty { null } else null

                        val categoryId = if (categoryName.isNotEmpty()) {
                            categoriesMap[categoryName]?.id
                        } else {
                            null
                        }

                        val word = Word(
                            word = wordText,
                            meaning = meaning,
                            categoryId = categoryId,
                            note = note
                        )
                        withContext(Dispatchers.IO) {
                            viewModel.insertWord(word)
                        }
                        importedCount++
                    }
                }

                Toast.makeText(this@MoreActivity, "成功导入 $importedCount 个单词", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@MoreActivity, "导入失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun escapeCSV(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }

    private fun parseCSVLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false

        for (char in line) {
            when {
                char == '"' -> inQuotes = !inQuotes
                char == ',' && !inQuotes -> {
                    result.add(current.toString())
                    current.clear()
                }
                else -> current.append(char)
            }
        }
        result.add(current.toString())
        return result
    }
}
