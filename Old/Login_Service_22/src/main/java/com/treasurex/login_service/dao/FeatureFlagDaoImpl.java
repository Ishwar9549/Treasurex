package com.treasurex.login_service.dao;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.treasurex.login_service.entity.FeatureFlag;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional
public class FeatureFlagDaoImpl implements FeatureFlagDao {

	private final SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void save(FeatureFlag featureFlag) {
		getSession().persist(featureFlag);
	}

	@Override
	public void update(FeatureFlag featureFlag) {
		getSession().merge(featureFlag);
	}

	@Override
	public void delete(FeatureFlag featureFlag) {
		getSession().remove(featureFlag);
	}

	@Override
	public List<FeatureFlag> findAll() {
		return getSession().createQuery("from FeatureFlag", FeatureFlag.class).list();
	}

	@Override
	public Optional<FeatureFlag> findById(Long id) {
		return Optional.ofNullable(getSession().get(FeatureFlag.class, id));
	}

	@Override
	public Optional<FeatureFlag> findByName(String name) {
		Query<FeatureFlag> query = getSession().createQuery("from FeatureFlag where name = :name", FeatureFlag.class);
		query.setParameter("name", name);
		return query.uniqueResultOptional();
	}
}
