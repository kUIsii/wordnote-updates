# WordNote

一个 Android 英语单词记忆工具，帮你把单词真正记住。

## 功能

**单词管理**
- 快速添加：输入 `单词 释义` 即可记录，支持自动识别中英文顺序
- 自定义分类：12 种预设颜色 + 自定义 HSL 调色，左右滑动切换
- 一词多义：逗号分隔多义，每个释义可单独标记"不会"或高亮
- 批量输入：多行一次性添加，自动分组为一个卡片
- 词语分组：相近词归组，显示组标签
- 跨分类查重：添加时自动检测是否已存在于其他分类

**复习与测验**
- 间隔复习：忘记后按递增间隔提醒（1分钟 → 5分钟 → 30分钟 → 2小时 → 12小时 → 1天 → 3天 → 7天 → 15天 → 30天）
- 单词测验：自定义数量（10-100），支持随机抽取或按遗忘次数加权
- 测验历史：完整记录每次测验的正确率和不熟悉的单词

**离线词典**
- 集成 ECDict 离线词典，支持英→中和中→英双向查询
- 显示音标、柯林斯/牛津词频等级、考试标签（CET4/6、TOEFL、IELTS、GRE）

**学习统计**
- 学习热力图：GitHub 风格，一眼看出学习连续性
- 7 天趋势：按分类堆叠的柱状图，点击查看每日明细
- 分类分布饼图、测验统计、"总是记不住"单词排行

**数据安全**
- 数据库备份/恢复，支持多份备份管理
- 更新前自动备份数据库
- 回收站：删除的单词保留 30 天，支持批量恢复

**其他**
- 深色模式
- 句子记录：记录英文句子，标注不认识的词
- 日历查看：按日期回顾学习记录
- 应用内自动更新
- 日期三级折叠：月 → 周 → 日

## 技术栈

- Kotlin
- Room + LiveData + ViewModel (MVVM)
- ViewBinding
- Material Design 3
- 自定义 Canvas 绘制（热力图、饼图）

## 环境要求

- Android 8.0+ (API 26)
- 目标 SDK 36

## 构建

```bash
git clone https://github.com/kUIsii/wordnote-updates.git
cd wordnote-updates/WordNoteApp
```

在 `local.properties` 中添加你的 GitHub Token（用于应用内更新功能）：

```
GITHUB_TOKEN=your_token_here
```

```bash
./gradlew assembleDebug
```

APK 输出到 `app/build/outputs/apk/debug/app-debug.apk`。

## 下载

前往 [Releases](https://github.com/kUIsii/wordnote-updates/releases) 页面下载最新版 APK。

## 许可证

MIT
