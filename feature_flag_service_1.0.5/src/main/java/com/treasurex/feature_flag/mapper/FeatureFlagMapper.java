package com.treasurex.feature_flag.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.treasurex.feature_flag.model.FeatureFlag;

/**
 * MyBatis mapper interface for FeatureFlag database operations.
 */
@Mapper
public interface FeatureFlagMapper {

	/**
	 * Fetch a FeatureFlag by its unique name.
	 */
	FeatureFlag findByName(@Param("name") String name);

	/**
	 * Retrieve all FeatureFlag records.
	 */
	List<FeatureFlag> findAll();

	/**
	 * Insert a new FeatureFlag record.
	 */
	int save(FeatureFlag featureFlag);

	/**
	 * Update an existing FeatureFlag record.
	 */
	int update(FeatureFlag featureFlag);

	/**
	 * Delete a FeatureFlag record by ID.
	 */
	int deleteById(@Param("id") Long id);
}
