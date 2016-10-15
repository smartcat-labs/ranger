package io.smartcat.model;

import org.junit.Assert;
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

	@Test
	public void test() {
		User user = new User();
		user.setUsername("newuser");
		User u = userRepository.save(user);
		
		long numberOfSavedUsers = userRepository.count();
		Assert.assertEquals(1L, numberOfSavedUsers);
	}

}
