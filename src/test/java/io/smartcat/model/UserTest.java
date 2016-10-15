package io.smartcat.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.smartcat.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTest {
	
	@Autowired
	private UserRepository userRepository;
	
	@Before
	public void prepare() {
		userRepository.deleteAll();
	}

	@Test
	public void test() {
		User user = new User();
		user.setUsername("newuser");
		User u = userRepository.save(user);
		
		long numberOfSavedUsers = userRepository.count();
		Assert.assertEquals(1L, numberOfSavedUsers);
	}
	
	@Test
	public void randomBuilderTest() {
		RandomUserBuilder randomUserBuilder = new RandomUserBuilder();
		long numberOfUsers = 50;
		
		
		LocalDateTime oldestBirthDate = LocalDateTime.of(1950, 1, 1,0,0);
		LocalDateTime yougnestBirthDate = LocalDateTime.of(2000, 1, 1,0,0);
		
        long yesterday = LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli();
        long yesterdayPlus1Hour = LocalDateTime.now().minusDays(1).plusHours(1).toInstant(ZoneOffset.UTC).toEpochMilli();
        
        RandomAddressBuilder addressBuilder = new RandomAddressBuilder();
        addressBuilder
        	.randomCityFrom("New York", "Moscow", "London", "Paris", "Budapest", "Las Vegas", "Ulm", "Berlin", "Madrid")
        	.randomHouseNumberRange(0, 50)
        	.randomStreetFrom("snake street", "nuts str", "cat street", "strawberry street");
		
		List<User> users = randomUserBuilder
				.randomUsernameFrom("destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero", "krelac", "pevac", "hardy")
				.randomBirthDateBetween(oldestBirthDate, yougnestBirthDate)
				.randomFavoriteMoviesFrom("Predator", "Comandos", "Terminator 2", "Conan", "Red Sonya") // set random subset
				.randomFirstNameFrom("Alice", "Bob", "Charlie", "Dick", "Eve", "Eleanor", "Boaty")
				.randomLastNameFrom("Avalanche", "Bizmark", "Kok", "McBoatface")
				.randomNumberOfCardsBetween(0, 5)
				.withAddressBuilder(addressBuilder)
				.build(numberOfUsers);
		
		userRepository.save(users);
		long numberOfSavedUsers = userRepository.count();
		Assert.assertEquals(50L, numberOfSavedUsers);
		
	}

}
