package com.wordnote.app.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wordnote.app.R
import com.wordnote.app.databinding.ActivitySentenceListBinding
import com.wordnote.app.data.SentenceWithWords
import com.wordnote.app.ui.adapter.SentenceAdapter
import com.wordnote.app.util.compatOverridePendingTransition
import com.wordnote.app.util.compatOverridePendingTransitionClose

class SentenceListActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySentenceListBinding
    private lateinit var viewModel: SentenceViewModel
    private lateinit var adapter: SentenceAdapter
    private var allSentences: List<SentenceWithWords> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySentenceListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[SentenceViewModel::class.java]

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupAddButton()
        observeData()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
            compatOverridePendingTransitionClose(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun setupRecyclerView() {
        adapter = SentenceAdapter(
            onItemClick = { item ->
                val intent = Intent(this, SentenceDetailActivity::class.java).apply {
                    putExtra(SentenceDetailActivity.EXTRA_SENTENCE_ID, item.sentence.id)
                }
                startActivity(intent)
                compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            },
            onDeleteClick = { item ->
                MaterialAlertDialogBuilder(this)
                    .setTitle("删除句子")
                    .setMessage("确定要删除这个句子吗？")
                    .setPositiveButton("删除") { _, _ ->
                        viewModel.deleteSentence(item.sentence)
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
        )

        binding.sentenceRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.sentenceRecyclerView.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterSentences(s?.toString() ?: "")
            }
        })
    }

    private fun filterSentences(query: String) {
        if (query.isBlank()) {
            adapter.submitSentences(allSentences)
        } else {
            val filtered = allSentences.filter { item ->
                item.sentence.originalText.contains(query, ignoreCase = true) ||
                    (item.sentence.translation?.contains(query, ignoreCase = true) == true)
            }
            adapter.submitSentences(filtered)
        }
    }

    private fun setupAddButton() {
        binding.addSentenceButton.setOnClickListener {
            val intent = Intent(this, SentenceEditActivity::class.java)
            startActivity(intent)
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun observeData() {
        viewModel.allSentencesWithWords.observe(this) { sentences ->
            allSentences = sentences
            adapter.submitSentences(sentences)
            binding.emptyView.visibility = if (sentences.isEmpty()) View.VISIBLE else View.GONE
            binding.sentenceRecyclerView.visibility = if (sentences.isEmpty()) View.GONE else View.VISIBLE
        }
    }
}
