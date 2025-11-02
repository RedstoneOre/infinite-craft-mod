# Infinite Craft Mod
+ Please follow the steps in [Quick Start](#quick-start)!

[简体中文](https://github.com/RedstoneOre/infinite-craft-mod/blob/main/docs/README.zh-CN.md)
[繁體中文](https://github.com/RedstoneOre/infinite-craft-mod/blob/main/docs/README.zh-CHT.md)
[français](https://github.com/RedstoneOre/infinite-craft-mod/blob/main/docs/README.fr.md)

This is an AI-based Minecraft Infinite Crafting mod that allows players to create new items with the help of AI.

*Unbalanced, may produce illegal item or duplicate items, just for fun!*

## Quick Start

1. Download this mod from [Modrinth](https://modrinth.com/mod/infinite-craft-mod/versions) or [Github](https://github.com/RedstoneOre/infinite-craft-mod/releases/latest)
2. Use it in Minecraft 1.21.10 with Fabric & Fabric API
3. Create `config/InfiniteCraft.json` and launch the game
4. Close the game and put your [api key](https://aistudio.google.com/app/api-keys) to `"gemini"."api_key"` in `config/InfiniteCraft.json`
5. Set `model` to `gemini-2.5-flash`
6. Restart the game

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
    ![Vanillaify Recipe](https://github.com/RedstoneOre/infinite-craft-mod/blob/main/docs/crafting%20vanillaify.png?raw=true)
   - Combine it with other items in the crafting table

## Links

- Github: https://github.com/RedstoneOre/infinite-craft-mod
- Gemini Docs: https://ai.google.dev/gemini-api/docs