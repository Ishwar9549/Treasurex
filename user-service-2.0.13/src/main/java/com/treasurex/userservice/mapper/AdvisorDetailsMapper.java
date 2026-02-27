package com.treasurex.userservice.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.treasurex.userservice.model.AdvisorDetails;

/**
 * MyBatis mapper interface for AdvisorDetails entity. Provides methods to
 * query, insert, and update advisor details in the database.
 */
@Mapper
public interface AdvisorDetailsMapper {

	AdvisorDetails findByUserId(@Param("userId") Long userId); // find advisor by user ID

	int save(AdvisorDetails advisorDetails); // insert, returns rows affected

	int update(AdvisorDetails advisorDetails); // update, returns rows affected
}
//END