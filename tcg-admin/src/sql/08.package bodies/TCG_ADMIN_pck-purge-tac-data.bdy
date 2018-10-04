create or replace PACKAGE BODY PCK_PURGE_TAC_DATA IS

	i_purge_row_count NUMBER;
	i_purge_part_count NUMBER:= PCK_PURGE_TAC_CONSTANT.PURGE_BATCH_PART_COUNT;
	o_purge_count NUMBER:=PCK_PURGE_TAC_CONSTANT.PURGE_DEFAULT_COUNT;

  CURSOR p_settings IS SELECT * FROM PURGE_DATA_SETTING WHERE ACTIVE = 1 ORDER BY PURGE_SEQUENCE ASC;

	-- **************************
	-- MAIN  ( Entry Pointer )
	-- **************************
	PROCEDURE MAIN IS
      setting PURGE_DATA_SETTING%ROWTYPE;
	BEGIN
		OPEN p_settings;
		LOOP
			FETCH p_settings INTO setting;
			EXIT WHEN p_settings%NOTFOUND;

      IF setting.PURGE_TYPE = 3	THEN
        PURGE_PARTITION (setting);
      ELSIF setting.PURGE_TYPE = 2 THEN
        PURGE_PROCEDURE (setting);
      ELSE
        PURGE_GENERAL (setting);
      END IF;

		END LOOP;
    CLOSE p_settings;
	END MAIN;

	-- **************************
  -- PURGE DATA
	-- **************************
	PROCEDURE PURGE_GENERAL (setting PURGE_DATA_SETTING%ROWTYPE) IS
	  count_sql  VARCHAR2(1000);
	  action_sql VARCHAR2(1000);
	  delete_sql VARCHAR2(1000);

	  strat_time NUMBER :=DBMS_UTILITY.get_time;
    process_time NUMBER :=DBMS_UTILITY.get_time;

    purge_count_from_table NUMBER ;
	  purge_total_count NUMBER ;
    purge_count NUMBER;

	  TYPE T_ROWID_TYPE IS TABLE OF VARCHAR(2000) INDEX BY PLS_INTEGER;
	  l_purge_ids_t T_ROWID_TYPE;

    i_condition VARCHAR2(1000);
    i_table_name VARCHAR2(100);
    i_purge_count_from_table NUMBER;

  BEGIN
	  purge_total_count:= 0;

    i_condition := replace(setting.CONDITION, ':retain_day','trunc(sysdate - '|| setting.RETAIN_DAYS || ')');

    i_table_name := setting.TABLE_NAME;

	  count_sql := 'SELECT COUNT(*)  FROM '||i_table_name||' T WHERE '|| i_condition;

	  action_sql := 'SELECT ROWID FROM '||i_table_name||' T WHERE '|| i_condition || ' AND ROWNUM <= '|| i_purge_part_count;

	  delete_sql := 'DELETE FROM '||i_table_name||' T WHERE ROWID = :1';

	  EXECUTE IMMEDIATE count_sql INTO purge_count_from_table;

		WHILE (purge_total_count < purge_count_from_table ) AND ((process_time-strat_time)/(100*60)) < setting.ACTION_PURGE_MINUTE
  	LOOP
			process_time :=DBMS_UTILITY.get_time;

	    EXECUTE IMMEDIATE action_sql BULK COLLECT INTO l_purge_ids_t;

	    FORALL indx IN l_purge_ids_t.FIRST..l_purge_ids_t.LAST
	      EXECUTE IMMEDIATE delete_sql USING l_purge_ids_t(indx);

		purge_count := SQL%ROWCOUNT;
        IF purge_count = 0 THEN
            EXIT;
        END IF;
	    purge_total_count:=purge_total_count+SQL%ROWCOUNT;

	    COMMIT;

			SAVE_INFO_LOGS(i_table_name, purge_count, PCK_PURGE_TAC_CONSTANT.PURGE_PURGE_SUCCESS, '',(DBMS_UTILITY.get_time-process_time)*10);

  	END LOOP;
    IF setting.INDEX_ACTIVE = 1 THEN
      REBUILD_INDEX( i_table_name );
    END IF;
  EXCEPTION WHEN OTHERS THEN

	  ROLLBACK;
		SAVE_INFO_LOGS(i_table_name, purge_count, PCK_PURGE_TAC_CONSTANT.PURGE_PURGE_FAILED, 'SQLCODE:'||SQLCODE||',SQLERRM:'||SUBSTR(SQLERRM, 1, 100),0);

	END PURGE_GENERAL;

  PROCEDURE PURGE_PARTITION (setting PURGE_DATA_SETTING%ROWTYPE) IS

    cursor p_cursor(i_table_name VARCHAR2) IS select PARTITION_NAME, HIGH_VALUE, NUM_ROWS from user_tab_partitions where TABLE_NAME = i_table_name AND PARTITION_POSITION > 1 ORDER BY PARTITION_POSITION;

    time_str VARCHAR2(4000);
    partition_name VARCHAR(500);
    purge_count NUMBER(10);
    date_obj DATE;

    process_time NUMBER;
  BEGIN

    OPEN p_cursor(setting.table_name);
		LOOP
      process_time :=DBMS_UTILITY.get_time;

      FETCH p_cursor INTO partition_name, time_str, purge_count;

			EXIT WHEN p_cursor%NOTFOUND;

      EXECUTE IMMEDIATE 'SELECT TRUNC('|| time_str || ') FROM DUAL' INTO date_obj;

      IF date_obj < sysdate - setting.RETAIN_DAYS THEN
        EXECUTE IMMEDIATE 'ALTER TABLE '|| setting.table_name ||' DROP PARTITION ('||partition_name||')';

        SAVE_INFO_LOGS(setting.TABLE_NAME, purge_count, PCK_PURGE_TAC_CONSTANT.PURGE_PURGE_SUCCESS, '',(DBMS_UTILITY.get_time-process_time)*10);
      END IF;
		END LOOP;
    CLOSE p_cursor;
    IF setting.INDEX_ACTIVE = 1 THEN
      REBUILD_INDEX( setting.TABLE_NAME );
    END IF;
  EXCEPTION WHEN OTHERS THEN
	  ROLLBACK;
    SAVE_INFO_LOGS(setting.TABLE_NAME, purge_count, PCK_PURGE_TAC_CONSTANT.PURGE_PURGE_FAILED, 'SQLCODE:'||SQLCODE||',SQLERRM:'||SUBSTR(SQLERRM, 1, 100),0);
	END PURGE_PARTITION;

  PROCEDURE PURGE_PROCEDURE (setting PURGE_DATA_SETTING%ROWTYPE) IS
    purge_count NUMBER;
    process_time NUMBER;
    i_condition VARCHAR2(4000);
  BEGIN
    process_time :=DBMS_UTILITY.get_time;

    i_condition := replace(setting.CONDITION, ':retain_day','trunc(sysdate - '|| setting.RETAIN_DAYS || ')');

    EXECUTE IMMEDIATE 'BEGIN '|| setting.TABLE_NAME ||'('|| i_condition || ', :1, :2); END;' USING in setting, out purge_count;

    SAVE_INFO_LOGS(setting.TABLE_NAME, purge_count, PCK_PURGE_TAC_CONSTANT.PURGE_PURGE_SUCCESS, '',(DBMS_UTILITY.get_time-process_time)*10);
  EXCEPTION WHEN OTHERS THEN
	  ROLLBACK;
    SAVE_INFO_LOGS(setting.TABLE_NAME, purge_count, PCK_PURGE_TAC_CONSTANT.PURGE_PURGE_FAILED, 'SQLCODE:'||SQLCODE||',SQLERRM:'||SUBSTR(SQLERRM, 1, 100),0);
	END PURGE_PROCEDURE;

	-- **************************
	-- Rebuild Index
	-- **************************
	PROCEDURE REBUILD_INDEX(i_purge_table_name IN VARCHAR2) IS
		v_select_index_sql VARCHAR2(1024);
		v_rebuild_index_sql VARCHAR2(1024);
		v_table_name  VARCHAR2(1024);
		TYPE R_IDX_NAME IS RECORD( IDX_NAME  ALL_IND_COLUMNS.INDEX_NAME%TYPE );
		TYPE T_IDX_NAME IS TABLE OF R_IDX_NAME INDEX BY BINARY_INTEGER;
		v_idx_name          T_IDX_NAME;
		process_time NUMBER :=DBMS_UTILITY.get_time;
	BEGIN
		v_table_name := i_purge_table_name;
		v_select_index_sql := 'SELECT distinct INDEX_NAME FROM ALL_IND_COLUMNS WHERE TABLE_NAME = upper(:1)  AND TABLE_OWNER = ''TCG_ADMIN'' ';
		dbms_output.put_line('REBUILD Index : ' || i_purge_table_name);
		EXECUTE IMMEDIATE v_select_index_sql  BULK COLLECT INTO v_idx_name USING v_table_name ;

		-- REBUILD index on the TABLE

		FOR i IN 1..v_idx_name.COUNT LOOP
			BEGIN
					-- EXECUTE IMMEDIATE 'ALTER INDEX :1 REBUILD ONLINE'
					-- USING v_idx_name(i).IDX_NAME ;
					dbms_output.put_line( 'Begin ' || v_idx_name(i).IDX_NAME );
					v_rebuild_index_sql :=  'ALTER INDEX ' || v_idx_name(i).IDX_NAME || ' REBUILD ONLINE' ;
					EXECUTE IMMEDIATE  v_rebuild_index_sql;
					dbms_output.put_line( 'Begin ' || v_rebuild_index_sql );
			END;
		END LOOP;

		-- Re gather stats of the Table
		DBMS_STATS.gather_table_stats('TCG_ADMIN',v_table_name,cascade => TRUE);

		dbms_output.put_line('REBUILD Index : ' || i_purge_table_name || ' . Done !');
		SAVE_INFO_LOGS(i_purge_table_name, 0, PCK_PURGE_TAC_CONSTANT.PURGE_PURGE_SUCCESS, 'REBUILD Index',(DBMS_UTILITY.get_time-process_time)*10);
	END REBUILD_INDEX;

	-- **************************
	-- Save Logs
	-- **************************
	PROCEDURE SAVE_INFO_LOGS(i_purge_table_name IN VARCHAR2,  o_purge_count IN NUMBER, i_status IN VARCHAR2, i_remark IN VARCHAR2, i_elapsed_time IN NUMBER) IS
	  msg varchar2(500);
	BEGIN
	  INSERT INTO PURGE_DATA_LOG (ID, TABLE_NAME, DELETE_ROW_COUNT, RESULT, REMARK, ELAPSED_TIME, CREATE_TIME, UPDATE_TIME)
	  VALUES (SEQ_PURGE_DATA_LOG.NEXTVAL, i_purge_table_name, o_purge_count, i_status, i_remark, trunc(i_elapsed_time, 4), sysdate, sysdate);
	  COMMIT;
	EXCEPTION WHEN OTHERS THEN
	  ROLLBACK;
	  msg := 'TABLE Name : '||i_purge_table_name||', DELETE ROW COUNT : '||o_purge_count||', Status : '||i_status||' Elapsed Time'||i_elapsed_time;

	  INSERT INTO PURGE_DATA_LOG (ID, TABLE_NAME, DELETE_ROW_COUNT, RESULT, REMARK, ELAPSED_TIME, CREATE_TIME, UPDATE_TIME)
	  VALUES (SEQ_PURGE_DATA_LOG.NEXTVAL, 'Purge Log', 0, 'Fail', msg, 0, sysdate, sysdate);
	  COMMIT;
	END SAVE_INFO_LOGS;

  END PCK_PURGE_TAC_DATA;