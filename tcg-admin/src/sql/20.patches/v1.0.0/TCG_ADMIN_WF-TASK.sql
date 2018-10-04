--------------------------------------------------------
--  已建立檔案 - 星期三-二月-15-2017   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table WF_TASK
--------------------------------------------------------

  CREATE TABLE "TCG_ADMIN"."WF_TASK" 
   (	"TASK_ID" NUMBER(8,0), 
	"TASK_NAME" NVARCHAR2(100), 
	"DESCRIPTION" NVARCHAR2(500), 
	"STATE_ID" NUMBER(8,0), 
	"MERCHANT_ID" NUMBER(8,0), 
	"OWNER" NUMBER(10,0) DEFAULT 0, 
	"SUB_SYSTEM_TASK" NUMBER(8,0), 
	"STATUS" VARCHAR2(1 BYTE), 
	"VERSION" NUMBER(8,0) DEFAULT 0, 
	"CREATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP, 
	"UPDATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP, 
	"CREATE_OPERATOR" VARCHAR2(16 BYTE), 
	"UPDATE_OPERATOR" VARCHAR2(16 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Index WF_TASK_IDX1
--------------------------------------------------------

  CREATE INDEX "TCG_ADMIN"."WF_TASK_IDX1" ON "TCG_ADMIN"."WF_TASK" ("TASK_ID", "OWNER", "STATE_ID", "MERCHANT_ID") 
  ;
--------------------------------------------------------
--  DDL for Index WF_TASK_IDX2
--------------------------------------------------------

  CREATE INDEX "TCG_ADMIN"."WF_TASK_IDX2" ON "TCG_ADMIN"."WF_TASK" ("TASK_ID", "STATE_ID") 
  ;
--------------------------------------------------------
--  DDL for Index WF_TASK_IDX3
--------------------------------------------------------

  CREATE INDEX "TCG_ADMIN"."WF_TASK_IDX3" ON "TCG_ADMIN"."WF_TASK" ("TASK_ID", "STATE_ID", "MERCHANT_ID") 
  ;
--------------------------------------------------------
--  DDL for Index WF_TASK_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TCG_ADMIN"."WF_TASK_PK" ON "TCG_ADMIN"."WF_TASK" ("TASK_ID") 
  ;
--------------------------------------------------------
--  Constraints for Table WF_TASK
--------------------------------------------------------

  ALTER TABLE "TCG_ADMIN"."WF_TASK" ADD CONSTRAINT "WF_TASK_PK" PRIMARY KEY ("TASK_ID")
  USING INDEX  ENABLE;
  ALTER TABLE "TCG_ADMIN"."WF_TASK" MODIFY ("TASK_ID" NOT NULL ENABLE);
