# Infinite Craft Mod
[English](../README.md)
[简体中文](README.zh-CN.md)
[繁體中文](README.zh-CHT.md)

Ceci est un mod Minecraft de crafting infini basé sur l'IA qui permet aux joueurs de créer de nouveaux objets avec l'aide d'une IA.

*Déséquilibré, peut produire des objets illégaux ou des doublons, juste pour le plaisir !*

## Aperçu

Infinite Craft est un mod Fabric qui ajoute un système de crafting innovant à Minecraft. Le mod utilise l'API Gemini de Google pour générer et gérer de nouvelles recettes de crafting.

## Démarrage rapide

1. Téléchargez ce mod depuis [Modrinth](https://modrinth.com/mod/infinite-craft-mod/versions) ou [Github](https://github.com/RedstoneOre/infinite-craft-mod/releases/latest)
2. Utilisez-le dans Minecraft 1.21.10 avec Fabric & Fabric API
3. Créez `config/InfiniteCraft.json` et lancez le jeu
4. Fermez le jeu et ajoutez votre clé API dans `"gemini"."api_key"` dans `config/InfiniteCraft.json`
5. Définissez `model` sur `gemini-2.5-flash`
6. Redémarrez le jeu

## Configuration système

- Minecraft 1.21.10
- Fabric Loader >= 0.17.3
- Java >= 21
- Fabric API

> Pour les utilisateurs en solo ou sur serveur en Chine continentale, si vous avez besoin d'utiliser Gemini via un proxy, ajoutez `-Djava.net.useSystemProxies=true` aux arguments JVM

## Configuration
Le fichier de configuration est `config/InfiniteCraft.json`, structure comme suit :
```json
{
  "gemini": {
    "api_key": "...",
    "model": "...",
    "note": [
      "L'API Gemini est actuellement gratuite !",
      "Le `model` peut être : ",
      "    gemini-2.5-flash",
      "    gemini-2.5-flash-lite",
      "    ou tout autre modèle disponible sur https://ai.google.dev/gemini-api/docs/models",
      "Pour générer une clé API, rendez-vous ici: https://aistudio.google.com/api-keys"
    ]
  },
  "proxy": "...",
  "model": "...",
  "description": {
    "proxy": "Type de proxy, peut être (aucun, système)",
    "model": "Le modèle à utiliser, peut être (aucun, gemini, ollama), utilisez aucun si vous utilisez uniquement le mod en tant que joueur sur un serveur",
    "gemini": "La configuration de gemini",
    "!!!": "Ce fichier sera écrasé, donc veuillez sauvegarder vos informations importantes ailleurs !"
  }
}
```
+ Les options de proxy ne fonctionnent pas pour une raison quelconque, vous devez donc toujours ajouter `-Djava.net.useSystemProxies=true`
+ La configuration pour Ollama n'est actuellement pas disponible !

## Fonctionnalités

1. Crafting assisté par IA
   - Utilise l'API Google Gemini pour traiter le crafting d'objets
   - Prend en charge la génération dynamique de nouveaux objets et recettes de crafting
   - Génère des résultats de crafting intelligents

2. Configuration
   - Configurez les clés d'API de l'IA via un fichier de configuration JSON
   - Emplacement du fichier de configuration : `config/InfiniteCraft.json`

3. Vanillaify
  - Fabriquez un objet `Vanillaify` en utilisant cette recette :
    ![Recette Vanillaify](crafting%20vanillaify.png)
  - Combinez-le avec d'autres objets dans la table de craft

## Liens

- Modrinth: https://modrinth.com/mod/infinite-craft-mod
- Github: https://github.com/RedstoneOre/infinite-craft-mod
- Gemini Docs: https://ai.google.dev/gemini-api/docs