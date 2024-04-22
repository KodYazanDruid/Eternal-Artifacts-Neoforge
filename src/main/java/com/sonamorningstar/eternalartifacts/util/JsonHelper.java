package com.sonamorningstar.eternalartifacts.util;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.GsonHelper;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class JsonHelper {
    private JsonHelper(){}

    public static <T> List<T> parseList(JsonArray array, String name, BiFunction<JsonElement,String,T> mapper) {
        if (array.size() == 0) {
            throw new JsonSyntaxException(name + " must have at least 1 element");
        }
        // build the list
        ImmutableList.Builder<T> builder = ImmutableList.builder();
        for (int i = 0; i < array.size(); i++) {
            builder.add(mapper.apply(array.get(i), name + "[" + i + "]"));
        }
        return builder.build();
    }

    public static <T> List<T> parseList(JsonArray array, String name, Function<JsonObject,T> mapper) {
        return parseList(array, name, (element, s) -> mapper.apply(GsonHelper.convertToJsonObject(element, s)));
    }

    public static <T> List<T> parseList(JsonObject parent, String name, BiFunction<JsonElement,String,T> mapper) {
        return parseList(GsonHelper.getAsJsonArray(parent, name), name, mapper);
    }

    public static <T> List<T> parseList(JsonObject parent, String name, Function<JsonObject,T> mapper) {
        return parseList(GsonHelper.getAsJsonArray(parent, name), name, mapper);
    }
}
