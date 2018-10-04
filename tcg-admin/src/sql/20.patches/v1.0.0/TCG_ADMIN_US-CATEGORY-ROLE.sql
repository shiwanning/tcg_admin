--------------------------------------------------------
--  已建立檔案 - 星期一-三月-13-2017   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table US_CATEGORY_ROLE
--------------------------------------------------------

  CREATE TABLE "TCG_ADMIN"."US_CATEGORY_ROLE" 
   (	"CATEGORY_ROLE_ID" NUMBER(8,0), 
	"ROLE_ID" NUMBER(8,0), 
	"DESCRIPTION" NVARCHAR2(500), 
	"VERSION" NUMBER(8,0) DEFAULT 0, 
	"CREATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP, 
	"UPDATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP, 
	"CREATE_OPERATOR" VARCHAR2(16 BYTE), 
	"UPDATE_OPERATOR" VARCHAR2(16 BYTE), 
	"CATEGORY_ID" NUMBER(8,0)
   ) ;
--------------------------------------------------------
--  DDL for Index US_CATEGORY_ROLE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TCG_ADMIN"."US_CATEGORY_ROLE_PK" ON "TCG_ADMIN"."US_CATEGORY_ROLE" ("CATEGORY_ROLE_ID");
  CREATE UNIQUE INDEX "TCG_ADMIN"."US_CATEGORY_ROLE_UK1" ON "TCG_ADMIN"."US_CATEGORY_ROLE" ("ROLE_ID", "CATEGORY_ID");
--------------------------------------------------------
--  Constraints for Table US_CATEGORY_ROLE
--------------------------------------------------------

  ALTER TABLE "TCG_ADMIN"."US_CATEGORY_ROLE" MODIFY ("CATEGORY_ROLE_ID" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_CATEGORY_ROLE" ADD CONSTRAINT "US_CATEGORY_ROLE_PK" PRIMARY KEY ("CATEGORY_ROLE_ID")
  USING INDEX  ENABLE;
  ALTER TABLE "TCG_ADMIN"."US_CATEGORY_ROLE" MODIFY ("CATEGORY_ID" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_CATEGORY_ROLE" ADD CONSTRAINT "US_CATEGORY_ROLE_UK1" UNIQUE ("ROLE_ID", "CATEGORY_ID")
  USING INDEX  ENABLE;
  ALTER TABLE "TCG_ADMIN"."US_CATEGORY_ROLE" MODIFY ("CATEGORY_ID" NOT NULL ENABLE);
