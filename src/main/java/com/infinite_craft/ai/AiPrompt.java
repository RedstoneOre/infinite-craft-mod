package com.infinite_craft.ai;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AiPrompt {
	public static JsonObject promptJsonSchema = JsonParser.parseString("""
		{
			"type": "object",
			"required": [
				"success",
				"itemNbt"
			],
			"properties": {
				"success": {
					"type": "boolean"
				},
				"itemNbt": {
					"type": "string"
				},
				"element": {
					"type": "object",
					"properties": {
						"name": {
							"type": "string",
							"description": "The name of the element"
						},
						"emoji": {
							"type": "string",
							"description": "The emoji(s) most close to the element",
							"minLength": 1,
							"maxLength": 3
						},
						"color": {
							"type": "string",
							"description": "A string describe a color, can be #RRGGBB or minecraft color words like aqua"
						}
					}
				}
			}
		}
		""").getAsJsonObject();
}
