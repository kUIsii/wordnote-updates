# 单词应用 UI/UX 优化设计

**日期**: 2026-05-22
**版本**: v2.3.0 (versionCode 17)

## 优化项

### 1. 意思完全显示（自适应高度）
- WordAdapter 中 meaning TextView 移除 maxLines 限制
- 卡片高度随内容自然撑开

### 2. 批量复制到其他分类
- 选择模式下底部栏新增「复制到」按钮
- 点击弹出分类选择对话框
- 复制内容：word + meaning + categoryId，新记录获得新 id 和当前时间戳
- 不复制 notes、tags、groupId、batchId、复习状态
- SQL: `INSERT INTO words (word, meaning, categoryId, createdAt) SELECT word, meaning, :targetCategoryId, :now FROM words WHERE id IN (:wordIds)`

### 3. 意思排序 + 序号显示
- WordMeaning 新增 `sortOrder: Int` 字段（默认 0）
- 数据库迁移 v8 → v9
- WordDetailActivity 每个意思左侧显示序号
- 启用 ItemTouchHelper 拖拽排序
- 拖拽完成后持久化新顺序

### 4. 全分类标记功能
- 所有分类的 WordDetailActivity 都显示「标记」按钮
- 标记颜色统一使用蓝色 (#5B9BD5)
- 列表卡片中也显示蓝色高亮

### 5. 相近单词分组视觉增强
- 组间间距增大（+8dp）
- 分组首卡片顶部加分类颜色条（3dp 高）
- 组背景色与卡片背景色 slightly lighter 对比
