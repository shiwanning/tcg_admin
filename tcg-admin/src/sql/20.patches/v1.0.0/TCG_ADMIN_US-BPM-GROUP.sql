--------------------------------------------------------
--  已建立檔案 - 星期二-三月-14-2017
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table US_BPM_GROUP
--------------------------------------------------------

    CREATE TABLE US_BPM_GROUP
    (
        GROUP_NAME VARCHAR2(32 BYTE),
	    MENU_ID NUMBER(8,0),
	    CREATE_TIME TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP,
	    UPDATE_TIME TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP,
	    VERSION NUMBER(8,0) DEFAULT 0
    );

    COMMENT ON COLUMN US_BPM_GROUP.GROUP_NAME IS '審核功能名稱';
    COMMENT ON COLUMN US_BPM_GROUP.MENU_ID IS '對應的申請功能ID';
    COMMENT ON COLUMN US_BPM_GROUP.CREATE_TIME IS '新增時間';
    COMMENT ON COLUMN US_BPM_GROUP.UPDATE_TIME IS '更新時間';
    COMMENT ON COLUMN US_BPM_GROUP.VERSION IS '版本控制碼';
    COMMENT ON TABLE US_BPM_GROUP IS 'BPM審核功能清單';

--------------------------------------------------------
--  DDL for Index US_BPM_GROUP_PK
--------------------------------------------------------
    CREATE UNIQUE INDEX US_BPM_GROUP_PK ON US_BPM_GROUP (GROUP_NAME);