package com.treasurex.login_service.dao;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.treasurex.login_service.entity.ReferalSystem;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional
public class ReferalSystemDaoImpl implements ReferalSystemDao {

	private final SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void save(ReferalSystem referalSystem) {
		getSession().persist(referalSystem);
	}

	@Override
	public void update(ReferalSystem referalSystem) {
		getSession().merge(referalSystem);
	}

	@Override
	public Optional<ReferalSystem> findByEmail(String email) {
		Query<ReferalSystem> query = getSession().createQuery("from ReferalSystem where email = :email",
				ReferalSystem.class);
		query.setParameter("email", email);
		return query.uniqueResultOptional();
	}

	@Override
	public Optional<ReferalSystem> findByReferralCode(String referralCode) {
		Query<ReferalSystem> query = getSession().createQuery("from ReferalSystem where referralCode = :referralCode",
				ReferalSystem.class);
		query.setParameter("referralCode", referralCode);
		return query.uniqueResultOptional();
	}

	@Override
	public List<ReferalSystem> findAll() {
		return getSession().createQuery("from ReferalSystem", ReferalSystem.class).list();
	}

	@Override
	public void delete(ReferalSystem referalSystem) {
		getSession().remove(referalSystem);
	}
}
