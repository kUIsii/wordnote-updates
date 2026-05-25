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
                "v2.17.1", "2026-05-26",
                listOf(
                    "日历崩溃修复：修复ScrollView有两个子节点导致的崩溃",
                    "测验历史记录：旧记录正确单词列显示提示文字，统一查看界面为双列布局",
                    "分类颜色选择器修复：修复自定义颜色按钮点击崩溃问题",
                    "首页日期分割线：非折叠模式下日期显示为蓝色横线居中样式"
                )
            ),
            VersionLog(
                "v2.17.0", "2026-05-25",
                listOf(
                    "日历崩溃修复：修复点击日历立即返回主页的问题",
                    "饼图图例优化：分类名完整显示，改为垂直列表排版",
                    "分类管理美化：色块更大更圆角，新增自定义HSL颜色选择器",
                    "测验界面美化：柔和配色，正确/不熟悉单词分两列显示，抽取方式改为卡片样式",
                    "句子记录优化：日期移到卡片顶部，新增搜索功能",
                    "首页图标重设计：词典、分组、更多图标风格统一更简洁",
                    "统计修复：学习趋势不再显示已删除分类的「未分类」，移除复习进度卡片"
                )
            ),
            VersionLog(
                "v2.16.0", "2026-05-25",
                listOf(
                    "导航重构：新增「更多」页面，整合日历查看、学习统计、单词测验、句子记录、分类管理",
                    "设置页精简：仅保留深色模式、备份管理、回收站、版本信息",
                    "首页header精简：从5个按钮减为4个（词典、分组、更多、设置）",
                    "句子详情页：新增独立句子详情页面，展示原文、翻译、生词分析",
                    "句子列表重新设计：更大卡片、更好排版、显示日期",
                    "日历查看增强：日历页面同时显示当天的句子记录"
                )
            ),
            VersionLog(
                "v2.15.0", "2026-05-25",
                listOf(
                    "新增句子记录功能：输入英文句子，手动标注不认识的单词/短语并提供翻译",
                    "独立句子管理页面：支持添加、编辑、删除句子",
                    "生词管理：每个句子可添加多个生词条目，包含单词和翻译",
                    "全局搜索图标优化：选中时显示蓝色圆形背景，更明显",
                    "搜索逻辑优化：无搜索词时始终显示当前分类，切换分类不受全局搜索影响",
                    "UI优化：周/日头部层级区分更明显，输入栏更紧凑"
                )
            ),
            VersionLog(
                "v2.14.0", "2026-05-25",
                listOf(
                    "搜索增强：搜索匹配单个词时自动显示完整批次，方便整体复习",
                    "全局搜索：新增搜索范围切换，支持跨分类搜索并显示分类名",
                    "批次追加优化：「意思相近的单词」分类下单个词也可通过+号追加",
                    "批次遗忘标记：批次卡片显示累计忘记次数徽章",
                    "跨分类显示修复：详情页正确显示该单词在其他分类中的信息"
                )
            ),
            VersionLog(
                "v2.13.0", "2026-05-25",
                listOf(
                    "学习趋势重新设计：改为7天分类堆叠柱状图，点击可查看当天各分类添加数量",
                    "分类分布改为饼图：从水平条形图改为环形饼图，显示各分类占比",
                    "测验详情重新设计：得分改为圆形色块，统计行更紧凑，不熟悉单词支持滚动"
                )
            ),
            VersionLog(
                "v2.12.0", "2026-05-25",
                listOf(
                    "学习统计热力图回归：替换近7天学习量柱状图为GitHub风格热力图，范围缩小至20周",
                    "单词测验闪退修复：修复displayHistory中setBackgroundResource使用attr导致的资源找不到崩溃",
                    "测验选词逻辑修复：修复优先抽取忘记次数多的词功能失效的bug"
                )
            ),
            VersionLog(
                "v2.11.0", "2026-05-24",
                listOf(
                    "学习统计重做：移除热力图，新增学习连续天数、近7天学习量柱状图、复习进度",
                    "单词测试闪退修复：修复测验设置页「全部分类」CheckBox 类型转换崩溃"
                )
            ),
            VersionLog(
                "v2.10.3", "2026-05-24",
                listOf(
                    "测验闪退修复：finishQuiz等待数据库写入完成后再跳转结果页，解决竞态条件导致的崩溃"
                )
            ),
            VersionLog(
                "v2.10.2", "2026-05-24",
                listOf(
                    "热力图优化：增大格子尺寸提升可读性，点击格子可查看当天学习记录",
                    "单词测试修复：修复测验完成后闪退问题，保存记录完成后再跳转结果页"
                )
            ),
            VersionLog(
                "v2.10.1", "2026-05-24",
                listOf(
                    "修复热力图不显示数据：根据屏幕宽度动态调整格子大小，今天的数据现在可以正常显示",
                    "修复batch追加创建新框框：追加的单词现在正确显示在原有分组中",
                    "修复单词测试闪退：添加异常处理，测试页面不再崩溃",
                    "修复测验分类名不显示：添加allCategories LiveData观察"
                )
            ),
            VersionLog(
                "v2.10.0", "2026-05-24",
                listOf(
                    "周数计算修复：显式设置周一为一周第一天，5月24日(周日)与5月23日(周六)现在在同一周",
                    "日级别折叠：点击日头部可折叠/展开该日单词，添加展开箭头和单词数量",
                    "测试记录修复：移除多余的try-catch，测验历史记录现在正常显示",
                    "首页布局优化：调整Header、搜索框、分类Tab间距，布局更舒适",
                    "batch追加功能：在batch组末尾点击+追加新单词到同一分组",
                    "跨分类重复检测：详情页显示该单词在其他分类中的出现，支持快速跳转"
                )
            ),
            VersionLog(
                "v2.9.3", "2026-05-24",
                listOf(
                    "日期折叠增强：新增周级别折叠，支持月->周->日三级层级",
                    "首页按钮优化：日期分组、分类管理使用独立图标，底部添加文字标签",
                    "测验记录增强：新增独立测验记录页面，显示统计摘要和完整历史",
                    "热力图修复：确保切换页面后热力图正确刷新显示最新数据"
                )
            ),
            VersionLog(
                "v2.9.2", "2026-05-24",
                listOf(
                    "单词测试闪退修复：数据库添加quiz_history表安全检查，ViewModel加防御性错误处理",
                    "备份管理统一化：合并备份/恢复/管理为一个界面，支持重命名备份文件",
                    "日期折叠优化：简化为月->日两级，重新设计头部视觉，支持动画展开/折叠",
                    "日期分组模式持久化：重启app后保持上次选择"
                )
            ),
            VersionLog(
                "v2.9.1", "2026-05-24",
                listOf(
                    "深色模式滚动位置修复增强：修复切换深色模式后页面位置自动跑到最上方的问题",
                    "在onRestoreInstanceState中正确恢复ViewModel状态",
                    "确保选中分类在Activity重建后正确恢复"
                )
            ),
            VersionLog(
                "v2.9.0", "2026-05-24",
                listOf(
                    "首页日期折叠分组：支持按月-周-日层级分组显示单词",
                    "月份和周支持点击折叠/展开",
                    "测验闪退修复：改用同步方法获取单词数据",
                    "深色模式滚动位置修复：使用onSaveInstanceState保存滚动状态"
                )
            ),
            VersionLog(
                "v2.8.2", "2026-05-24",
                listOf(
                    "修复滚动条不可见问题：改用自定义View覆盖层实现",
                    "修复单词测验打开即闪退问题：添加数据库构建和LiveData观察的防御性错误处理",
                    "WordNoteApplication添加数据库构建失败自动重试机制"
                )
            ),
            VersionLog(
                "v2.8.1", "2026-05-24",
                listOf(
                    "测验记录增强：点击记录可查看详细情况（时间、正确率、不熟悉单词列表）",
                    "长按记录可删除",
                    "选择「全部分类」时所有分类自动打勾，视觉状态一致",
                    "取消某个分类时「全部分类」自动取消",
                    "学习统计增强：新增测验统计（测验次数、累计测验单词、平均正确率、最高正确率）",
                    "新增「总是记不住的单词」分析：按忘记次数排序显示"
                )
            ),
            VersionLog(
                "v2.8.0", "2026-05-23",
                listOf(
                    "测验流程重新设计：同时显示「记得」和「不记得」按钮，操作更直观",
                    "点击「不记得」显示释义，再点击「下一个」继续",
                    "点击「记得」直接进入下一个单词",
                    "测验过程中添加渐入动画",
                    "新增测验历史记录：每次测验完成后自动保存记录",
                    "测验设置页显示最近10次测验记录",
                    "数据库升级至v12，新增quiz_history表"
                )
            ),
            VersionLog(
                "v2.7.1", "2026-05-23",
                listOf(
                    "修复测验页面无法进入下一个单词的bug",
                    "修复记忆滚动位置不生效的问题",
                    "测验分类选择改用标准checkbox图标",
                    "统计图表分类名完整显示",
                    "搜索栏与分类标签间距优化",
                    "滚动条更明显且不自动隐藏"
                )
            ),
            VersionLog(
                "v2.7.0", "2026-05-23",
                listOf(
                    "单词测验：支持随机或按遗忘次数选词，自定义测验数量和分类",
                    "测验结果：显示正确率，列出不熟悉的单词供复习",
                    "主页紧凑优化：间距缩小，单屏可显示更多单词",
                    "滚动条：右侧显示滚动条，方便快速定位",
                    "记忆滚动位置：切换分类后返回自动恢复上次位置",
                    "统计页面美化：卡片样式优化，视觉层次更清晰"
                )
            ),
            VersionLog(
                "v2.6.0", "2026-05-23",
                listOf(
                    "学习统计：热力图展示每日学习量",
                    "数据概览：总词汇量、本月新增、分类数",
                    "分类分布：按分类显示词汇占比"
                )
            ),
            VersionLog(
                "v2.5.0", "2026-05-23",
                listOf(
                    "词典搜索记录：英译中/中译英搜索记录分开存储，支持一键清除",
                    "备份文件管理：设置页可查看并批量删除旧备份文件",
                    "释义排序同步：拖动排序释义后主页显示顺序同步更新",
                    "日历高亮同步：日历查看页显示被标记的释义时显示加粗蓝色",
                    "界面优化：整体上移，单词列表更紧凑，单屏可显示更多内容"
                )
            ),
            VersionLog(
                "v2.4.1", "2026-05-23",
                listOf(
                    "单词回收站：删除的单词移入回收站，30天内可恢复",
                    "多义词拖动排序：长按拖动图标调整释义顺序"
                )
            ),
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
