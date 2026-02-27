package com.treasurex.app_config.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.treasurex.app_config.model.AppConfig;

/**
 * MyBatis mapper interface for AppConfig database operations.
 */
@Mapper
public interface AppConfigMapper {

	/**
	 * Fetch AppConfig by unique key name.
	 */
	AppConfig findByKeyName(@Param("keyName") String keyName);

	/**
	 * Retrieve all AppConfig records.
	 */
	List<AppConfig> findAll();

	/**
	 * Insert a new AppConfig record.
	 */
	int insert(AppConfig appConfig);

	/**
	 * Update an existing AppConfig record.
	 */
	int update(AppConfig appConfig);

	/**
	 * Delete AppConfig by its primary ID.
	 */
	int deleteById(@Param("id") Long id);
}
//END