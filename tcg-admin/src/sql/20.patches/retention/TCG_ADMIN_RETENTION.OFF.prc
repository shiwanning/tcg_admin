BEGIN
	  DBMS_SCHEDULER.disable(name=>'"purge_tac_data_job"', force => TRUE);
	  COMMIT;
END;