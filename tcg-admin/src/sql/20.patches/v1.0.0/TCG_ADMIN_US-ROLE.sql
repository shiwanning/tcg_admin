--------------------------------------------------------
--  已建立檔案 - 星期三-二月-15-2017   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table US_ROLE
--------------------------------------------------------

  CREATE TABLE "TCG_ADMIN"."US_ROLE" 
   (	"ROLE_ID" NUMBER(8,0), 
	"ROLE_NAME" NVARCHAR2(100), 
	"DESCRIPTION" NVARCHAR2(500), 
	"ACTIVE_FLAG" NUMBER(1,0), 
	"VERSION" NUMBER(8,0) DEFAULT 0, 
	"DISPLAY_ORDER" NUMBER(8,0) DEFAULT 0, 
	"CREATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP, 
	"UPDATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP
   ) ;

   COMMENT ON COLUMN "TCG_ADMIN"."US_ROLE"."ROLE_ID" IS '角色ID';
   COMMENT ON COLUMN "TCG_ADMIN"."US_ROLE"."ROLE_NAME" IS '角色名稱';
   COMMENT ON COLUMN "TCG_ADMIN"."US_ROLE"."DESCRIPTION" IS '角色描述';
   COMMENT ON COLUMN "TCG_ADMIN"."US_ROLE"."ACTIVE_FLAG" IS '角色狀態 0:刪除 1:正常';
   COMMENT ON COLUMN "TCG_ADMIN"."US_ROLE"."VERSION" IS '交易控制碼';
   COMMENT ON COLUMN "TCG_ADMIN"."US_ROLE"."DISPLAY_ORDER" IS '顯示順序';
   COMMENT ON TABLE "TCG_ADMIN"."US_ROLE"  IS '角色資料表';
--------------------------------------------------------
--  DDL for Index US_ROLE_NAME_UN
--------------------------------------------------------

  CREATE UNIQUE INDEX "TCG_ADMIN"."US_ROLE_NAME_UN" ON "TCG_ADMIN"."US_ROLE" ("ROLE_NAME") 
  ;
--------------------------------------------------------
--  DDL for Index US_ROLE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TCG_ADMIN"."US_ROLE_PK" ON "TCG_ADMIN"."US_ROLE" ("ROLE_ID") 
  ;
--------------------------------------------------------
--  Constraints for Table US_ROLE
--------------------------------------------------------

  ALTER TABLE "TCG_ADMIN"."US_ROLE" ADD CONSTRAINT "US_ROLE_PK" PRIMARY KEY ("ROLE_ID")
  USING INDEX  ENABLE;
  ALTER TABLE "TCG_ADMIN"."US_ROLE" ADD CONSTRAINT "US_ROLE_NAME_UN" UNIQUE ("ROLE_NAME")
  USING INDEX  ENABLE;
  ALTER TABLE "TCG_ADMIN"."US_ROLE" MODIFY ("VERSION" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_ROLE" MODIFY ("ACTIVE_FLAG" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_ROLE" MODIFY ("DESCRIPTION" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_ROLE" MODIFY ("ROLE_NAME" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_ROLE" MODIFY ("ROLE_ID" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_ROLE" MODIFY ("DISPLAY_ORDER" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_ROLE" MODIFY ("UPDATE_TIME" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_ROLE" MODIFY ("CREATE_TIME" NOT NULL ENABLE);
