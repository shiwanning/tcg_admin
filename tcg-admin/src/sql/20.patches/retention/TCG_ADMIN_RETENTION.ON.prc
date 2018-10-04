BEGIN
	  DBMS_SCHEDULER.enable(name=>'"purge_tac_data_job"');
	  COMMIT;
END;