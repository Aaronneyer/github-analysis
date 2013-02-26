package com.matrobot.gha.archive.event;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * Deserialize Issue class. 
 * Sometimes it is string and sometimes object.
 */
class IssueDeserializer implements JsonDeserializer<Issue> {
	public Issue deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

		Issue issue = new Issue();
		if(json.isJsonPrimitive()){
			issue.id = json.getAsJsonPrimitive().getAsString();
		}
		else{
			JsonObject jsObj = json.getAsJsonObject();
			JsonElement number = jsObj.get("number");
			if(number != null){
				issue.number = number.getAsString();
			}
		}
	    return issue;
	}
}
