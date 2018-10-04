--------------------------------------------------------
--  已建立檔案 - 星期一-三月-13-2017   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table WF_STATE
--------------------------------------------------------

  CREATE TABLE "TCG_ADMIN"."WF_STATE" 
   (	"STATE_ID" NUMBER(8,0), 
	"STATE_NAME" VARCHAR2(20 BYTE), 
	"TYPE" VARCHAR2(20 BYTE), 
	"DESCRIPTION" NVARCHAR2(500), 
	"MENU_ID" NUMBER(8,0), 
	"PARENT_ID" NUMBER(8,0), 
	"VERSION" NUMBER(8,0) DEFAULT 0, 
	"CREATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP, 
	"UPDATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP, 
	"VIEW_URL_ID" VARCHAR2(500 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Index WF_STATE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TCG_ADMIN"."WF_STATE_PK" ON "TCG_ADMIN"."WF_STATE" ("STATE_ID") 
  ;
--------------------------------------------------------
--  Constraints for Table WF_STATE
--------------------------------------------------------

  ALTER TABLE "TCG_ADMIN"."WF_STATE" MODIFY ("STATE_ID" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."WF_STATE" ADD CONSTRAINT "WF_STATE_PK" PRIMARY KEY ("STATE_ID")
  USING INDEX  ENABLE;
