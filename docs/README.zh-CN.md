# Infinite Craft Mod
[English](../README.md)
[繁體中文](README.zh-CHT.md)
[français](README.fr.md)

这是一个基于AI的Minecraft无限合成模组，它允许玩家通过AI的帮助来创造新的物品。

*不平衡，可能会产生非法物品或重复物品，仅供娱乐！*

## 概述

Infinite Craft是一个Fabric模组，它为Minecraft添加了一个创新的合成系统。该模组使用Google的Gemini AI API来生成和处理新的物品合成配方。

## 快速开始

1. 从 [Modrinth](https://modrinth.com/mod/infinite-craft-mod/versions) 或 [GitHub](https://github.com/RedstoneOre/infinite-craft-mod/releases/latest) 下载此模组  
2. 在 Minecraft 1.21.10 中使用 Fabric 和 Fabric API 运行  
3. 创建 `config/InfiniteCraft.json` 并启动游戏  
4. 关闭游戏，将你的 [API 密钥](https://aistudio.google.com/app/api-keys)填入 `config/InfiniteCraft.json` 的 `"gemini"."api_key"` 字段  
5. 将 `model` 设置为 `gemini-2.5-flash`  
6. 重启游戏

## 系统要求

- Minecraft 1.21.10
- Fabric Loader >= 0.17.3
- Java >= 21
- Fabric API

> 对于中国大陆的单人游戏或服务器用户，如需通过代理使用Gemini，请在JVM参数中添加`-Djava.net.useSystemProxies=true`

## 配置
配置文件为`config/InfiniteCraft.json`, 结构如下:
```json
{
  "gemini": {
    "api_key": "...",
    "model": "...",
    "note": [
      "Gemini API目前免费!",
      "`model`可为: ",
      "    gemini-2.5-flash",
      "    gemini-2.5-flash-lite",
      "    或其他任意模型: https://ai.google.dev/gemini-api/docs/models",
      "生成API密钥: https://aistudio.google.com/api-keys"
    ]
  },
  "proxy": "...",
  "model": "...",
  "description": {
    "proxy": "代理类型, 可以是 (none, system)",
    "model": "模型类型, 可以是 (none, gemini, ollama), 如果你只用这个模组在服务器中玩可以使用none",
    "gemini": "Gemini配置",
    "!!!": "这个文件将会被覆盖, 请将重要数据同时保存在其他位置!"
  }
}
```
+ 代理选项由于*某种原因*无法正常工作，因此您仍然需要添加 `-Djava.net.useSystemProxies=true` 
+ Ollama 的配置当前不可用！

## 功能特点

1. **AI辅助合成**
   - 使用Google Gemini AI API来处理物品合成
   - 支持动态生成新的物品和合成配方
   - 智能合成结果生成

2. **配置**
   - 通过JSON配置文件配置AI API密钥
   - 配置文件位置：`config/InfiniteCraft.json`

3. **原版化**
  - 使用以下配方制作`原版化`物品：
   ![原版化配方](crafting%20vanillaify.png)
  - 然后在工作台中将其与其他物品组合

## 链接

- Modrinth : https://modrinth.com/mod/infinite-craft-mod
- Github: https://github.com/RedstoneOre/infinite-craft-mod
- Gemini Docs: https://ai.google.dev/gemini-api/docs