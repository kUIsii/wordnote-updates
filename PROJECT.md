# WordNoteApp (单词笔记) - 项目文档

## 项目简介

Android 英语单词记忆工具，支持分类管理、一词多义标注、分组、间隔复习、深色模式、数据备份恢复。

- 包名：`com.wordnote.app`
- 语言：Kotlin
- 最低 SDK：26 (Android 8.0)
- 目标 SDK：36
- 架构：Room + LiveData + ViewModel

---

## 功能清单

### 已完成

| 功能 | 说明 |
|------|------|
| 单词记录 | 输入 `单词 释义` 格式快速添加 |
| 自定义分类 | 12 种预设颜色，支持增删改 |
| 分类 Tab | 按分类筛选，Tab 使用分类自身颜色，支持左右滑动切换分类 |
| 一词多义标注 | 逗号分隔多义，空格分隔近义词 |
| 释义级标注 | 每个释义可单独标记"不会"或添加备注，被标记的释义在详情页显示为红色高亮 |
| 释义颜色标记 | 每个释义可单独标记，使用分类颜色显示文字 |
| 词语分组 | 相近词归入同一组，显示组标签 |
| 批量输入 | "意思相近的单词"分类支持多行输入，一次性添加多个相关词，自动分组；其他分类单行输入 |
| 批量分组显示 | 同批次单词合并为一个白色卡片，每个单词一行，不同批次之间有间距 |
| 长按多选删除 | 长按单词进入选择模式，支持批量删除 |
| 日历查看 | 按日期查看单词记录，支持搜索 |
| 间隔复习 | 忘记后按递增间隔提醒复习 |
| 深色模式 | 蓝调深色背景 #1A1D23，设置页开关，启动时读取偏好 |
| 搜索 | 按单词、释义、备注全文搜索 |
| 编辑单词 | 底部弹窗 (BottomSheet) 编辑，含分类/分组选择 |
| 数据备份 | 导出数据库到 Downloads/WordNoteBackup/，附带版本元数据 |
| 数据恢复 | 从备份文件恢复，自动重启，支持跨版本恢复 |
| 应用图标 | 自适应图标，蓝色背景 + 白色叠放卡片 + 字母 A |
| 页面过渡动画 | 所有页面切换使用滑动动画（已修复动画不对称问题） |
| 离线词典搜索 | 独立词典页面，支持加载 ECDict 离线数据库查询英文单词释义、音标、词频、考试标签；通过 SAF 选择数据库文件，首次选择后自动记住 |
| 词典在线例句 | 英译中查询时异步调用 Free Dictionary API 获取英文例句，最多显示 3 条，网络不可用时静默跳过 |
| 词典词性显示 | 词典结果卡片显示词性标签（名词/动词/形容词等），支持多词性显示 |
| 中文检索英文 | 词典页面支持中→英反向查询，输入中文可查找包含该释义的所有英文单词 |
| 应用内自动更新 | 启动时检查 GitHub 私有仓库 Releases，有新版本弹窗提示下载安装，无需数据线 |
| 日记/备忘录 | 每日记录想法、待办事项，支持心情追踪、关联单词、学习摘要统计 |
| 单词回收站 | 删除的单词移入回收站，30 天内可恢复，支持批量恢复和彻底删除 |
| 词典搜索记录 | 英译中/中译英搜索记录分开存储，支持一键清除 |
| 备份文件管理 | 统一备份管理界面，支持新建备份、恢复、重命名、删除 |
| 释义排序同步 | 拖动排序释义后主页显示顺序同步更新 |
| 日历高亮同步 | 日历查看页显示被标记的释义时显示加粗蓝色 |
| 学习统计 | 学习连续天数、学习趋势（7天分类堆叠柱状图+点击查看分类明细）、分类饼图、复习进度、测验统计、记不住的单词 |
| 日期三级折叠 | 月→周→日三级折叠，周数按ISO 8601标准计算（周一为一周第一天） |
| 测验记录 | 独立测验历史页面，显示统计摘要和完整历史，支持查看详情和删除 |
| 批量追加 | 在batch组末尾点击"+"追加新单词到同一分组 |
| 跨分类重复检测 | 详情页显示该单词在其他分类中的出现，支持快速跳转；插入时提示重复 |
| 句子记录 | 输入英文句子，手动标注不认识的单词/短语并提供翻译，独立页面管理，支持添加/编辑/删除 |
| 句子详情页 | 独立句子详情页面，展示原文、翻译、生词分析 |
| 导航重构 | 新增「更多」页面整合日历、统计、测验、句子、分类入口；设置页精简为深色模式/备份/回收站/版本 |
| 句子搜索 | 句子列表支持按原文和翻译内容搜索 |
| 自定义分类颜色 | 颜色选择器支持12预设色+自定义HSL调色 |
| 测验双列结果 | 测验结果正确/不熟悉单词分两列显示，柔和配色 |

### 开发中 / 待做

| 功能 | 状态 |
|------|------|
| 数据导入导出 (JSON) | 已用数据库文件备份替代 |
| 更多复习策略 | 待定 |
| 单词统计图表 | 已完成（连续天数、热力图、复习进度） |

---

## 技术架构

### 项目结构

```
app/src/main/java/com/wordnote/app/
├── WordNoteApplication.kt          # Application，初始化数据库
├── data/
│   ├── Word.kt                     # 单词实体
│   ├── Category.kt                 # 分类实体 (含 color)
│   ├── Tag.kt                      # 标签实体
│   ├── WordTag.kt                  # 多对多关联
│   ├── WordMeaning.kt              # 释义实体 (一词多义)
│   ├── WordGroup.kt                # 词语分组
│   ├── Sentence.kt                 # 句子实体
│   ├── SentenceWord.kt             # 句子生词实体
│   ├── SentenceWithWords.kt        # 句子+生词关联
│   ├── WordDao.kt                  # 单词 DAO
│   ├── CategoryDao.kt              # 分类 DAO
│   ├── TagDao.kt                   # 标签 DAO
│   ├── WordMeaningDao.kt           # 释义 DAO
│   ├── WordGroupDao.kt             # 分组 DAO
│   ├── SentenceDao.kt              # 句子 DAO
│   ├── WordDatabase.kt             # Room 数据库 (v13)
│   ├── WordRepository.kt           # 数据仓库层
│   └── DictionaryDatabase.kt       # 离线词典数据库 (ECDict)
├── ui/
│   ├── MainActivity.kt             # 主页：Tab + 单词列表 + 输入
│   ├── WordDetailActivity.kt       # 单词详情：复习、释义标注
│   ├── AddWordActivity.kt          # 添加/编辑单词
│   ├── CategoryActivity.kt         # 分类管理 + 颜色选择器
│   ├── SettingsActivity.kt         # 设置：备份/恢复/深色模式/版本信息
│   ├── MoreActivity.kt             # 更多功能入口：日历/统计/测验/句子/分类
│   ├── CalendarViewActivity.kt     # 日历查看：按日期查看单词和句子
│   ├── DictionaryActivity.kt       # 离线词典搜索页面
│   ├── DiaryActivity.kt            # 日记列表页面
│   ├── DiaryDetailActivity.kt      # 日记详情/编辑页面
│   ├── DiaryViewModel.kt           # 日记 ViewModel
│   ├── SentenceListActivity.kt     # 句子列表页面
│   ├── SentenceDetailActivity.kt   # 句子详情页面
│   ├── SentenceEditActivity.kt     # 句子编辑页面
│   ├── SentenceViewModel.kt        # 句子 ViewModel
│   ├── RecycleBinActivity.kt       # 回收站页面
│   ├── StatisticsActivity.kt       # 学习统计页面
│   ├── HeatmapView.kt              # 热力图自定义View
│   └── adapter/
│       ├── WordAdapter.kt          # 单词列表适配器 (紧凑单行布局，支持批量选择)
│       ├── CategoryAdapter.kt      # 分类列表适配器
│       ├── SentenceAdapter.kt      # 句子列表适配器
│       ├── DiaryAdapter.kt         # 日记列表适配器
│       └── DiaryTodoAdapter.kt     # 日记待办适配器
└── util/
    ├── DateUtils.kt                # 日期工具
    └── ActivityTransitionExt.kt    # 页面过渡动画兼容性工具 (API 34+ 兼容)

app/src/main/res/
├── layout/
│   ├── activity_main.xml           # 主页
│   ├── activity_word_detail.xml    # 单词详情
│   ├── activity_add_word.xml       # 添加/编辑
│   ├── activity_category.xml       # 分类管理
│   ├── activity_settings.xml       # 设置页
│   ├── activity_calendar_view.xml  # 日历查看页
│   ├── activity_dictionary.xml     # 离线词典搜索页
│   ├── activity_diary.xml          # 日记列表页
│   ├── activity_diary_detail.xml   # 日记详情/编辑页
│   ├── activity_recycle_bin.xml    # 回收站页面
│   ├── item_recycle_bin_word.xml   # 回收站单词条目
│   ├── item_word_compact.xml       # 单词条目 (单行紧凑)
│   ├── item_date_header.xml        # 日期分组标题
│   ├── item_category.xml           # 分类条目
│   ├── item_diary_entry.xml        # 日记列表条目
│   ├── item_diary_todo.xml         # 日记待办条目
│   ├── sheet_edit_word.xml         # 底部编辑弹窗
│   ├── dialog_add_category.xml     # 添加分类弹窗
│   ├── dialog_note_input.xml       # 备注输入弹窗
│   └── dialog_color_picker.xml     # 颜色选择 (已弃用，代码构建)
├── drawable/
│   ├── ic_launcher_foreground.xml  # 图标前景 (矢量)
│   ├── ic_launcher_background.xml  # 图标背景 (矢量)
│   ├── ic_settings.xml             # 设置图标
│   ├── ic_backup.xml               # 备份图标
│   ├── ic_restore.xml              # 恢复图标
│   ├── ic_trash_restore.xml        # 回收站图标
│   ├── ic_back.xml                 # 返回图标
│   ├── ic_dictionary.xml           # 词典图标 (书本形状)
│   ├── search_box_background.xml   # 搜索框背景
│   └── ...                         # 其他背景/形状
├── anim/                           # 页面过渡动画
├── mipmap-anydpi-v26/              # 自适应图标定义
├── values/colors.xml               # 亮色主题色
├── values/themes.xml               # Material3 主题
├── values-night/colors.xml         # 暗色主题色
└── values-night/themes.xml         # 暗色主题
```

### 数据库 Schema (v12)

```
words
├── id (PK)
├── word: String
├── meaning: String
├── categoryId: Long? (FK → categories)
├── groupId: Long? (FK → word_groups)
├── note: String?
├── createdAt: Long
├── forgetCount: Int
├── nextReviewAt: Long
├── lastReviewedAt: Long
├── batchId: Long? (批量输入分组ID)
├── isDeleted: Boolean (软删除标记)
└── deletedAt: Long (删除时间)

categories
├── id (PK)
├── name: String
├── color: String (hex, e.g. "#E53935")
└── isDefault: Boolean

word_meanings
├── id (PK)
├── wordId: Long (FK → words)
├── meaningText: String
├── note: String?
├── isProblematic: Boolean
└── isHighlighted: Boolean (颜色标记)

word_groups
├── id (PK)
├── name: String
└── createdAt: Long

diary_entries (NEW)
├── id (PK)
├── entryDate: Long (唯一，每天一条)
├── content: String (日记正文)
├── mood: Int (0=无, 1=开心, 2=平静, 3=难过, 4=疲惫, 5=兴奋)
├── createdAt: Long
└── updatedAt: Long

diary_todos (NEW)
├── id (PK)
├── diaryEntryId: Long (FK → diary_entries, CASCADE)
├── text: String
├── isCompleted: Boolean
├── sortOrder: Int
└── createdAt: Long

diary_word_refs (NEW)
├── diaryEntryId: Long (FK → diary_entries, CASCADE)
├── wordId: Long (FK → words, CASCADE)
└── addedAt: Long

tags / word_tag (标签系统，目前未在 UI 使用)
```

### 数据库迁移

- MIGRATION_1_2: 添加 forgetCount, nextReviewAt, lastReviewedAt
- MIGRATION_2_3: 添加 category.color, 创建 word_meanings 表
- MIGRATION_3_4: 添加 word.groupId, 创建 word_groups 表
- MIGRATION_4_5: 添加 word.batchId (批量输入分组)
- MIGRATION_5_6: 添加 word_meanings.isHighlighted (释义颜色标记)
- MIGRATION_6_7: 创建 diary_entries, diary_todos, diary_word_refs 表 (日记/备忘录功能)
- MIGRATION_7_8: 删除 diary_todos, diary_word_refs 表
- MIGRATION_8_9: 添加 word_meanings.sortOrder (释义排序)
- MIGRATION_9_10: 删除 diary_entries, diary_todos, diary_word_refs 表
- MIGRATION_10_11: 添加 words.isDeleted, words.deletedAt (单词回收站)

---

## 关键设计决策

1. **单词排序**: `ORDER BY createdAt ASC`，旧的在上新的在下
2. **输入格式**: `单词 释义`，逗号分隔多义，空格分隔近义词
3. **颜色预设**: 按色相排列（红→粉→橙→黄→绿→青→蓝→靛→紫→棕）
4. **编辑方式**: 点击单词弹出 BottomSheet 编辑，不跳转页面
5. **图标设计**: 两张叠放白色卡片 + 字母 A，蓝色 #1E88E5 背景
6. **备份方式**: 直接复制 .db 文件 + .meta 版本元数据，支持跨版本恢复（fallbackToDestructiveMigration）
7. **批量输入**: 仅"意思相近的单词"分类支持多行输入，多行时自动归组为一个白色卡片，每个单词一行无间隔
8. **释义标记**: 被标记为"不会"的释义在详情页以红色文字+浅红背景高亮显示；"部分意思记不住"分类独有的"标记颜色"功能可对特定释义做分类色高亮
9. **批量删除**: 长按单词进入选择模式，支持多选删除，类似微信聊天记录选择
10. **日历查看**: 在设置页添加入口，按日期查看单词记录，支持搜索

---

## 开发日志

### 2026-05-27 (分类切换性能优化)

- 修复分类切换卡顿
  - 去掉 `selectCategory()` 中先清空再设置的无用操作（原来触发两次 LiveData 通知）
  - 分类切换时跳过 DiffUtil 全量对比（旧数据和新数据完全不同，O(n*m) 对比是浪费）
  - 通过 `pendingCategorySwitch` 标志通知 Adapter 使用 `submitList(null)` + `submitList(items)` 模式

### 2026-05-26 (v2.19.3)

- 存储权限修复
  - 添加 READ/WRITE/MANAGE_EXTERNAL_STORAGE 权限声明
  - 备份管理入口添加运行时权限检查和引导
  - Android 11+ 引导用户到系统设置授权「所有文件访问权限」

### 2026-05-26 (v2.19.2)

- 更新检测修复
  - 移除更新检测和下载中的 GitHub Token 认证（公开仓库不需要）
  - 解决旧 token 泄露后手机端无法检测更新的问题
  - 移除 build.gradle 中的 GITHUB_TOKEN BuildConfig 字段

### 2026-05-26 (词典增强 v2.19.1)

- 例句中文翻译：每条例句自动调用 MyMemory API 显示中文翻译
- 例句保存：点击「记录」一键保存到句子记录，自动填充原文和翻译
- SentenceEditActivity 支持 EXTRA_SENTENCE_TEXT/EXTRA_SENTENCE_TRANSLATION 预填充
- 移除 local.properties 的 git 追踪（防止 token 泄露）

### 2026-05-26 (词典增强)

- 词典在线例句
  - 英译中查询时异步调用 Free Dictionary API（dictionaryapi.dev）获取英文例句
  - 最多显示 3 条例句，每条包含英文例句和英文释义
  - 网络不可用或 API 失败时静默跳过，不影响基本功能
  - 使用 lifecycleScope 管理协程，5 秒超时
- 词典 UI 重新设计
  - 结果卡片改为 MaterialCardView，圆角 16dp，描边样式
  - 单词和音标同行显示，视觉更紧凑
  - 新增词性标签（名词/动词/形容词等），蓝色显示
  - 新增「释义」和「例句」分区标签，灰色小字
  - 例句区域使用圆角背景卡片，视觉层次清晰
  - 标签芯片配色优化：考试标签用红色系，词频标签用绿色系

### 2026-05-26 (代码质量重构)

- CoroutineScope 修复
  - MainActivity: 4 处 `CoroutineScope(Dispatchers.Main)` → `lifecycleScope`
  - SettingsActivity: 7 处无作用域协程 → `lifecycleScope`（含 IO 调度）
  - 修复潜在内存泄漏：Activity 销毁后协程不再继续执行
- DiffUtil 修复
  - WordAdapter: 8 处 `notifyDataSetChanged()` → `rebuildAndSubmit()` + `submitList()`
  - 列表刷新现在走 DiffUtil 差量更新，有动画无闪烁
- ViewBinding 全量迁移
  - 18 个 Activity + 4 个 Adapter 全部从 `findViewById` 迁移到 ViewBinding
  - 消除 ~238 处 `findViewById` 调用
  - Dialog/BottomSheet 的 `findViewById` 保持不变（布局无绑定类）
- 手写 UI 提取为 XML 布局
  - ChangelogActivity: 版本条目提取为 `item_changelog_version.xml`
  - QuizResultActivity: 单词行提取为 `item_quiz_result_word.xml`
  - WordDetailActivity: 相似词行提取为 `item_similar_word.xml`
  - StatisticsActivity: 连续天数/遗忘单词/测验统计/趋势统计/分类明细 共 5 个布局
  - CalendarViewActivity: 句子卡片/分类头部/单词行 共 3 个布局
  - 堆叠柱状图保持程序化绘制（动态 Canvas）

### 2026-05-26 (v2.17.1)

- Bug修复
  - 修复日历查看页崩溃：ScrollView 有两个子节点导致崩溃，添加父 LinearLayout 包裹
  - 修复测验历史记录旧数据不显示正确单词：旧记录 correctWordIds 为空时显示提示文字
  - 修复分类颜色选择器崩溃：setBackgroundResource 传入 attr ID 改为 resolveAttribute 方式
  - 首页日期分割线优化：非折叠模式下日期显示为蓝色横线居中样式

### 2026-05-25 (v2.17.0)

- UI优化
  - 饼图图例排版优化：改为垂直列表，分类名完整显示
  - 分类管理美化：色块改为36dp圆角矩形，新增自定义HSL颜色选择器
  - 测验界面美化：柔和配色（橙色替代刺眼红色），正确/不熟悉单词分两列显示
  - 测验设置页抽取方式改为卡片样式（Radio效果）
  - 句子日期移到卡片顶部右侧
  - 首页图标重设计：词典、分组、更多图标风格统一更简洁
- 功能增强
  - 句子列表新增搜索功能
  - 分类颜色选择器支持自定义调色（色相/饱和度/明度三滑块）
- Bug修复
  - 修复日历点击崩溃问题（SentenceViewModel异常处理）
  - 修复学习趋势点击显示已删除分类的「未分类」问题
  - 移除复习进度卡片（统计数据模块精简）

### 2026-05-25 (v2.16.0)

- 导航重构
  - 新增「更多」页面 (MoreActivity)，整合日历查看、学习统计、单词测验、句子记录、分类管理5个入口
  - 设置页精简：仅保留深色模式、备份管理、回收站、版本信息
  - 首页header从5个按钮减为4个（词典、分组、更多、设置）
- 句子功能增强
  - 新增句子详情页 (SentenceDetailActivity)，展示原文、翻译、生词分析
  - 句子列表重新设计：更大卡片、更好排版、显示日期
  - 日历查看页同时显示当天的句子记录

### 2026-05-25 (v2.14.0)

- 搜索增强
  - 搜索匹配单个词时自动显示完整批次，方便整体复习
  - 新增全局搜索切换按钮，支持跨分类搜索并显示分类名
  - 搜索结果在全局模式下显示所属分类标签
- 批次追加优化
  - 「意思相近的单词」分类下单个词也可通过+号追加新词
  - 追加时自动创建批次，将原词和新词归入同一批次
- 批次遗忘标记
  - 批次卡片显示累计忘记次数徽章（红色数字）
- 跨分类显示修复
  - 修复详情页「该单词还出现在以下分类」显示空白的bug
  - 修复观察者累积、协程作用域、onResume重复加载等问题

### 2026-05-25 (v2.13.0)

- 学习统计重新设计
  - 学习趋势：改为7天分类堆叠柱状图，每天的柱子按分类颜色分段，点击可查看当天各分类的添加数量
  - 分类分布：从水平条形图改为饼图（PieChartView），显示各分类占比
  - 统计数字更新：本月新增、近7天、总词汇量
- 测验详情重新设计
  - 得分显示改为圆形色块（绿色80%+，橙色50%+，红色50%-）
  - 统计行更紧凑，不熟悉单词列表支持滚动
- 新增 PieChartView 自定义Canvas环形图组件

### 2026-05-25 (v2.12.0)

- 学习统计热力图回归
  - 将"近7天学习量"柱状图替换为 GitHub 风格学习热力图
  - 热力图范围缩小至 20 周（原 53 周），更紧凑
  - HeatmapView 支持可配置周数（numWeeks 参数）
- 单词测验修复
  - 修复 selectQuizWords 中 coerceAtLeast 导致优先抽取逻辑失效的 bug
  - 添加 QuizActivity 全流程日志（loadWords、selectQuizWords、finishQuiz）
  - 改进错误处理和异常日志记录

### 2026-05-24 (v2.11.0)

- 学习统计页面重做
  - 移除热力图，替换为更有意义的统计
  - 新增学习连续天数：当前连续天数、最长连续天数、累计学习天数
  - 新增近7天学习量柱状图：直观展示每日学习趋势
  - 新增复习进度：待复习/已掌握/未复习比例条，及待复习单词列表
  - 保留分类分布、测验统计、记不住的单词
- 单词测试闪退修复
  - 修复 QuizSetupActivity 中 styleCheckBox 对"全部分类" CheckBox 的 tag 类型错误
  - 原因：OnCheckedChangeListener 中 `buttonView.tag as Long` 对 null tag 抛出 ClassCastException
  - 移除 `checkBox.tag = checkBox.tag` 无效赋值，改用 `as? Long` 安全类型转换

### 2026-05-24 (v2.10.3)

- 测验闪退修复
  - `finishQuiz()` 调用 `viewModel.insertQuizHistory()` 时，内部使用 `viewModelScope.launch` 异步写入数据库，但 Activity 在写入完成前就 `finish()` 了，导致竞态条件
  - 新增 `insertQuizHistorySync` 挂起函数，直接调用 repository 层，确保数据库写入完成后再跳转结果页

### 2026-05-24 (v2.10.1)

- 热力图优化
  - 增大格子尺寸（最小12dp），提升可读性
  - 添加点击事件：点击格子可查看当天学习记录详情
- 单词测试修复
  - 修复 finishQuiz() 异步写入数据库后立即 finish() 导致的竞态问题
  - 添加 isFinishingQuiz 防重复触发保护

### 2026-05-24 (v2.10.0)

- 周数计算修复
  - 修复 getWeekKey() 周数计算bug：显式设置 Calendar.firstDayOfWeek = MONDAY 和 minimalDaysInFirstWeek = 4
  - 5月24日(周日)现在与5月23日(周六)显示在同一周
- 日级别折叠
  - DayHeader 添加 dayKey 和 wordCount 字段
  - 点击日头部可折叠/展开该日单词
  - 添加展开箭头和单词数量显示
- 测试记录修复
  - 移除 WordRepository/WordViewModel 中的 try-catch，简化 allQuizHistory 初始化
  - 修复 QuizSetupActivity/QuizHistoryActivity 中的三层 try-catch 导致 historySection 被隐藏
- 首页布局优化
  - Header paddingBottom: 2dp → 8dp
  - Search Bar paddingTop: 8dp → 12dp, paddingBottom: 4dp → 8dp
  - Category Tabs paddingTop: 4dp → 8dp, paddingBottom: 8dp → 12dp
- batch追加功能
  - 在batch组末尾添加"+"追加按钮
  - 点击弹出 BottomSheet 输入新单词，自动归入同一分组
- 跨分类重复检测
  - WordDao 添加 findSimilarWordsExcluding() 查询（忽略大小写）
  - 详情页显示"该单词也出现在以下分类"卡片
  - 点击分类名可跳转到对应分类
  - 插入新单词时 Toast 提示是否已存在于其他分类

### 2026-05-24 (v2.9.3)

- 日期折叠增强
  - 新增周级别折叠，支持月->周->日三级层级
  - 周头部显示周数、单词数量、展开/折叠箭头
  - 折叠状态自动保存，切换月份时周折叠状态保持
- 首页按钮优化
  - 日期分组按钮改用日历+列表图标 (ic_date_group)
  - 分类管理按钮改用文件夹图标 (ic_category)
  - 每个按钮底部添加文字标签 (词典/分组/分类/设置)
- 测验记录增强
  - 新增独立 QuizHistoryActivity 页面，显示完整测验历史
  - 顶部统计摘要：总测验次数、平均正确率、累计单词
  - 支持点击查看详细情况、长按删除
- 热力图修复
  - 添加 post-layout invalidation 确保视图正确刷新
  - onResume 时强制重绘，确保切换页面后数据同步

### 2026-05-24 (v2.9.2)

- 单词测试闪退修复
  - WordDatabase 添加 onOpen 回调，确保 quiz_history 表存在
  - WordRepository 和 WordViewModel 中 allQuizHistory 改为 lazy 初始化，避免阻塞
  - QuizSetupActivity 添加多层防御性错误处理
- 备份管理统一化
  - 合并"备份数据"、"恢复数据"、"管理备份"为一个"备份管理"入口
  - 新建统一备份管理 BottomSheetDialog
  - 支持备份文件重命名（弹窗输入新文件名）
  - 支持单个文件删除和恢复
  - 文件列表显示名称、大小、日期
- 日期折叠优化
  - 简化层级：去除周级别，改为月->日两级折叠
  - 重新设计月份头部：左侧蓝色竖条 + 月份名称 + 单词数量徽章 + 展开箭头
  - 重新设计日期头部：小圆点 + 日期标签 + 细线分隔
  - 展开/折叠箭头添加旋转动画
  - 日期分组模式持久化到 SharedPreferences，重启后保持

### 2026-05-24 (v2.9.1)

- 深色模式滚动位置修复增强
  - 修复切换深色模式后页面位置自动跑到最上方的问题
  - 在onRestoreInstanceState中正确恢复ViewModel状态
  - 确保选中分类在Activity重建后正确恢复

### 2026-05-24 (v2.9.0)

- 首页日期折叠分组
  - 新增日期分组按钮：点击切换日期折叠模式
  - 支持按月-周-日层级分组显示单词
  - 月份和周支持点击折叠/展开
  - 月份头显示年月和单词数量
  - 周头显示第几周和单词数量
  - 日头显示日期和星期
- 测验闪退修复
  - 修复QuizActivity中使用LiveData.value获取数据可能为null的问题
  - 改用同步方法getAllActiveWordsSync()获取单词数据
  - 在WordDao/Repository/ViewModel中添加同步获取方法
- 深色模式滚动位置修复
  - 使用onSaveInstanceState/onRestoreInstanceState保存和恢复滚动状态
  - 保存当前滚动位置、偏移量、选中分类和日期分组模式
- 更新更新日志
  - 在ChangelogActivity中添加v2.8.0、v2.8.1、v2.8.2的更新记录
- 体验优化
  - 词典例句加载状态提示：查询时显示"正在加载例句..."，无结果时显示"暂无例句"
  - 分类切换预加载：启动时缓存所有分类单词数据，滑动/点击切换分类时瞬间显示，不再每次查数据库
  - 分类切换流畅度优化：切换时先清空再加载，消除 DiffUtil 列表比较延迟

### 2026-05-24 (v2.8.2)

- 滚动条修复
  - 修复滚动条不可见问题：改用自定义 View 覆盖层实现，基于可见条目比例计算滑块高度和位置
  - 滚动时显示，1.5 秒后自动淡出
- 测验崩溃修复
  - 修复单词测验打开即闪退问题：添加数据库构造和 LiveData 观察的防御性错误处理
  - WordNoteApplication 添加数据库构建失败自动重试机制

### 2026-05-24 (v2.8.1)

- 测验记录增强
  - 点击记录可查看详细情况（时间、正确率、不熟悉单词列表）
  - 长按记录可删除
- 分类选择修复
  - 选择"全部分类"时所有分类自动打勾，视觉状态一致
  - 取消某个分类时"全部分类"自动取消
- 学习统计增强
  - 新增测验统计：测验次数、累计测验单词、平均正确率、最高正确率
  - 新增"总是记不住的单词"分析：按忘记次数排序，显示单词、释义和忘记次数

### 2026-05-23 (v2.8.0)

- 测验流程重新设计
  - 同时显示"记得"和"不记得"按钮，操作更直观
  - 点击"不记得"显示释义，再点击"下一个"继续
  - 点击"记得"直接进入下一个单词
  - 测验过程中添加渐入动画
- 新增测验历史记录
  - 每次测验完成后自动保存记录（时间、正确率、不熟悉单词）
  - 测验设置页显示最近 10 次测验记录
  - 数据库升级至 v12，新增 quiz_history 表
- 测验设置页优化
  - 移除分类选择旁的冗余颜色圆点，排版更清爽
- 主页滚动条修复
  - 添加 scrollbarFadeDuration="0" 确保滚动条始终可见
  - 调整滚动条样式（4dp 宽，50% 黑色）

### 2026-05-23 (v2.7.2)

- 修复单词测验多项严重 bug
  - 修复测验索引错误：currentIndex 在显示单词前就递增，导致处理错误的单词、测验结果不准确
  - 修复测验结束后应用崩溃：添加生命周期安全检查和空列表防护
  - 改用 lifecycleScope 替代 CoroutineScope，防止 Activity 销毁后协程继续执行导致崩溃
  - 添加测验过程中的渐入动画效果
- 修复测验设置页选择体验
  - 分类选择改为 CheckBox，支持单独取消选择
  - 选择时添加缩放和背景色渐变动画
  - 抽取方式改为 CheckBox，支持独立选择/取消
- 修复主页滚动位置记忆
  - 切换分类 Tab 后返回时正确恢复滚动位置（保存 adapter position + pixel offset）
  - 使用 scrollToPositionWithOffset 替代 scrollToPosition 确保精确恢复
- 修复主页滚动条显示
  - 编程启用垂直滚动条，确保始终可见

### 2026-05-23 (v2.7.0)

- 新增单词测验功能
  - 测验设置页：自定义测验数量（10-100，默认35）、选择分类、选择方式（随机/按遗忘次数）
  - 测验页面：显示单词和分类，点击"不记得"查看释义，支持记住/不记得操作
  - 测验结果页：显示正确率、不熟悉单词列表，支持重测
  - 遗忘次数递增：不记得的单词 forgetCount +1
- 主页紧凑优化
  - 间距缩小：header、搜索栏、tab、单词条目间距全面压缩
  - 单词条目更紧凑：padding、字号、按钮尺寸均减小
  - 单屏可显示更多单词
- 滚动条
  - 右侧显示半透明滚动条，方便快速定位
  - 自定义 scrollbar_thumb.xml 样式
- 记忆滚动位置
  - 切换分类 Tab 后返回自动恢复上次滚动位置
  - 使用 MutableMap 保存每个分类的滚动位置
- 统计页面美化
  - 卡片样式优化，视觉层次更清晰
  - 热力图和分类分布区域布局优化

### 2026-05-23 (v2.4.1)

- 新增单词回收站功能
  - 删除的单词不再直接移除，而是标记为软删除移入回收站
  - 回收站页面：显示已删除单词列表，支持勾选批量操作
  - 支持单个恢复、批量恢复、批量彻底删除
  - 已删除单词保留 30 天后自动清除
  - 设置页添加回收站入口
  - 数据库升级至 v11，添加 isDeleted 和 deletedAt 字段
  - 删除对话框提示"可从回收站恢复"
- 多义词拖动排序
  - 释义列表改为 RecyclerView，支持拖动排序
  - 左侧拖动图标可长按拖动调整释义顺序
  - 移除旧的上下箭头排序按钮

### 2026-05-21 (v2.2.1)

- 重新设计日记功能
  - 简化日记功能，去除心情、待办、关联单词等花哨功能
  - 日记列表：按月份查看，支持左右切换月份
  - 日记卡片：显示日期、星期、内容预览、字数
  - 长按删除日记
  - 搜索日记内容
  - 新增温暖色调设计（淡绿 + 米色背景）

### 2026-05-21 (v2.2.0)

- 新增日记/备忘录功能
  - 日记列表页面：按日期倒序显示所有日记条目，支持搜索
  - 日记详情页面：支持文字编辑
  - 数据库升级至 v7，新增 diary_entries 表
  - 主页添加日记入口按钮

### 2026-05-20 (v4)

- 修复"部分意思记不住"标记功能：进入详情页时自动从释义字符串生成 WordMeaning 条目
- 左右滑动切换分类：在主页单词列表区域左右滑动即可切换到相邻分类
- 修复 loadWord/loadMeanings 时序问题：保证 currentWord 设置后再触发自动创建

### 2026-05-20 (v3)

- 批量输入限制：仅"意思相近的单词"分类支持多行输入，其他分类恢复单行输入
- 批量分组显示优化：同批次单词合并为一个白色卡片（ShapeAppearanceModel 按位置设圆角），每个单词一行无间隔
- 移除主列表的红色感叹号（problematicIndicator），列表更简洁
- 释义"不会"标记改为颜色高亮：被标记的中文释义以红色文字+浅红背景显示
- "部分意思记不住"分类独有"标记颜色"功能：可对特定释义做分类色（蓝色）高亮，仅此分类显示该按钮

### 2026-05-20 (v2)

- UI 全面美化：柔和色板（background #F0F4F8、primary #5B7FD6），分类颜色更柔和不刺眼
- 单词列表改为 MaterialCardView 卡片布局，左侧分类色条，圆角 12dp
- 所有分类支持多行批量输入（移除"意思相近的单词"限制），多行自动分组
- 释义"不会"标记在主列表可见（warning 图标），详情页标记按钮增强（红色背景+图标）
- 备份恢复修复：添加 clearInstance() 清除单例、fallbackToDestructiveMigration 安全网
- 备份时写入 .meta 版本元数据，恢复时删除 journal 文件避免冲突
- 丰富预设数据：21 个单词覆盖 4 个分类，含 3 个 batch 组，部分单词预设释义和"不会"标记
- APK 统一输出到桌面固定路径（build.gradle 自动复制 task）
- 修复 MainActivity 启动黑屏：setDefaultNightMode 移到 setContentView 之前
- 深色模式启动时读取用户偏好（不再强制亮色）

### 2026-05-20

- 修复 Android 15+ (API 35+) 闪退问题：`overridePendingTransition()` 在 API 35 被移除，替换为兼容性封装
- 新增 `ActivityTransitionExt.kt` 工具类，根据 API 版本自动选择 `overrideActivityTransition()` 或 `overridePendingTransition()`
- 修复 `WordNoteApplication` 中未捕获异常可能导致崩溃的问题（添加 try/catch）

### 2026-05-19

- 完成所有核心功能开发
- UI 全面改版：Material3 风格、圆角卡片、紧凑布局
- 颜色选择器重做：64dp 色块 + 色相排序
- 深色模式优化：纯黑背景 + #1A1A1A 卡片
- 应用图标设计：自适应矢量图标
- 添加设置页面：备份/恢复数据库、深色模式开关
- 修复单词排序问题（DESC → ASC）
- 所有对话框统一 MaterialAlertDialogBuilder
- 添加页面过渡动画
- 修复页面切换动画不对称问题（slide_out_left和slide_in_left距离统一为100%）
- 添加批量输入功能（"意思相近的单词"分类支持多行输入）
- 添加批量分组显示（同批次输入的词用彩色边框框住，按分类颜色区分）
- 添加释义颜色标记功能（每个释义可单独标记，使用分类颜色显示文字）
- 数据库升级至v6，新增batchId和isHighlighted字段
- 添加长按多选删除功能（长按进入选择模式，支持批量删除）
- 添加日历查看功能（在设置页添加入口，按日期查看单词记录，支持搜索）
- 修复日期显示颜色（改回蓝色）
- 修复搜索框样式（添加边框，让用户能看清范围）
- 修复备份恢复功能（添加更详细的文件检查和错误处理）
- 改进批量分组显示（整个分组用边框框起来，而不是单个单词）
- 改进日历查看功能（按分类分组显示，每个分类用自己颜色边框，添加折叠功能）
- 修复深色模式下日历文字显示问题（使用主题颜色）
- 首页搜索框改为蓝色椭圆边框样式
- 修复批量分组边框显示（batch中共享边框：第一个词上圆角，中间无圆角，最后一个词下圆角）

---

## 后续可扩展

- 单词统计图表（按天/周/月统计）
- 更多复习算法（SuperMemo-2 等）
- 导出为 CSV/Anki 格式
- 云同步
- 桌面小组件（Widget）
