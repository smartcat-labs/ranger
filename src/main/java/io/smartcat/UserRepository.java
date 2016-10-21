package io.smartcat;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;

import io.smartcat.model.User;

public interface UserRepository extends CrudRepository<User, Serializable>{

}
