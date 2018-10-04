--------------------------------------------------------
--  已建立檔案 - 星期三-二月-15-2017   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table US_ROLE_MENU_PERMISSION
--------------------------------------------------------

  CREATE TABLE "TCG_ADMIN"."US_ROLE_MENU_PERMISSION" 
   (	"SEQ_ID" NUMBER(8,0), 
	"ROLE_ID" NUMBER(8,0), 
	"MENU_ID" NUMBER(8,0), 
	"VERSION" NUMBER(8,0) DEFAULT 0, 
	"CREATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP, 
	"UPDATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP
   ) ;

   COMMENT ON COLUMN "TCG_ADMIN"."US_ROLE_MENU_PERMISSION"."SEQ_ID" IS 'pKey';
   COMMENT ON COLUMN "TCG_ADMIN"."US_ROLE_MENU_PERMISSION"."ROLE_ID" IS '角色ID';
   COMMENT ON COLUMN "TCG_ADMIN"."US_ROLE_MENU_PERMISSION"."MENU_ID" IS '選單ID';
   COMMENT ON COLUMN "TCG_ADMIN"."US_ROLE_MENU_PERMISSION"."VERSION" IS '交易控制碼';
   COMMENT ON TABLE "TCG_ADMIN"."US_ROLE_MENU_PERMISSION"  IS '角色與選單權限對應表';
--------------------------------------------------------
--  DDL for Index US_ROLE_MENU_PERMISSION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TCG_ADMIN"."US_ROLE_MENU_PERMISSION_PK" ON "TCG_ADMIN"."US_ROLE_MENU_PERMISSION" ("SEQ_ID") 
  ;
--------------------------------------------------------
--  Constraints for Table US_ROLE_MENU_PERMISSION
--------------------------------------------------------

  ALTER TABLE "TCG_ADMIN"."US_ROLE_MENU_PERMISSION" ADD CONSTRAINT "US_ROLE_MENU_PERMISSION_PK" PRIMARY KEY ("SEQ_ID")
  USING INDEX  ENABLE;
  ALTER TABLE "TCG_ADMIN"."US_ROLE_MENU_PERMISSION" MODIFY ("VERSION" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_ROLE_MENU_PERMISSION" MODIFY ("MENU_ID" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_ROLE_MENU_PERMISSION" MODIFY ("ROLE_ID" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_ROLE_MENU_PERMISSION" MODIFY ("SEQ_ID" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_ROLE_MENU_PERMISSION" MODIFY ("UPDATE_TIME" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_ROLE_MENU_PERMISSION" MODIFY ("CREATE_TIME" NOT NULL ENABLE);
