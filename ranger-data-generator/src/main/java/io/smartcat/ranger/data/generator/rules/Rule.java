package io.smartcat.ranger.data.generator.rules;

/**
 * Rule is used for generating random values of certain type.
 *
 * @param <T> Type of value which will be generated.
 */
public interface Rule<T> {

    /**
     * Get random allowed value.
     *
     * @return Random allowed value.
     */
    T getRandomAllowedValue();

}
