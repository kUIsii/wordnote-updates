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

### 开发中 / 待做

| 功能 | 状态 |
|------|------|
| 数据导入导出 (JSON) | 已用数据库文件备份替代 |
| 更多复习策略 | 待定 |
| 单词统计图表 | 待定 |

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
│   ├── WordDao.kt                  # 单词 DAO
│   ├── CategoryDao.kt              # 分类 DAO
│   ├── TagDao.kt                   # 标签 DAO
│   ├── WordMeaningDao.kt           # 释义 DAO
│   ├── WordGroupDao.kt             # 分组 DAO
│   ├── WordDatabase.kt             # Room 数据库 (v6)
│   ├── WordRepository.kt           # 数据仓库层
│   └── DictionaryDatabase.kt       # 离线词典数据库 (ECDict)
├── ui/
│   ├── MainActivity.kt             # 主页：Tab + 单词列表 + 输入
│   ├── WordDetailActivity.kt       # 单词详情：复习、释义标注
│   ├── AddWordActivity.kt          # 添加/编辑单词
│   ├── CategoryActivity.kt         # 分类管理 + 颜色选择器
│   ├── SettingsActivity.kt         # 设置：备份/恢复/深色模式/日历查看
│   ├── CalendarViewActivity.kt     # 日历查看：按日期查看单词
│   ├── DictionaryActivity.kt       # 离线词典搜索页面
│   └── adapter/
│       ├── WordAdapter.kt          # 单词列表适配器 (紧凑单行布局，支持批量选择)
│       └── CategoryAdapter.kt      # 分类列表适配器
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
│   ├── item_word_compact.xml       # 单词条目 (单行紧凑)
│   ├── item_date_header.xml        # 日期分组标题
│   ├── item_category.xml           # 分类条目
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

### 数据库 Schema (v6)

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
└── batchId: Long? (批量输入分组ID)

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

tags / word_tag (标签系统，目前未在 UI 使用)
```

### 数据库迁移

- MIGRATION_1_2: 添加 forgetCount, nextReviewAt, lastReviewedAt
- MIGRATION_2_3: 添加 category.color, 创建 word_meanings 表
- MIGRATION_3_4: 添加 word.groupId, 创建 word_groups 表
- MIGRATION_4_5: 添加 word.batchId (批量输入分组)
- MIGRATION_5_6: 添加 word_meanings.isHighlighted (释义颜色标记)

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
