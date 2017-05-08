package io.smartcat.ranger.data.generator;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import io.smartcat.ranger.data.generator.rules.DiscreteRuleBoolean;
import io.smartcat.ranger.data.generator.rules.DiscreteRuleString;
import io.smartcat.ranger.data.generator.rules.RangeRuleDate;
import io.smartcat.ranger.data.generator.rules.RangeRuleDouble;
import io.smartcat.ranger.data.generator.rules.RangeRuleFloat;
import io.smartcat.ranger.data.generator.rules.RangeRuleInt;
import io.smartcat.ranger.data.generator.rules.RangeRuleLong;
import io.smartcat.ranger.data.generator.rules.RangeRuleShort;
import io.smartcat.ranger.data.generator.rules.Rule;
import io.smartcat.ranger.data.generator.rules.SubListRule;
import io.smartcat.ranger.data.generator.rules.SubSetRule;
import io.smartcat.ranger.data.generator.rules.UUIDRule;
import io.smartcat.ranger.data.generator.util.Randomizer;
import io.smartcat.ranger.data.generator.util.RandomizerImpl;

/**
 * Generates objects of certain type and specified criteria.
 *
 * @param <T> Type of object which will be generated.
 */
public class ObjectGenerator<T> implements Iterable<T> {

    private final Class<T> objectType;
    private final int numberOfObjects;
    private final Map<String, Rule<?>> fieldRules;
    private final Map<String, ObjectGenerator<?>> nestedObjectGeneratorMap;

    private ObjectGenerator(Builder<T> builder) {
        this.objectType = builder.objectType;
        this.numberOfObjects = builder.numberOfObjects;
        this.fieldRules = new HashMap<>(builder.fieldRules);
        this.nestedObjectGeneratorMap = new HashMap<>(builder.nestedObjectGeneratorMap);
    }

    /**
     * Generates set <code>numberOfObjects</code> of entities of type {@code <T>}.
     *
     * @return List containing generated entities, or empty list, never null.
     */
    public List<T> generateAll() {
        return generate(numberOfObjects);
    }

    /**
     * Generates passed <code>numberOfObjects</code> of entities of type {@code <T>}.
     *
     * @param numberOfObjects Number of entities to be generated.
     * @return List containing generated entities, or empty list, never null.
     */
    public List<T> generate(int numberOfObjects) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < numberOfObjects; i++) {
            final T randomEntity = buildOne();
            result.add(randomEntity);
        }

        return result;
    }

    /**
     * Returns total number of objects that should be generated.
     *
     * @return Total number of objects that should be generated.
     */
    public int getNumberOfObjects() {
        return numberOfObjects;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < numberOfObjects;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T result = buildOne();
                currentIndex++;
                return result;
            }
        };
    }

    private T buildOne() {
        final T instance = initObject();
        fieldRules.forEach((key, value) -> set(instance, key, value.getRandomAllowedValue()));
        nestedObjectGeneratorMap.forEach((key, value) -> set(instance, key, value.buildOne()));
        return instance;
    }

    private T initObject() {
        T instance;
        try {
            instance = objectType.newInstance();
            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the passed value {@code fieldValue} to field {@code fieldName} on the object {@code object}.
     *
     * @param object which field should be set with value
     * @param fieldName name of the field that should be set with value
     * @param fieldValue the value which should be set to field with {@code fieldName}
     * @return true if value is successfully set. Otherwise, false.
     */
    private void set(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, fieldValue);
                return;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        throw new IllegalArgumentException("Unexisting field: " + fieldName);
    }

    /**
     * Builder for {@link ObjectGenerator}.
     *
     * @param <T> Type which {@link ObjectGenerator} will generate.
     */
    public static class Builder<T> {

        private Class<T> objectType;
        private Map<String, Rule<?>> fieldRules = new HashMap<>();
        private int numberOfObjects = -1;
        private Map<String, ObjectGenerator<?>> nestedObjectGeneratorMap = new HashMap<>();
        private Randomizer random = new RandomizerImpl();

        /**
         * Creates Builder that is used with passed {@code objectType}.
         *
         * @param objectType Type which {@link ObjectGenerator} will generate.
         */
        public Builder(Class<T> objectType) {
            this.objectType = objectType;
        }

        /**
         * Creates Builder that is used with passed {@code objectType} and randomizer.
         *
         * @param objectType Type which {@link ObjectGenerator} will generate.
         * @param random Randomizer implementation.
         */
        public Builder(Class<T> objectType, Randomizer random) {
            this.objectType = objectType;
            this.random = random;
        }

        /**
         * Sets the allowed ranges of Shorts for the field with {@code fieldName}. The ranges are defined by array of
         * Shorts <code>S1, S2, ..., Sn</code> such that <code>S1 &lt; S2 &lt; ... &lt; Sn</code> and
         * <code>(n % 2) == 0</code>;
         *
         * The ranges defined by <code>S1, S2, ..., Sn</code> are: <code>[S1, S2), [S3, S4), ..., [Sn-1, Sn)</code>. In
         * each range <code>[Sj, Sk)</code> <code>Sj</code> denotes inclusive start of the range and <code>Sk</code>
         * denotes exclusive end of the range.
         *
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param rangeMarkers Array of Short that denotes the ranges.
         * @return This builder.
         *
         * @throws IllegalArgumentException if {@code rangeMarkers} is not strictly increasing array.
         */
        public Builder<T> randomFromRange(String fieldName, Short... rangeMarkers) {
            checkRangeInput(rangeMarkers);
            fieldRules.put(fieldName, new RangeRuleShort.Builder(random).ranges(rangeMarkers).build());
            return this;
        }

        /**
         * Sets the allowed ranges of Integers for the field with {@code fieldName}. The ranges are defined by an array
         * of Integers <code>I1, I2, ..., In</code> such that <code>I1 &lt; I2 &lt; ... &lt; In</code> and
         * <code>(n % 2) == 0</code>;
         *
         * The ranges defined by <code>I1, I2, ..., In</code> are: <code>[I1, I2), [I3, I4), ..., [In-1, In)</code>. In
         * each range <code>[Ij, Ik)</code> <code>Ij</code> denotes inclusive start of the range and <code>Ik</code>
         * denotes exclusive end of the range.
         *
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param rangeMarkers Array of Integers that denotes the ranges.
         * @return This builder.
         *
         * @throws IllegalArgumentException if {@code rangeMarkers} is not strictly increasing array.
         */
        public Builder<T> randomFromRange(String fieldName, Integer... rangeMarkers) {
            checkRangeInput(rangeMarkers);
            fieldRules.put(fieldName, new RangeRuleInt.Builder(random).ranges(rangeMarkers).build());
            return this;
        }

        /**
         * Sets the allowed ranges of Float for the field with {@code fieldName}. The ranges are defined by an array of
         * Floats <code>F1, F2, ..., Fn</code> such that <code>F1 &lt; F2 &lt; ... &lt; Fn</code> and
         * <code>(n % 2) == 0</code>;
         *
         * The ranges defined by <code>F1, F2, ..., Fn</code> are: <code>[F1, F2), [F3, F4), ..., [F(n-1), Fn)</code>.
         * In each range <code>[Fj, Fk)</code> <code>Fj</code> denotes inclusive start of the range and <code>Fk</code>
         * denotes exclusive end of the range.
         *
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param rangeMarkers Array of Floats that denotes the ranges.
         * @return This builder.
         *
         * @throws IllegalArgumentException if {@code rangeMarkers} is not strictly increasing array.
         */
        public Builder<T> randomFromRange(String fieldName, Float... rangeMarkers) {
            checkRangeInput(rangeMarkers);
            fieldRules.put(fieldName, new RangeRuleFloat.Builder(random).ranges(rangeMarkers).build());
            return this;
        }

        /**
         * Sets the allowed ranges of LocalDateTime for the field with {@code fieldName}. The ranges are defined by an
         * array of LocalDateTime <code>T1, T2, ..., Tn</code> such that <code>T1 &lt; T2 &lt; ... &lt; Tn</code> and
         * <code>(n % 2) == 0</code>;
         *
         * The ranges defined by <code>T1, T2, ..., Tn</code> are: <code>[T1, T2), [T3, T4), ..., [T(n-1), Tn)</code>.
         * In each range <code>[Tj, Tk)</code> <code>Tj</code> denotes inclusive start of the range and <code>Tk</code>
         * denotes exclusive end of the range.
         *
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param rangeMarkers Array of LocalDateTime that denotes the ranges.
         * @return This builder.
         *
         * @throws IllegalArgumentException if {@code rangeMarkers} is not strictly increasing array.
         */
        public Builder<T> randomFromRange(String fieldName, LocalDateTime... rangeMarkers) {
            checkRangeInput(rangeMarkers);
            List<Date> result = Arrays.asList(rangeMarkers).stream().map(marker -> marker.toInstant(ZoneOffset.UTC))
                    .map(instant -> Date.from(instant)).collect(Collectors.toList());
            fieldRules.put(fieldName, new RangeRuleDate.Builder(random).ranges(result).build());
            return this;
        }

        /**
         * Sets the allowed ranges of Dates for the field with {@code fieldName}. The ranges are defined by an array of
         * Dates <code>T1, T2, ..., Tn</code> such that <code>T1 &lt; T2 &lt; ... &lt; Tn</code> and
         * <code>(n % 2) == 0</code>;
         *
         * The ranges defined by <code>T1, T2, ..., Tn</code> are: <code>[T1, T2), [T3, T4), ..., [T(n-1), Tn)</code>.
         * In each range <code>[Tj, Tk)</code> <code>Tj</code> denotes inclusive start of the range and <code>Tk</code>
         * denotes exclusive end of the range.
         *
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param rangeMarkers Array of Dates that denotes the ranges.
         * @return This builder.
         *
         * @throws IllegalArgumentException if {@code rangeMarkers} is not strictly increasing array.
         */
        public Builder<T> randomFromRange(String fieldName, Date... rangeMarkers) {
            checkRangeInput(rangeMarkers);
            fieldRules.put(fieldName, new RangeRuleDate.Builder(random).ranges(rangeMarkers).build());
            return this;
        }

        /**
         * Sets the allowed ranges of Longs for the field with {@code fieldName}. The ranges are defined by an array of
         * Longs <code>L1, L2, ..., Ln</code> such that <code>L1 &lt; L2 &lt; ... &lt; Ln</code> and
         * <code>(n % 2) == 0</code>;
         *
         * The ranges defined by <code>L1, L2, ..., Ln</code> are: <code>[L1, L2), [L3, L4), ..., [L(n-1), Ln)</code>.
         * In each range <code>[Lj, Lk)</code> <code>Lj</code> denotes inclusive start of the range and <code>Lk</code>
         * denotes exclusive end of the range.
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param rangeMarkers Array of Longs that denotes the ranges.
         * @return This builder.
         *
         * @throws IllegalArgumentException if {@code rangeMarkers} is not strictly increasing array.
         */
        public Builder<T> randomFromRange(String fieldName, Long... rangeMarkers) {
            checkRangeInput(rangeMarkers);
            fieldRules.put(fieldName, new RangeRuleLong.Builder(random).ranges(rangeMarkers).build());
            return this;
        }

        /**
         * Sets the allowed ranges of Doubles for the field with {@code fieldName}. The ranges are defined by an array
         * of Doubles <code>D1, D2, ..., Dn</code> such that <code>D1 &lt; D2 &lt; ... &lt; Dn</code> and
         * <code>(n % 2) == 0</code>;
         *
         * The ranges defined by <code>D1, D2, ..., Dn</code> are: <code>[D1, D2), [D3, D4), ..., [D(n-1), Dn)</code>.
         * In each range <code>[Dj, Dk)</code> <code>Dj</code> denotes inclusive start of the range and <code>Dk</code>
         * denotes exclusive end of the range.
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param rangeMarkers Array of Doubles that denotes the ranges.
         * @return This builder.
         *
         * @throws IllegalArgumentException if {@code rangeMarkers} is not strictly increasing array.
         */
        public Builder<T> randomFromRange(String fieldName, Double... rangeMarkers) {
            checkRangeInput(rangeMarkers);
            fieldRules.put(fieldName, new RangeRuleDouble.Builder(random).ranges(rangeMarkers).build());
            return this;
        }

        /**
         * Sets the allowed list of String values for the field with {@code fieldName}.
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param values List of allowed values.
         * @return This builder.
         */
        public Builder<T> randomFrom(String fieldName, String... values) {
            fieldRules.put(fieldName, new DiscreteRuleString.Builder(random).allowedValues(values).build());
            return this;
        }

        /**
         * Declares that the field with {@code fieldName} should be assigned random boolean value.
         *
         * @param fieldName name of the field in the type {@code <T>}.
         * @return This builder.
         */
        public Builder<T> randomBoolean(String fieldName) {
            fieldRules.put(fieldName, new DiscreteRuleBoolean.Builder(random).build());
            return this;
        }

        /**
         * Sets the builder for the property for the field with {@code fieldName}.
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param objectGenerator Instance of {@code ObjectGenerator<?>} for the type of the field with passed
         *            {@code fieldName}.
         * @return This builder.
         */
        public Builder<T> randomWithGenerator(String fieldName, ObjectGenerator<?> objectGenerator) {
            nestedObjectGeneratorMap.put(fieldName, objectGenerator);
            return this;
        }

        /**
         * Sets the allowed list of String values for the field with {@code fieldName} from which a random sub set
         * should be chosen (including empty set).
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param values List of allowed values.
         * @return This builder.
         */
        public Builder<T> randomSubsetFrom(String fieldName, String... values) {
            Set<String> set = new HashSet<>(Arrays.asList(values));
            fieldRules.put(fieldName, new SubSetRule.Builder<String>(random).withValues(set).build());
            return this;
        }

        /**
         * Sets the allowed list of String values for the field with {@code fieldName} from which a random sub list
         * should be chosen (including empty list).
         *
         * @param fieldName name of the field in the type {@code <T>}.
         * @param values List of allowed values.
         * @return This builder.
         */
        public Builder<T> randomSubListFrom(String fieldName, String... values) {
            fieldRules.put(fieldName,
                    new SubListRule.Builder<String>(random).withValues(Arrays.asList(values)).build());
            return this;
        }

        /**
         * Sets the builder for the property for the field with {@code fieldName}. The passed builder will be used to
         * create a list of size between passed {@code lower} value (inclusive) and passed {@code upper} value
         * (exclusive).
         *
         * @param fieldName name of the field in the type {@code <T>}.
         * @param objectGenerator Instance of {@code ObjectGenerator<?>} for the type of the field with passed
         *            {@code fieldName}.
         * @param lower Lower bound of random list size.
         * @param upper Upper bound of random list size.
         * @return This builder.
         */
        public Builder<T> randomSubListWithGenerator(String fieldName, ObjectGenerator<?> objectGenerator, int lower,
                int upper) {
            // TODO as part of issue #37
            throw new UnsupportedOperationException("Not yet implemented");
        }

        /**
         * Declares that field with passed {@code fieldName} is UUID string.
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @return This builder.
         */
        public Builder<T> randomUUID(String fieldName) {
            fieldRules.put(fieldName, new UUIDRule());
            return this;
        }

        /**
         * Sets the number of entities of type {@code <T>} to be generated.
         *
         * @param numberOfObjects Number of objects to be generated.
         * @return This builder.
         */
        public Builder<T> toBeGenerated(int numberOfObjects) {
            this.numberOfObjects = numberOfObjects;
            return this;
        }

        /**
         * Builds {@link ObjectGenerator} based on current builder configuration.
         *
         * @return Instance of {@link ObjectGenerator}.
         */
        public ObjectGenerator<T> build() {
            return new ObjectGenerator<>(this);
        }

        @SafeVarargs
        private static <C extends Comparable<C>> void checkRangeInput(C... markers) {
            List<C> markersList = new LinkedList<>(Arrays.asList(markers));
            if (markersList.size() % 2 != 0) {
                throw new IllegalArgumentException(
                        "Invalid ranges definition. Ranges must be defined with even number of elements.");
            }
            C firstElement = markersList.remove(0);
            for (C c : markersList) {
                if (c.compareTo(firstElement) <= 0) {
                    throw new IllegalArgumentException(
                            "Invalid range bounds. Range definition must be stricly increasing.");
                }
                firstElement = c;
            }
        }
    }
}
