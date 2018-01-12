package io.smartcat.ranger.core;

import java.util.Collections;
import java.util.Map;

/**
 * Generates empty map.
 *
 * @param <K> Type of key within map.
 * @param <V> Type of value within map.
 */
public class EmptyMapValue<K, V> extends Value<Map<K, V>> {

    @Override
    public void reset() {
    }

    @Override
    protected void eval() {
        val = Collections.emptyMap();
    }
}
