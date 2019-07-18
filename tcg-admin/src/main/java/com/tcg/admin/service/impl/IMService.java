package com.tcg.admin.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.tcg.admin.model.Announcement;
import com.tcg.admin.model.StateRelationship;
import com.tcg.admin.model.Task;
import com.tcg.admin.service.StateService;
import com.tcg.admin.service.TaskService;
import com.tcg.admin.to.IMTaskTO;
import com.tcg.im.client.MessageGatewayFactory;
import com.tcg.im.service.PushACMessageService;

@Service
@Transactional
public class IMService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IMService.class);
	
    private PushACMessageService pushACMessageService;
	
	@Autowired
	StateService stateService;
	
	@Autowired
	private TaskService taskService;
	
	@PostConstruct
	public void init() {
		try {
			MessageGatewayFactory factory = new MessageGatewayFactory();
		    pushACMessageService = factory.createPushAcMessageService();
		} catch(Throwable e) {
			LOGGER.error("create EJB pushACMessageService error", e);
		}
	    
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void uploadChanges(Integer taskId, Boolean isNew) {
		Task task = taskService.getTask(taskId);
		
		List<StateRelationship> nextStates = stateService.getStateRelationship(task.getState().getStateId());
		IMTaskTO transferObj = new IMTaskTO();
		transferObj.setMerchantId(task.getMerchantId());
		transferObj.setType(task.getState().getType());
		transferObj.setNextStates(nextStates);
		transferObj.setNotSound(!isNew || (task.getOwner() != null && task.getOwner() > 0));
		
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			String json = ow.writeValueAsString(transferObj);
			if(pushACMessageService!=null){
                pushACMessageService.pushTaskMessage("1", json);
                LOGGER.debug("published object success");
            }else{
            	LOGGER.warn("NO IM HOST FOUND");
            }

		} catch (JsonProcessingException e) {
			LOGGER.error("uploadChanges json error", e);
		}

	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void publishEmergencyAnnouncement(Announcement announcement) {
		
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			String json = ow.writeValueAsString(announcement);
			if(pushACMessageService!=null){
                pushACMessageService.pushTaskMessage("emergency-topic", json);
                LOGGER.debug("publishing emergency success");
            }else{
            	LOGGER.warn("NO IM HOST FOUND FOR ANNOUNCEMENT");
            }

		} catch (JsonProcessingException e) {
			LOGGER.error("publishEmergencyAnnouncement json error", e);
		}

	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void publish(Object objec, String parameter) {

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			String json = ow.writeValueAsString(objec);
			if(pushACMessageService!=null){
				pushACMessageService.pushTaskMessage(parameter, json);
				LOGGER.debug("publishing {} success", parameter);
			}else{
				LOGGER.warn("NO IM HOST FOUND FOR PUBLISH");
			}

		} catch (Exception e) {
			LOGGER.error("publish error", e);
		}

	}
	
}
