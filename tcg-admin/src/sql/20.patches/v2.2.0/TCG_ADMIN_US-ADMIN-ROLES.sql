--------------------------------------------------------
--  已建立檔案 - 星期一-三月-13-2017   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table US_ADMIN_ROLES
--------------------------------------------------------

  CREATE TABLE "TCG_ADMIN"."US_ADMIN_ROLES" 
   (	"ROLE_ID" NUMBER(8,0), 
	"ROLE_NAME" NVARCHAR2(100), 
	"DESCRIPTION" NVARCHAR2(500), 
	"VERSION" NUMBER(8,0) DEFAULT 0, 
	"CREATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP, 
	"UPDATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP) ;
--------------------------------------------------------
--  DDL for Index US_ADMIN_ROLES_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TCG_ADMIN"."US_ADMIN_ROLES_UN" ON "TCG_ADMIN"."US_ADMIN_ROLES" ("ROLE_ID", "ROLE_NAME") 
  ;
--------------------------------------------------------
--  Constraints for Table US_ADMIN_ROLES
--------------------------------------------------------

  ALTER TABLE "TCG_ADMIN"."US_ADMIN_ROLES" ADD CONSTRAINT "US_ADMIN_ROLES_PK" PRIMARY KEY ("ROLE_ID")
  USING INDEX  ENABLE;

