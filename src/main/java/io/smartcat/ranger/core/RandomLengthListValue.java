package io.smartcat.ranger.core;

import io.smartcat.ranger.distribution.Distribution;
import io.smartcat.ranger.distribution.UniformDistribution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generates random length list out of specified values.
 *
 * @param <T> Type which evaluated list will contain.
 */
public class RandomLengthListValue<T> extends Value<List<T>> {

    private final int minLength;
    private final int maxLength;
    private Value<T> elementGenerator;
    private final Distribution distribution;

    /**
     * Constructs random length list value out of specified values.
     *
     * @param minLength Minimum list length
     * @param maxLength Maximum list length
     * @param elementGenerator Element generator
     */
    public RandomLengthListValue(int minLength, int maxLength, Value<T> elementGenerator) {
        this(minLength, maxLength, elementGenerator, new UniformDistribution());
    }

    /**
     * Constructs random length list value out of specified values.
     *
     * @param minLength Minimum list length
     * @param maxLength Maximum list length
     * @param elementGenerator Element generator
     * @param distribution Distribution to use for number of items in list.
     */
    public RandomLengthListValue(int minLength, int maxLength, Value<T> elementGenerator, Distribution distribution) {
        if (elementGenerator == null) {
            throw new IllegalArgumentException("Element Generator cannot be null nor empty.");
        }
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.elementGenerator = elementGenerator;
        this.distribution = distribution;
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    protected void eval() {
        int randomLength = distribution.nextInt(minLength, maxLength);
        List<T> result = new ArrayList<>();
        for (int i = 0; i < randomLength; i++) {
            result.add(elementGenerator.get());
            elementGenerator.reset();
        }
        val = Collections.unmodifiableList(result);
    }
}
