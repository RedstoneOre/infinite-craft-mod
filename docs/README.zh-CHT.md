# Infinite Craft Mod
[English](../README.md)
[简体中文](README.zh-CN.md)
[français](README.fr.md)

這是一個基於 AI 的 Minecraft 無限合成模組，它允許玩家透過 AI 的幫助來創造新的物品。

*不平衡，可能會產生非法物品或重複物品，僅供娛樂！*

## 概述

Infinite Craft 是一個 Fabric 模組，它為 Minecraft 新增了一個創新的合成系統。該模組使用 Google 的 Gemini AI API 來生成和處理新的物品合成配方。

## 快速開始

1. 從 [Modrinth](https://modrinth.com/mod/infinite-craft-mod/versions) 或 [GitHub](https://github.com/RedstoneOre/infinite-craft-mod/releases/latest) 下載此模組  
2. 在 Minecraft 1.21.10 中使用 Fabric 和 Fabric API 運行  
3. 創建 `config/InfiniteCraft.json` 並啟動遊戲  
4. 關閉遊戲，將你的 [API 密鑰](https://aistudio.google.com/app/api-keys)填入 `config/InfiniteCraft.json` 的 `"gemini"."api_key"` 字段  
5. 將 `model` 設置為 `gemini-2.5-flash`  
6. 重啟遊戲

## 系統需求

- Minecraft 1.21.10
- Fabric Loader >= 0.17.3
- Java >= 21
- Fabric API

> 對於中國大陸的單人遊戲或伺服器使用者，如需透過代理使用 Gemini，請在 JVM 參數中添加 `-Djava.net.useSystemProxies=true`

## 配置
配置檔為 `config/InfiniteCraft.json`, 結構如下:
```json
{
  "gemini": {
    "api_key": "...",
    "model": "...",
    "note": [
      "Gemini API 目前免費！",
      "`model` 可為：",
      "    gemini-2.5-flash",
      "    gemini-2.5-flash-lite",
      "    或其他任意模型： https://ai.google.dev/gemmini-api/docs/models",
      "生成 API 密鑰： https://aistudio.google.com/api-keys"
    ]
  },
  "proxy": "...",
  "model": "...",
  "description": {
    "proxy": "代理類型，可為 (none, system)",
    "model": "模型類型，可為 (none, gemini, ollama)。如果你只在伺服器上作爲玩家使用此模組，可設為 none",
    "gemini": "Gemini 設定",
    "!!!": "此檔案可能會被覆蓋，請將重要資料同時備份至其他位置！"
  }
}
```
+ 代理選項由於*某種原因*無法正常工作，因此您仍然需要添加 `-Djava.net.useSystemProxies=true` 
+ Ollama 的配置當前不可用！

## 功能特點

1. **AI 輔助合成**
   - 使用 Google Gemini AI API 來處理物品合成
   - 支援動態生成新的物品和合成配方
   - 智能合成結果生成

2. **配置**
   - 透過 JSON 配置檔配置 AI API 密鑰
   - 配置檔位置：`config/InfiniteCraft.json`

3. **原版化**
  - 使用以下配方製作`原版化`物品：
    ![原版化配方](crafting%20vanillaify.png)
  - 然後在工作台中將其與其他物品組合

## 連結

- Modrinth : https://modrinth.com/mod/infinite-craft-mod
- Github: https://github.com/RedstoneOre/infinite-craft-mod
- Gemini Docs: https://ai.google.dev/gemini-api/docs