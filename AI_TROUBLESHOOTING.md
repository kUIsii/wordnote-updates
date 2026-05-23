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

## 2. Batch 单词缩进对齐

**问题**：在"意思相近的单词"分类中，batch 内第二行及以后的单词没有和第一行对齐，而是偏左。

**原因**：代码用固定值 `36dp` 作为缩进宽度，但 `indexTextView` 是 `wrap_content`，显示 "1." 和 "10." 的宽度不同。

**解决**：动态测量 `indexTextView` 的实际宽度。

```kotlin
// 错误方式（固定值）
val indentWidth = (36 * density).toInt()

// 正确方式（动态测量）
indexTextView.text = "$index."
indexTextView.measure(
    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
)
val indexWidth = indexTextView.measuredWidth
val indexMarginEnd = (indexTextView.layoutParams as ViewGroup.MarginLayoutParams).marginEnd
val indentWidth = indexWidth + indexMarginEnd
```

---

## 3. GitHub Release 创建

**重要**：仅 `git push` 代码不会触发应用内更新检测，必须创建 GitHub Release 并上传 APK。

**应用更新检测逻辑**：
- 应用通过 GitHub API 获取 `/repos/{owner}/{repo}/releases/latest`
- 比较 `tag_name` 版本号
- 从 `assets` 中下载 APK

**完整发布流程**：

### 方式一：使用 curl + GitHub API（推荐，无需安装 gh）

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
  --data-binary @"C:\Users\陈仕杰\Desktop\单词笔记.apk" \
  "https://uploads.github.com/repos/kUIsii/wordnote-updates/releases/$RELEASE_ID/assets?name=wordnote.apk"
```

**注意**：Windows Git Bash 环境下，JSON 直接写在 `-d` 参数中可能解析失败，必须用临时文件。

### 方式二：安装 gh CLI

```bash
winget install GitHub.cli
gh auth login
gh release create v2.4.2 ./wordnote.apk --title "v2.4.2" --notes "更新内容"
```

---

## 4. 版本号管理

**文件位置**：`app/build.gradle`

```gradle
defaultConfig {
    versionCode 25  // 每次发版 +1
    versionName "2.4.2"  // 语义化版本
}
```

**版本号规则**：
- `versionCode`：整数，每次发版递增
- `versionName`：`主版本.次版本.修订号`

---

## 5. 构建与复制 APK

**构建命令**：
```bash
cd "C:\Users\陈仕杰\Desktop\文档收集器\手机备忘录\WordNoteApp"
./gradlew assembleDebug
```

**复制到桌面**：
```bash
cp "app/build/outputs/apk/debug/app-debug.apk" "C:\Users\陈仕杰\Desktop\单词笔记.apk"
```

---

## 6. 项目关键配置

- **包名**：`com.wordnote.app`
- **GitHub 仓库**：`kUIsii/wordnote-updates`
- **GitHub Token**：见 `UpdateChecker.kt` 第 21 行
- **数据库版本**：v11（见 `WordDatabase.kt`）

---

## 7. 常见陷阱

1. **不要用 emoji**：用户明确要求 UI 中不要使用 emoji，显得廉价
2. **更新 PROJECT.md**：每次功能变更后必须同步更新
3. **自动构建**：改完代码后自动运行 `gradlew assembleDebug`
4. **APK 复制到桌面**：构建后自动复制到 `C:\Users\陈仕杰\Desktop\单词笔记.apk`
5. **攒功能再推送**：不要每个小改动都推送，攒几个功能后一次性推送
