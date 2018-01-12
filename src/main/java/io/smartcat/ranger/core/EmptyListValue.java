package io.smartcat.ranger.core;

import java.util.Collections;
import java.util.List;

/**
 * Generates empty list.
 *
 * @param <T> Type which evaluated list will contain.
 */
public class EmptyListValue<T> extends Value<List<T>> {

    @Override
    public void reset() {
    }

    @Override
    protected void eval() {
        val = Collections.emptyList();
    }
}
