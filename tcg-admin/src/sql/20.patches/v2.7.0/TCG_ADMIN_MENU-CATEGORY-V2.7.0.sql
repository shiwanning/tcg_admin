--------------------------------------------------------
--  DDL for Table US_MENU_CATEGORY
--------------------------------------------------------

  CREATE TABLE "TCG_ADMIN"."US_MENU_CATEGORY"
   (	"CATEGORY_NAME" NVARCHAR2(100),
	"VERSION" NUMBER(8,0) DEFAULT 0,
	"CREATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP,
	"UPDATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP,
	"CREATE_OPERATOR" VARCHAR2(16 BYTE),
	"UPDATE_OPERATOR" VARCHAR2(16 BYTE)
   );
--------------------------------------------------------
--  DDL for Index US_MENU_CATEGORY_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TCG_ADMIN"."US_MENU_CATEGORY_PK" ON "TCG_ADMIN"."US_MENU_CATEGORY" ("CATEGORY_NAME") ;

--------------------------------------------------------
--  Constraints for Table US_MENU_CATEGORY
--------------------------------------------------------

  ALTER TABLE "TCG_ADMIN"."US_MENU_CATEGORY" MODIFY ("CATEGORY_NAME" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_MENU_CATEGORY" ADD CONSTRAINT "US_MENU_CATEGORY_PK" PRIMARY KEY ("CATEGORY_NAME");

  INSERT INTO "TCG_ADMIN"."US_MENU_CATEGORY" (CATEGORY_NAME, VERSION, CREATE_TIME, CREATE_OPERATOR) VALUES ('SYSTEM', '0', SYSDATE, 'administrator')
