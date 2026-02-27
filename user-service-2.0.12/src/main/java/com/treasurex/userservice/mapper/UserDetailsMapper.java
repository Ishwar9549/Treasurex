package com.treasurex.userservice.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.treasurex.userservice.model.UserDetails;

/**
 * MyBatis mapper interface for UserDetails entity. Provides methods to query,
 * insert, and update user details in the database.
 */
@Mapper
public interface UserDetailsMapper {

	UserDetails findByUserId(@Param("userId") Long userId); //find user by user Id

	int save(UserDetails userDetails); // insert, returns number of rows affected

	int update(UserDetails userDetails); // update, returns number of rows affected
}
//END