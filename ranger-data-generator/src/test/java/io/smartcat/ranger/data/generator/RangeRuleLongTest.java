package io.smartcat.ranger.data.generator;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.data.generator.model.User;
import io.smartcat.ranger.data.generator.util.Randomizer;
import io.smartcat.ranger.data.generator.util.RandomizerImpl;

public class RangeRuleLongTest {

    @Test
    public void should_set_number_of_cards_randomly_from_range() {
        Randomizer randomizerMock = new RandomizerImpl();

        ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class, randomizerMock)
                .randomFromRange("numberOfCards", 0L, 5L).toBeGenerated(1000).build();
        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(userGenerator).build();

        List<User> result = aggregatedObjectGenerator.generateAll();
        Assert.assertEquals(1000, result.size());

        for (User u : result) {
            String message = "user should have number of cards in range [0,5), but is was: " + u.getNumberOfCards();
            Assert.assertTrue(message, u.getNumberOfCards() >= 0L && u.getNumberOfCards() < 5L);
        }
    }
}
