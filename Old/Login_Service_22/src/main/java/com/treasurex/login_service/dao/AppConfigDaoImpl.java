package com.treasurex.login_service.dao;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.treasurex.login_service.entity.AppConfig;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional
public class AppConfigDaoImpl implements AppConfigDao {

	private final SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void save(AppConfig appConfig) {
		getSession().persist(appConfig);
	}

	@Override
	public void update(AppConfig appConfig) {
		getSession().merge(appConfig);
	}

	@Override
	public void delete(AppConfig appConfig) {
		getSession().remove(appConfig);
	}

	@Override
	public List<AppConfig> findAll() {
		return getSession().createQuery("from AppConfig", AppConfig.class).list();
	}

	@Override
	public Optional<AppConfig> findById(Long id) {
		return Optional.ofNullable(getSession().get(AppConfig.class, id));
	}

	@Override
	public Optional<AppConfig> findByKeyName(String keyName) {
		Query<AppConfig> query = getSession().createQuery("from AppConfig where keyName = :keyName", AppConfig.class);
		query.setParameter("keyName", keyName);
		return query.uniqueResultOptional();
	}
}
