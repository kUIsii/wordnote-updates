package com.wordnote.app.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.graphics.drawable.GradientDrawable
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.lifecycle.ViewModelProvider
import com.wordnote.app.R
import com.wordnote.app.databinding.ActivityAddWordBinding
import com.wordnote.app.data.Category
import com.wordnote.app.data.Word
import com.wordnote.app.data.WordGroup
import com.wordnote.app.data.WordMeaning
import com.wordnote.app.util.compatOverridePendingTransitionClose

class AddWordActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_WORD_ID = "extra_word_id"
    }

    private lateinit var binding: ActivityAddWordBinding
    private lateinit var viewModel: WordViewModel

    private var wordId: Long? = null
    private var categories: List<Category> = emptyList()
    private var groups: List<WordGroup> = emptyList()
    private var existingMeanings: List<WordMeaning> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

        setupToolbar()
        loadWordIfEditing()
        observeCategories()
        observeGroups()
        setupSaveButton()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
            compatOverridePendingTransitionClose(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun loadWordIfEditing() {
        wordId = intent.getLongExtra(EXTRA_WORD_ID, -1).takeIf { it != -1L }
        if (wordId != null) {
            viewModel.getWordById(wordId!!).observe(this) { word ->
                word?.let { populateFields(it) }
            }
            viewModel.getMeaningsForWord(wordId!!).observe(this) { meanings ->
                existingMeanings = meanings
                if (meanings.isNotEmpty() && binding.meaningEditText.text.isNullOrEmpty()) {
                    val meaningText = meanings.joinToString(", ") { it.meaningText }
                    binding.meaningEditText.setText(meaningText)
                }
            }
        }
    }

    private fun populateFields(word: Word) {
        binding.wordEditText.setText(word.word)
        binding.meaningEditText.setText(word.meaning)

        word.categoryId?.let { categoryId ->
            val index = categories.indexOfFirst { it.id == categoryId }
            if (index >= 0) {
                binding.categorySpinner.setSelection(index)
            }
        }

        word.groupId?.let { groupId ->
            // +1 because first item is "无分组"
            val index = groups.indexOfFirst { it.id == groupId } + 1
            if (index >= 0) {
                binding.groupSpinner.setSelection(index)
            }
        }
    }

    private fun observeCategories() {
        viewModel.allCategories.observe(this) { cats ->
            categories = cats
            val categoryNames = cats.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categorySpinner.adapter = adapter
            wordId?.let { loadWordIfEditing() }
        }
    }

    private fun observeGroups() {
        viewModel.allGroups.observe(this) { groupList ->
            groups = groupList
            val groupNames = mutableListOf("无分组")
            groupList.forEach { group ->
                groupNames.add(group.name)
            }
            // Add "新建分组" option
            groupNames.add("+ 新建分组")

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, groupNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.groupSpinner.adapter = adapter

            binding.groupSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                    if (position == groupNames.size - 1 && groupNames[position] == "+ 新建分组") {
                        showCreateGroupDialog()
                    }
                }
                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }

            wordId?.let { loadWordIfEditing() }
        }
    }

    private fun showCreateGroupDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_note_input, null)
        val input = dialogView.findViewById<EditText>(R.id.noteEditText)
        input.hint = "输入分组名称"

        MaterialAlertDialogBuilder(this)
            .setTitle("新建词语分组")
            .setView(dialogView)
            .setPositiveButton("创建") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotBlank()) {
                    viewModel.createGroup(name)
                    Toast.makeText(this, "分组已创建", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消") { _, _ ->
                binding.groupSpinner.setSelection(0)
            }
            .setOnCancelListener {
                binding.groupSpinner.setSelection(0)
            }
            .show()
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            val word = binding.wordEditText.text.toString().trim()
            val meaning = binding.meaningEditText.text.toString().trim()

            if (word.isEmpty()) {
                binding.wordEditText.error = getString(R.string.error_word_empty)
                return@setOnClickListener
            }
            if (meaning.isEmpty()) {
                binding.meaningEditText.error = getString(R.string.error_meaning_empty)
                return@setOnClickListener
            }

            val selectedCategory = if (binding.categorySpinner.selectedItemPosition >= 0) {
                categories[binding.categorySpinner.selectedItemPosition]
            } else null

            val selectedGroup = if (binding.groupSpinner.selectedItemPosition > 0 && binding.groupSpinner.selectedItemPosition <= groups.size) {
                groups[binding.groupSpinner.selectedItemPosition - 1]
            } else null

            val wordEntity = Word(
                id = wordId ?: 0,
                word = word,
                meaning = meaning,
                categoryId = selectedCategory?.id,
                groupId = selectedGroup?.id
            )

            if (wordId != null) {
                viewModel.updateWord(wordEntity)
                val meaningTexts = meaning.split("，", ",").map { it.trim() }.filter { it.isNotBlank() }
                viewModel.saveMeanings(wordId!!, meaningTexts)
            } else {
                val meaningTexts = meaning.split("，", ",").map { it.trim() }.filter { it.isNotBlank() }
                viewModel.insertWord(wordEntity, meaningTexts = meaningTexts)
            }

            Toast.makeText(this, R.string.word_saved, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
