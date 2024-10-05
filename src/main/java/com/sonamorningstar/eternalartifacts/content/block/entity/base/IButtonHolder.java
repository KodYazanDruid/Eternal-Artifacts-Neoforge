package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import java.util.Map;
import java.util.function.Consumer;

public interface IButtonHolder {

    Map<Integer, Consumer<Integer>> buttonConsumerMap();
}
