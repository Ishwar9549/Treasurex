package com.treasurex.userservice.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.treasurex.userservice.model.BusinessDetails;

/**
 * MyBatis mapper interface for BusinessDetails entity. Provides methods to
 * query, insert, and update business details in the database.
 */
@Mapper
public interface BusinessDetailsMapper {

	BusinessDetails findByUserId(@Param("userId") Long userId); // find business details by user ID

	int save(BusinessDetails businessDetails); // insert, returns rows affected

	int update(BusinessDetails businessDetails); // update, returns rows affected
}
//END