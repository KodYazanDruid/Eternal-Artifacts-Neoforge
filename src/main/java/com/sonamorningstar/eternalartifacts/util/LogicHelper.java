package com.sonamorningstar.eternalartifacts.util;

import java.util.List;

public class LogicHelper {
    private LogicHelper() {}

    public static int defaultIf(int check, int undesired, int fallback) {
        if(check == undesired) return fallback;
        return check;
    }

    public static <T> T getOrDefault(List<T> list, int index, T defaultValue) {
        if(index < 0 || index >= list.size()) return defaultValue;
        return list.get(index);
    }

    public static <T> boolean isInList(T[] slots, T predicate) {
        for(T slot : slots) {
            if(predicate.equals(slot)) return true;
        }
        return false;
    }
}
