package com.treasurex.userservice.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.treasurex.userservice.model.User;

/**
 * MyBatis mapper interface for User entity. Provides methods to query, insert,
 * and update users in the database.
 */
@Mapper
public interface UserMapper {

	User findByPhoneNumber(@Param("phoneNumber") String phoneNumber); // find by phone number

	User findByEmail(@Param("email") String email); // find by email id

	User findByUserName(@Param("username") String username); // find by user name

	boolean existsByUserName(@Param("username") String username); // validating user name existance

	int save(User user); // insert, returns number of rows affected

	int update(User user); // update, returns number of rows affected
}
//END