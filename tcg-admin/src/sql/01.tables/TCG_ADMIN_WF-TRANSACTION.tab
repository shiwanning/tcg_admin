--------------------------------------------------------
--  已建立檔案 - 星期三-二月-15-2017   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table WF_TRANSACTION
--------------------------------------------------------

  CREATE TABLE "TCG_ADMIN"."WF_TRANSACTION" 
   (	"TRANS_ID" NUMBER(20,0),
	"TASK_ID" NUMBER(8,0), 
	"OWNER" NUMBER(10,0) DEFAULT 0, 
	"STATE_ID" NUMBER(8,0), 
	"STATE_NAME" VARCHAR2(20 BYTE), 
	"MERCHANT_ID" NUMBER(8,0), 
	"VERSION" NUMBER(8,0) DEFAULT 0, 
	"CREATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP, 
	"UPDATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP, 
	"CREATE_OPERATOR" VARCHAR2(16 BYTE), 
	"UPDATE_OPERATOR" VARCHAR2(16 BYTE), 
	"COMMENTS" VARCHAR2(500 BYTE), 
	"TRANSACTION_TYPE" VARCHAR2(20 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Index WF_TRANSACTION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TCG_ADMIN"."WF_TRANSACTION_PK" ON "TCG_ADMIN"."WF_TRANSACTION" ("TRANS_ID") 
  ;
--------------------------------------------------------
--  DDL for Index WF_TRANSACTION_IDX1
--------------------------------------------------------

  CREATE INDEX "TCG_ADMIN"."WF_TRANSACTION_IDX1" ON "TCG_ADMIN"."WF_TRANSACTION" ("TASK_ID", "OWNER", "STATE_ID", "MERCHANT_ID") 
  ;
--------------------------------------------------------
--  DDL for Index WF_TRANSACTION_IDX3
--------------------------------------------------------

  CREATE INDEX "TCG_ADMIN"."WF_TRANSACTION_IDX3" ON "TCG_ADMIN"."WF_TRANSACTION" ("TASK_ID", "STATE_ID") 
  ;
--------------------------------------------------------
--  DDL for Index WF_TRANSACTION_IDX2
--------------------------------------------------------

  CREATE INDEX "TCG_ADMIN"."WF_TRANSACTION_IDX2" ON "TCG_ADMIN"."WF_TRANSACTION" ("TASK_ID", "STATE_ID", "MERCHANT_ID") 
  ;
--------------------------------------------------------
--  Constraints for Table WF_TRANSACTION
--------------------------------------------------------

  ALTER TABLE "TCG_ADMIN"."WF_TRANSACTION" MODIFY ("OWNER" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."WF_TRANSACTION" MODIFY ("MERCHANT_ID" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."WF_TRANSACTION" MODIFY ("STATE_NAME" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."WF_TRANSACTION" MODIFY ("STATE_ID" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."WF_TRANSACTION" MODIFY ("TASK_ID" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."WF_TRANSACTION" ADD CONSTRAINT "WF_TRANSACTION_PK" PRIMARY KEY ("TRANS_ID")
  USING INDEX  ENABLE;
  ALTER TABLE "TCG_ADMIN"."WF_TRANSACTION" MODIFY ("TRANS_ID" NOT NULL ENABLE);
