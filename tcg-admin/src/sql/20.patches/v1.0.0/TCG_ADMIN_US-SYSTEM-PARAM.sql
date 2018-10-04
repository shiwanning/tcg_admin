--------------------------------------------------------
--  已建立檔案 - 星期三-二月-15-2017   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table US_SYSTEM_PARAM
--------------------------------------------------------

  CREATE TABLE "TCG_ADMIN"."US_SYSTEM_PARAM" 
   (	"PARAM_NAME" VARCHAR2(64 BYTE), 
	"PARAM_VALUE" VARCHAR2(64 BYTE), 
	"DESCRIPTION" NVARCHAR2(128), 
	"VERSION" NUMBER(8,0) DEFAULT 0, 
	"CREATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP, 
	"UPDATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP
   ) ;
--------------------------------------------------------
--  DDL for Index US_SYSTEM_PARAM_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TCG_ADMIN"."US_SYSTEM_PARAM_PK" ON "TCG_ADMIN"."US_SYSTEM_PARAM" ("PARAM_NAME") 
  ;
--------------------------------------------------------
--  Constraints for Table US_SYSTEM_PARAM
--------------------------------------------------------

  ALTER TABLE "TCG_ADMIN"."US_SYSTEM_PARAM" ADD CONSTRAINT "US_SYSTEM_PARAM_PK" PRIMARY KEY ("PARAM_NAME")
  USING INDEX  ENABLE;
  ALTER TABLE "TCG_ADMIN"."US_SYSTEM_PARAM" MODIFY ("VERSION" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_SYSTEM_PARAM" MODIFY ("PARAM_VALUE" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_SYSTEM_PARAM" MODIFY ("PARAM_NAME" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_SYSTEM_PARAM" MODIFY ("UPDATE_TIME" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_SYSTEM_PARAM" MODIFY ("CREATE_TIME" NOT NULL ENABLE);
