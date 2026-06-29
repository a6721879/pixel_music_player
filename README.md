# 🎮 像素音乐盒 (Pixel Music Box)

基于 **[铜钟音乐](https://tonzhon.whamon.com/)** 的卓越设计理念精心打造，融合 16-bit 像素游戏机美学、极简免登录畅听与本地离线持久化存储的复古怀旧音乐播放器。

---

## 🌟 项目简介

**像素音乐盒** 是一款将经典电玩掌机视觉与现代流媒体音乐无缝融合的 Android 客户端。本项目致敬并吸取了经典音乐聚合搜索平台 **铜钟音乐 (https://tonzhon.whamon.com/)** 的核心思想——即**“打破壁垒，极简畅听”**。在此基础之上，我们创新性地为移动端开发了整套**复古像素美学界面**，并搭载了本地数据持久化机制、动态 Canvas 像素宠物交互以及纯净免登录的搜索与缓存系统。

---

## ✨ 核心特色

### 🍦 1. 复古轻盈的浅色像素美学 (Retro Light Aesthetics)
*   **专属怀旧色彩**：采用温暖舒适的浅米色 GameBoy 风格底色（`#FAF6EE`），搭配高对比度黑实线粗边框、像素实体阴影（Drop Shadow）按键，重现 90 年代掌机和经典 CRT 显示屏的时代质感。
*   **拟物化按钮微动**：所有的分类标签和操作按钮均支持物理弹起/按下的动态位移，提供极富打击感的触觉反馈。

### 🐱 2. 自研 Canvas 动态像素宠物 (Interactive Pixel Mascot)
*   **“像素小盒” 动态相伴**：基于 Jetpack Compose `Canvas` 与 `rememberInfiniteTransition` 纯手工绘制的 12×12 像素矩阵律动音响宠物，正戴着复古耳机，随着节拍快乐地眨眼、泛红并跳跃，为播放界面注入治愈活力。

### 💾 3. 极简畅听 & 离线离线收藏 (Room DB Persistence)
*   **零打扰设计**：无广告、不要求任何账号登录，即开即用，给您最纯粹的听歌环境。
*   **本地安全存档**：采用 Android 现代持久化标准 **Room Database**，一键点击红心即可将心仪歌单、歌曲元数据无损保存在本地 SQLite 数据库中，构建专属您的“离线音乐卡带货架”。

### 🔍 4. 聚合智能搜索 & 多维度推荐 (Omni-Search Engine)
*   支持拼音、歌名、歌手和模糊词汇的多路实时检索，极速响应。
*   精细划分为**热门歌曲**、**最新歌曲**以及**网易推荐**三大维度，满足任何挑剔耳朵的音乐探索需求。

---

## 🛠️ 技术底座与架构

应用遵循 Android 官方推荐的现代应用架构（Modern Android Architecture）最佳实践：

*   **UI 框架**：100% 采用 **Jetpack Compose** 声明式构建，配合 `enableEdgeToEdge()` 实现沉浸式非阻塞窗口边距。
*   **设计系统**：严格遵守 **Material Design 3 (M3)** 指南，自定义符合复古特性的浅色调色板、圆角与排版系统。
*   **数据持久化**：使用 **Room + Coroutines Flow** 实现强类型安全数据读写，全生命周期保障 UI 状态自适应刷新。
*   **多线程并发**：全面采用 Kotlin **Coroutines** 和 **StateFlow** 进行单向数据流（UDF）响应式管理。
*   **网络数据链**：整合 **Retrofit / Ktor** 进行高效、容错的外部音乐服务请求处理。

---

## 🎨 视觉系统：全新自适应图标 (Adaptive App Icon)

我们为 **像素音乐盒** 量身定做并手绘了全新的 Android Vector 矢量自适应图标，告别枯燥的默认 Android 机器人：
*   **背景层 (`ic_launcher_background.xml`)**：温润的复古米色背板上点缀着细密优雅的工程网格刻度线。
*   **前景层 (`ic_launcher_foreground.xml`)**：手绘的双耳戴着蓝色监听耳机、正面带有绿色 GameBoy 点阵背光屏的快乐红掌机，并环绕着跃动的 16-bit 像素红蓝双色音符，使整款应用在手机首屏上独具一格、萌力十足。

---

## 📂 项目结构

```text
/app/src/main/java/com/example/
├── data/
│   ├── db/            # Room Database 离线数据存储
│   ├── model/         # 音乐实体（Song, Artist 等）
│   └── repository/    # 音乐数据源抽象与聚合库
├── ui/
│   ├── components/    # 像素宠物、音轨卡片等复古原子组件
│   ├── screens/       # 探索、搜索、个人收藏等核心交互视窗
│   └── theme/         # 像素风专属浅色 Material Theme 调色板
└── MainActivity.kt    # 单 Activity Type-Safe 导航主轴
```

---

*本项目仅作为开源技术探索与复古视觉交互实践，所有内容均在本地设备运行。再次感谢 [铜钟音乐](https://tonzhon.whamon.com/) 带来的优雅音乐世界灵感。*
