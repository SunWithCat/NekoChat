# 🐾 NekoChat

Ciallo～(∠・ω< )⌒★
这是一款基于 Android 的智能聊天应用，与猫娘 AI 助手"小苍"进行有趣对话。

## 📸 预览

<div align="center" style="display: flex; gap: 20px; flex-wrap: wrap;">
  <img src="preview1.jpg" alt="预览图1" style="width: 20%; max-width: 200px; border-radius: 12px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
  <img src="preview2.jpg" alt="预览图2" style="width: 20%; max-width: 200px; border-radius: 12px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

## ✨ 功能特性

- 与基于 Google Gemini 2.5 Flash 的猫娘实时聊天与可爱互动

## 🛠️ 技术栈

- **Kotlin** - 主要编程语言
- **Jetpack Compose** - 声明式 UI 框架
- **Retrofit + OkHttp** - 网络请求
- **Coroutines** - 异步处理
- **MVVM** - 架构模式
- **Room** - 本地数据存储

## ⚙️ 配置使用

1. 在 `local.properties` 中添加你的 Gemini API Key：
```
GEMINI_API_KEY=your_api_key_here
```

2. 构建并运行应用

3. 若需修改 AI 角色或对话风格，可在 `AIConfig.kt` 中调整对应内容。
