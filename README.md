# Infinite Craft Mod
[简体中文](docs/README.zh-CN.md)
[繁體中文](docs/README.zh-CHT.md)
[français](docs/README.fr.md)

This is an AI-based Minecraft Infinite Crafting mod that allows players to create new items with the help of AI.

*Unbalanced, may produce illegal item or duplicate items, just for fun!*

## Overview

Infinite Craft is a Fabric mod that adds an innovative crafting system to Minecraft. The mod uses Google's Gemini AI API to generate and handle new item crafting recipes.

## System Requirements

- Minecraft 1.21.10
- Fabric Loader >= 0.17.3
- Java >= 21
- Fabric API

> For single-player or server users in mainland China, if you need to use Gemini through a proxy, add `-Djava.net.useSystemProxies=true` to the JVM arguments

## Configuration
The config file is `config/InfiniteCraft.json`, structure as follows:
```json
{
  "gemini": {
    "api_key": "...",
    "model": "...",
    "note": [
      "Gemini API Is Currently Free!",
      "the `model` can be: ",
      "    gemini-2.5-flash",
      "    gemini-2.5-flash-lite",
      "    or any other ones in https://ai.google.dev/gemini-api/docs/models",
      "For API key, generate here: https://aistudio.google.com/api-keys"
    ]
  },
  "proxy": "...",
  "model": "...",
  "description": {
    "proxy": "Proxy type, could be (none, system)",
    "model": "The model to use, could be (none, gemini, ollama), use none if you only use the mod as a player of a server",
    "gemini": "The gemini config",
    "!!!": "This file will be overridden, so please save your important information somewhere else!"
  }
}
```
+ The proxy options cannot work for some reason so you still need to add `-Djava.net.useSystemProxies=true`
+ The config for ollama is currently not avalible!

## Features

1. AI-assisted crafting
   - Uses Google Gemini AI API to process item crafting
   - Supports dynamically generating new items and crafting recipes
   - Generates intelligent crafting results

2. Configuration
   - Configure AI API keys via a JSON config file
   - Config file location: `config/InfiniteCraft.json`

3. Vanillaify
   - Craft a `Vanillaify` item using this recipe:
    ![Vanillaify Recipe](docs/crafting%20vanillaify.png)
   - Combine it with other items in the crafting table

## Links

- Modrinth: https://modrinth.com/mod/infinite-craft-mod
- Fabric: https://fabricmc.net/