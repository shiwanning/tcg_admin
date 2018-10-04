--------------------------------------------------------
--  已建立檔案 - 星期一-三月-13-2017   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table US_CATEGORY
--------------------------------------------------------

  CREATE TABLE "TCG_ADMIN"."US_CATEGORY" 
   (	"CATEGORY_ID" NUMBER(8,0), 
	"CATEGORY_NAME" NVARCHAR2(100), 
	"DESCRIPTION" NVARCHAR2(500), 
	"VERSION" NUMBER(8,0) DEFAULT 0, 
	"CREATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP, 
	"UPDATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP, 
	"CREATE_OPERATOR" VARCHAR2(16 BYTE), 
	"UPDATE_OPERATOR" VARCHAR2(16 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Index US_CATEGORY_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TCG_ADMIN"."US_CATEGORY_PK" ON "TCG_ADMIN"."US_CATEGORY" ("CATEGORY_ID", "CATEGORY_NAME") 
  ;
--------------------------------------------------------
--  Constraints for Table US_CATEGORY
--------------------------------------------------------

  ALTER TABLE "TCG_ADMIN"."US_CATEGORY" ADD CONSTRAINT "US_CATEGORY_PK" PRIMARY KEY ("CATEGORY_ID")
  USING INDEX  ENABLE;
  ALTER TABLE "TCG_ADMIN"."US_CATEGORY" MODIFY ("CATEGORY_ID" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_CATEGORY" MODIFY ("CATEGORY_NAME" NOT NULL ENABLE);
