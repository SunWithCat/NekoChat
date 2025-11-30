# 🐾 NekoChat

<div align="center">

![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5+-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Android](https://img.shields.io/badge/Android-8.0+-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

**Ciallo～(∠・ω< )⌒★**

*一款温馨可爱的 Android AI 聊天应用*  
*与猫娘助手"小苍"开启有趣的对话之旅*

</div>

---

## 📖 项目简介

NekoChat 是一款基于 Google Gemini API 的智能聊天应用，采用 Material Design 3 设计语言，提供流畅优雅的聊天体验。无论是日常闲聊还是信息查询，"小苍"都能以可爱的猫娘人格为你提供温馨的陪伴。

### 🎯 设计理念

- **隐私优先**: 所有对话数据仅存储在本地设备，不上传任何第三方服务器
- **个性化**: 支持自定义对话角色、温度参数和上下文长度
- **现代化**: 采用最新的 Jetpack Compose 构建 UI，流畅的动画和交互体验
- **轻量级**: 简洁的代码结构，快速启动，低内存占用

## ✨ 功能特性

### 核心功能

- 🐱 **智能对话系统** 
  - 基于 Google Gemini 2.5 Flash 模型
  - 支持多轮上下文理解
  - 可自定义系统提示词（System Prompt）

- 💬 **灵活的对话管理**
  - 创建和切换多个独立对话
  - 自定义对话标题
  - 按时间排序的历史记录列表
  - 单条消息重试功能（遇到错误自动重发）

- ⚙️ **个性化配置**
  - 全局配置：默认系统提示词、温度、历史长度
  - 单对话配置：为每个对话独立设置参数
  - 实时保存，无需手动确认

- 💾 **本地数据持久化**
  - Room 数据库存储所有消息
  - 数据库自动迁移机制
  - 支持清空对话、删除对话等操作

- 🎨 **精美的 UI 设计**
  - Material Design 3 风格
  - 流畅的打字动画效果
  - 优雅的加载指示器
  - 支持侧滑抽屉菜单

## 📸 应用预览

<div align="center">
  <img src="preview1.jpg" alt="聊天界面预览" width="30%" style="border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.15);">
  <img src="preview2.jpg" alt="菜单界面预览" width="30%" style="border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.15);">
</div>

## 🚀 快速开始

### 环境要求

- **Android Studio**: Hedgehog (2023.1.1) 或更高版本
- **JDK**: 17 或更高版本
- **Android SDK**: API Level 26 (Android 8.0) 或更高
- **Kotlin**: 1.9+

### 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/SunWithCat/NekoChat.git
   cd NekoChat
   ```

2. **导入项目**
   - 使用 Android Studio 打开项目
   - 等待 Gradle 自动同步依赖

3. **构建运行**
   - 连接你的 Android 设备或启动模拟器
   - 点击运行按钮 ▶️ 或使用快捷键 `Shift + F10`

4. **配置 API Key**
   - 打开应用，点击左上角菜单图标
   - 进入"设置"页面
   - 输入你的 Gemini API Key

### 获取 Gemini API Key

1. 访问 [Google AI Studio](https://aistudio.google.com/app/apikey)
2. 使用 Google 账号登录
3. 点击 "Create API Key" 创建密钥
4. 复制生成的 API Key
5. 粘贴到应用的设置页面

## 🛠️ 技术栈

| 技术 | 说明 |
|------|------|
| **语言** | Kotlin 1.9+ |
| **UI 框架** | Jetpack Compose (声明式 UI) |
| **架构模式** | MVVM (ViewModel + StateFlow) |
| **网络请求** | Retrofit 2 + OkHttp 4 |
| **异步处理** | Kotlin Coroutines + Flow |
| **本地存储** | Room Database (SQLite 封装) |
| **依赖注入** | 手动注入（轻量级项目） |
| **数据序列化** | Gson |
| **设计规范** | Material Design 3 |

### 项目结构

```
app/src/main/java/com/sunwithcat/nekochat/
├── data/                    # 数据层
│   ├── local/              # 本地数据库
│   │   ├── AppDatabase.kt
│   │   ├── ChatMessageDao.kt
│   │   └── Migrations.kt
│   ├── model/              # 数据模型
│   ├── remote/             # 网络请求
│   └── repository/         # 数据仓库
├── ui/                      # UI 层
│   ├── chat/               # 聊天页面
│   ├── history/            # 历史记录页面
│   ├── settings/           # 设置页面
│   └── util/               # UI 工具类
└── MainActivity.kt          # 主活动
```

## 🐛 问题反馈

如果你在使用过程中遇到任何问题，或者有新的功能建议，欢迎通过以下方式反馈：

- 📧 提交 [GitHub Issue](https://github.com/SunWithCat/NekoChat/issues)

提交 Issue 时，请包含以下信息：
- Android 版本
- 应用版本
- 问题复现步骤
- 错误日志（如果有）

## ⭐ 支持项目

如果这个项目对你有帮助，请给它一个 ⭐️！

---

<div align="center">

**感谢使用 NekoChat！**  
**希望小苍能给你带来愉快的聊天体验～** 🐾

Made with ❤️ by [SunWithCat](https://github.com/SunWithCat)

</div>
