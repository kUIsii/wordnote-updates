# AI 开发问题排查指南

本文档记录开发过程中遇到的常见问题和解决方案，供后续 AI 对话参考。

---

## 1. Git 仓库位置

**问题**：当前工作目录 `手机备忘录/` 不是 git 仓库，git 仓库在子目录 `WordNoteApp/` 中。

**解决**：所有 git 操作需要先 `cd WordNoteApp/` 或使用完整路径。

```bash
cd "C:\Users\陈仕杰\Desktop\文档收集器\手机备忘录\WordNoteApp"
```

---

## 2. Bash 环境限制

**问题**：当前 bash 环境是精简版，很多常见命令不可用：
- 没有 `ls`、`cp`、`cat`、`head`、`tail`、`find`、`tr`、`which` 等
- 没有 `git`、`python`、`node`、`powershell.exe`
- `cmd.exe` 会被 conda 初始化脚本干扰

**替代方案**：
- 用 Glob 工具代替 `ls`/`find` 搜索文件
- 用 Read 工具代替 `cat` 读文件
- 用 Write/Edit 工具代替 `cp` 复制文件
- 用 Grep 工具代替 `grep`/`rg` 搜索内容
- git 操作：用户手动在系统终端执行，或通过 `gradlew.bat` 间接调用

---

## 3. 构建与复制 APK

**构建命令**（Windows 环境必须用 `gradlew.bat`）：
```bash
cd "C:\Users\陈仕杰\Desktop\文档收集器\手机备忘录\WordNoteApp"
"C:/Users/陈仕杰/Desktop/文档收集器/手机备忘录/WordNoteApp/gradlew.bat" assembleDebug
```

**APK 自动复制**：`app/build.gradle` 中已配置 task，构建完成后自动复制到：
```
C:\Users\陈仕杰\Desktop\文档收集器\手机备忘录\单词笔记.apk
```

**注意**：不要用 `./gradlew`（bash 环境下找不到 Java），必须用 `gradlew.bat`。

---

## 4. GitHub Release 创建

**重要**：仅 `git push` 代码不会触发应用内更新检测，必须创建 GitHub Release 并上传 APK。

**应用更新检测逻辑**：
- 应用通过 GitHub API 获取 `/repos/{owner}/{repo}/releases/latest`
- 比较 `tag_name` 版本号
- 从 `assets` 中下载 APK

**完整发布流程**：

### 方式一：使用 curl + GitHub API

```bash
# 1. 创建 Release
TOKEN="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"
echo '{"tag_name":"v2.4.2","name":"v2.4.2","body":"更新内容描述","draft":false,"prerelease":false}' > /tmp/release.json

curl -s -X POST \
  -H "Authorization: token $TOKEN" \
  -H "Accept: application/vnd.github.v3+json" \
  -H "Content-Type: application/json" \
  -d @/tmp/release.json \
  "https://api.github.com/repos/kUIsii/wordnote-updates/releases"

# 2. 上传 APK（返回的 id 替换到下面）
RELEASE_ID="返回的id"
curl -s -X POST \
  -H "Authorization: token $TOKEN" \
  -H "Accept: application/vnd.github.v3+json" \
  -H "Content-Type: application/vnd.android.package-archive" \
  --data-binary @"C:\Users\陈仕杰\Desktop\文档收集器\手机备忘录\单词笔记.apk" \
  "https://uploads.github.com/repos/kUIsii/wordnote-updates/releases/$RELEASE_ID/assets?name=wordnote.apk"
```

**注意**：Windows 环境下，JSON 直接写在 `-d` 参数中可能解析失败，必须用临时文件。

### 方式二：安装 gh CLI

```bash
winget install GitHub.cli
gh auth login
gh release create v2.4.2 ./wordnote.apk --title "v2.4.2" --notes "更新内容"
```

---

## 5. 版本号管理

**文件位置**：`app/build.gradle`

```gradle
defaultConfig {
    versionCode 20  // 每次发版 +1
    versionName "2.4.1"  // 语义化版本
}
```

**版本号规则**：
- `versionCode`：整数，每次发版递增
- `versionName`：`主版本.次版本.修订号`
- 当前最新版本：v2.4.1 (versionCode 20)

---

## 6. 项目关键配置

- **包名**：`com.wordnote.app`
- **GitHub 仓库**：`kUIsii/wordnote-updates`
- **GitHub Token**：见 `UpdateChecker.kt` 第 21 行
- **数据库版本**：v11（见 `WordDatabase.kt`）
- **当前 APK 路径**：`C:\Users\陈仕杰\Desktop\文档收集器\手机备忘录\单词笔记.apk`

---

## 7. 数据库迁移历史

| 迁移 | 内容 |
|------|------|
| MIGRATION_1_2 | 添加 forgetCount, nextReviewAt, lastReviewedAt |
| MIGRATION_2_3 | 添加 category.color, 创建 word_meanings 表 |
| MIGRATION_3_4 | 添加 word.groupId, 创建 word_groups 表 |
| MIGRATION_4_5 | 添加 word.batchId (批量输入分组) |
| MIGRATION_5_6 | 添加 word_meanings.isHighlighted (释义颜色标记) |
| MIGRATION_6_7 | 创建 diary_entries, diary_todos, diary_word_refs 表 |
| MIGRATION_7_8 | 删除 diary_todos, diary_word_refs 表 |
| MIGRATION_8_9 | 添加 word_meanings.sortOrder (释义排序) |
| MIGRATION_9_10 | 删除 diary_entries 等日记相关表 |
| MIGRATION_10_11 | 添加 words.isDeleted, words.deletedAt (回收站) |

**重要**：修改 entities 列表或添加新字段时，必须升级数据库版本并添加迁移。

---

## 8. 用户偏好与设计规范

### UI 设计
- **不要用 emoji**：用户明确要求 UI 中不要使用 emoji，显得廉价和 AI 味
- **图标风格**：使用 Material Design Outlined 风格，2dp stroke，24dp x 24dp
- **颜色基调**：蓝色渐变，柔和不刺眼
- **卡片样式**：圆角 12-20dp，无阴影或微阴影，1dp 描边
- **间距规范**：页面边距 16dp，卡片内边距 16dp，元素间距 12dp

### 日记功能
- 温暖色调：米色背景 #F5F1EB + 绿色强调色 #7D9B76
- 简洁设计：只有日期和文字内容，不要花哨功能
- 按月份查看，支持左右切换

### 开发习惯
- **自动构建**：改完代码后自动运行 `gradlew assembleDebug`
- **APK 放桌面**：构建后自动复制到桌面
- **更新 PROJECT.md**：每次功能变更后必须同步更新
- **攒功能再推送**：不要每个小改动都推送，攒几个功能后一次性推送

---

## 9. 常见错误与解决方案

### 数据库版本冲突导致闪退
**症状**：更新后应用一打开就闪退
**原因**：修改了 entities 列表但没有升级数据库版本
**解决**：升级数据库版本 + 添加迁移

### 编译错误：Unresolved reference
**症状**：编译时报 `Unresolved reference 'xxx'`
**原因**：布局文件中缺少对应的 ID，或 ID 名称拼写错误
**解决**：检查布局文件中是否有对应的 `android:id="@+id/xxx"`

### 编译错误：Namespace missing
**症状**：`app:flexWrap` 等属性报错
**原因**：布局根元素缺少 `xmlns:app` 命名空间声明
**解决**：在根元素添加 `xmlns:app="http://schemas.android.com/apk/res-auto"`

### RecyclerView 在 ScrollView 中
**症状**：RecyclerView 内容显示不全或高度为 0
**解决**：设置 `android:nestedScrollingEnabled="false"` 和 `android:overScrollMode="never"`

---

## 10. 文件结构速查

### 核心数据文件
- `app/src/main/java/com/wordnote/app/data/Word.kt` - 单词实体
- `app/src/main/java/com/wordnote/app/data/WordDao.kt` - 单词 DAO
- `app/src/main/java/com/wordnote/app/data/WordDatabase.kt` - 数据库定义
- `app/src/main/java/com/wordnote/app/data/WordRepository.kt` - 数据仓库

### UI 文件
- `app/src/main/java/com/wordnote/app/ui/MainActivity.kt` - 主页
- `app/src/main/java/com/wordnote/app/ui/WordDetailActivity.kt` - 单词详情
- `app/src/main/java/com/wordnote/app/ui/WordViewModel.kt` - 主 ViewModel

### 适配器
- `app/src/main/java/com/wordnote/app/ui/adapter/WordAdapter.kt` - 单词列表
- `app/src/main/java/com/wordnote/app/ui/adapter/MeaningAdapter.kt` - 释义列表（支持拖动）

### 工具类
- `app/src/main/java/com/wordnote/app/util/DateUtils.kt` - 日期工具
- `app/src/main/java/com/wordnote/app/util/UpdateChecker.kt` - 应用更新检查

---

## 11. 下次 AI 对话快速启动清单

1. 阅读本文档了解项目背景
2. 检查 `PROJECT.md` 了解当前功能状态
3. 检查 `app/build.gradle` 确认当前版本号
4. 检查 `WordDatabase.kt` 确认数据库版本
5. 构建测试：`"gradlew.bat" assembleDebug`
