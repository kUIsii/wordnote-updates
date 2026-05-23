package com.wordnote.app.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wordnote.app.R

class ChangelogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changelog)

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        setupChangelog()
    }

    private fun setupChangelog() {
        val container = findViewById<LinearLayout>(R.id.changelogContainer)

        val changelog = listOf(
            VersionLog(
                "v2.2.2", "2026-05-21",
                listOf("修复数据库版本冲突导致的闪退")
            ),
            VersionLog(
                "v2.2.1", "2026-05-21",
                listOf(
                    "重新设计日记功能，删除心情、待办、关联单词等花哨功能",
                    "日记列表改为按月份查看，支持左右切换月份",
                    "长按可删除日记，新增搜索功能",
                    "页面风格改为温暖色调（米色背景 + 淡绿强调色）"
                )
            ),
            VersionLog(
                "v2.2.0", "2026-05-21",
                listOf(
                    "新增日记/备忘录功能",
                    "数据库升级至 v7，新增 diary_entries 表",
                    "主页添加日记入口按钮"
                )
            ),
            VersionLog(
                "v2.1.1", "2026-05-20",
                listOf(
                    "重新设计中译英搜索结果，改为卡片式布局",
                    "搜索结果按相关性排序（精确匹配 > 开头匹配 > 包含匹配）",
                    "FlexboxLayout 自动换行显示搜索结果"
                )
            ),
            VersionLog(
                "v2.1.0", "2026-05-20",
                listOf(
                    "去掉所有 emoji，换成矢量图标",
                    "统一蓝色渐变色调，增加视觉层次",
                    "14 个新图标：空状态图标 + 系统功能图标",
                    "颜色微调：更柔和的蓝色基调"
                )
            ),
            VersionLog(
                "v2.0.0", "2026-05-20",
                listOf(
                    "应用内自动更新：启动时检查 GitHub Releases",
                    "APK 签名统一：CI 和本地使用相同 keystore",
                    "语义化版本号：采用 MAJOR.MINOR.PATCH 格式"
                )
            ),
            VersionLog(
                "v1.10", "2026-05-20",
                listOf(
                    "离线词典搜索：支持加载 ECDict 数据库",
                    "中文检索英文：词典支持中→英反向查询",
                    "新增词典页面入口"
                )
            ),
            VersionLog(
                "v1.9", "2026-05-20",
                listOf(
                    "日历查看：按日期查看单词记录",
                    "搜索功能：全文搜索单词、释义、备注",
                    "编辑单词：底部弹窗编辑，含分类/分组选择"
                )
            ),
            VersionLog(
                "v1.8", "2026-05-19",
                listOf(
                    "数据备份恢复：导出/导入数据库文件",
                    "深色模式：设置页开关，启动时读取偏好",
                    "应用图标：自适应矢量图标"
                )
            ),
            VersionLog(
                "v1.0", "2026-05-19",
                listOf(
                    "单词记录：输入「单词 释义」格式快速添加",
                    "自定义分类：12 种预设颜色，支持增删改",
                    "一词多义标注：逗号分隔多义",
                    "间隔复习：忘记后按递增间隔提醒复习",
                    "词语分组：相近词归入同一组"
                )
            )
        )

        changelog.forEach { versionLog ->
            addVersionSection(container, versionLog)
        }
    }

    private fun addVersionSection(container: LinearLayout, log: VersionLog) {
        // Version header
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dpToPx(16), 0, dpToPx(8))
        }

        // Version badge
        val badge = TextView(this).apply {
            text = log.version
            textSize = 14f
            setTextColor(Color.WHITE)
            setPadding(dpToPx(10), dpToPx(4), dpToPx(10), dpToPx(4))
            background = GradientDrawable().apply {
                setColor(getColor(R.color.primary))
                cornerRadius = 12f * resources.displayMetrics.density
            }
        }
        headerLayout.addView(badge)

        // Date
        val dateText = TextView(this).apply {
            text = log.date
            textSize = 12f
            setTextColor(getColor(R.color.text_hint))
            setPadding(dpToPx(12), 0, 0, 0)
        }
        headerLayout.addView(dateText)

        container.addView(headerLayout)

        // Changes
        log.changes.forEach { change ->
            val changeText = TextView(this).apply {
                text = "• $change"
                textSize = 14f
                setTextColor(getColor(R.color.text_secondary))
                setPadding(dpToPx(8), dpToPx(4), 0, dpToPx(4))
                lineHeight = (14 * 1.5f * resources.displayMetrics.density).toInt()
            }
            container.addView(changeText)
        }

        // Divider
        val divider = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
            ).apply {
                topMargin = dpToPx(8)
            }
            setBackgroundColor(getColor(R.color.divider))
        }
        container.addView(divider)
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    data class VersionLog(
        val version: String,
        val date: String,
        val changes: List<String>
    )
}
