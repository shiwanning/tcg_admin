package com.tcg.admin.controller.core.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcgAdminSessionListener implements SessionListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(TcgAdminSessionListener.class);

	@Override
	public void onStart(Session session) {
		LOGGER.debug("The session is started : {}", session.getId());
	}

	@Override
	public void onStop(Session session) {
		LOGGER.debug("The session is stoped : {}", session.getId());
	}

	@Override
	public void onExpiration(Session session) {
		// TODO Logout of services
		LOGGER.debug("The session is expired : {}", session.getId());
        Subject.Builder builder = new Subject.Builder();
        builder.sessionId(session.getId().toString());
        Subject subject = builder.buildSubject();
        subject.logout();


	}
}
