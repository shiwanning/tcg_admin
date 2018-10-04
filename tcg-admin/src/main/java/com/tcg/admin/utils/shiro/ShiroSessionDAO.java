package com.tcg.admin.utils.shiro;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentMap;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.util.CollectionUtils;

import com.google.common.collect.Maps;

public class ShiroSessionDAO extends AbstractSessionDAO{	

	private static ConcurrentMap<Serializable, Session> sessions = Maps.newConcurrentMap();

	public ShiroSessionDAO() {
		// do nothing
	}

	protected Serializable doCreate(Session session) {
		Serializable sessionId = generateSessionId(session);
		assignSessionId(session, sessionId);
		storeSession(sessionId, session);
		return sessionId;
	}

	protected Session storeSession(Serializable id, Session session) {
		if (id == null) {
			throw new NullPointerException("id argument cannot be null.");
		}
		return sessions.putIfAbsent(id, session);
	}

	protected Session doReadSession(Serializable sessionId) {
		return sessions.get(sessionId);
	}

	public void update(Session session) {
		storeSession(session.getId(), session);
	}

	public void delete(Session session) {
		if (session == null) {
			throw new NullPointerException("session argument cannot be null.");
		}
		Serializable id = session.getId();
		if (id != null) {
			sessions.remove(id);
		}
	}

	public Collection<Session> getActiveSessions() {
		Collection<Session> values = sessions.values();
		if (CollectionUtils.isEmpty(values)) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableCollection(values);
		}
	}
}
