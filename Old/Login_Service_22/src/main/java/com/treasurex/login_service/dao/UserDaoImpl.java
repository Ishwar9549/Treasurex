package com.treasurex.login_service.dao;

import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.treasurex.login_service.entity.User;

@Repository
@Transactional(readOnly = true)
public class UserDaoImpl implements UserDao {

	private final SessionFactory sessionFactory;

	public UserDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Session currentSession() {
		return sessionFactory.getCurrentSession();
	}

	public User save(User user) {
		Session session = currentSession();
		if (user.getId() == null) {
			session.persist(user); // new entity
		} else {
			session.merge(user); // detached or existing entity
		}
		return user;
	}

	@Override
	public Optional<User> findByUserId(String userId) {
		User user = currentSession().createQuery("from User u where u.userId = :userId", User.class)
				.setParameter("userId", userId).uniqueResult();
		return Optional.ofNullable(user);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		User user = currentSession().createQuery("from User u where u.email = :email", User.class)
				.setParameter("email", email).uniqueResult();
		return Optional.ofNullable(user);
	}

	@Override
	public Optional<User> findByPhoneNumber(String phoneNumber) {
		User user = currentSession().createQuery("from User u where u.phoneNumber = :phone", User.class)
				.setParameter("phone", phoneNumber).uniqueResult();
		return Optional.ofNullable(user);
	}

	@Override
	public boolean existsByUserId(String userId) {
		Long count = currentSession().createQuery("select count(u.id) from User u where u.userId = :userId", Long.class)
				.setParameter("userId", userId).uniqueResult();
		return count != null && count > 0;
	}
}
