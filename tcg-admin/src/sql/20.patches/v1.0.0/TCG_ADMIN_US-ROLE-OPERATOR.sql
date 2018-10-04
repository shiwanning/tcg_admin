--------------------------------------------------------
--  已建立檔案 - 星期三-二月-15-2017   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table US_ROLE_OPERATOR
--------------------------------------------------------

  CREATE TABLE "TCG_ADMIN"."US_ROLE_OPERATOR" 
   (	"SEQ_ID" NUMBER(10,0), 
	"OPERATOR_ID" NUMBER(10,0), 
	"ROLE_ID" NUMBER(8,0), 
	"VERSION" NUMBER(8,0) DEFAULT 0,
	"CREATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP, 
	"UPDATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP
   ) ;

   COMMENT ON COLUMN "TCG_ADMIN"."US_ROLE_OPERATOR"."SEQ_ID" IS '主鍵';
   COMMENT ON COLUMN "TCG_ADMIN"."US_ROLE_OPERATOR"."OPERATOR_ID" IS '管理員ID';
   COMMENT ON COLUMN "TCG_ADMIN"."US_ROLE_OPERATOR"."ROLE_ID" IS '角色ID';
   COMMENT ON COLUMN "TCG_ADMIN"."US_ROLE_OPERATOR"."VERSION" IS '交易控制碼';
   COMMENT ON TABLE "TCG_ADMIN"."US_ROLE_OPERATOR"  IS '角色與後台管理員對應表';
--------------------------------------------------------
--  DDL for Index US_ROLE_OPERATOR_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TCG_ADMIN"."US_ROLE_OPERATOR_PK" ON "TCG_ADMIN"."US_ROLE_OPERATOR" ("SEQ_ID") 
  ;
--------------------------------------------------------
--  Constraints for Table US_ROLE_OPERATOR
--------------------------------------------------------

  ALTER TABLE "TCG_ADMIN"."US_ROLE_OPERATOR" ADD CONSTRAINT "US_ROLE_OPERATOR_PK" PRIMARY KEY ("SEQ_ID")
  USING INDEX  ENABLE;
  ALTER TABLE "TCG_ADMIN"."US_ROLE_OPERATOR" MODIFY ("VERSION" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_ROLE_OPERATOR" MODIFY ("ROLE_ID" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_ROLE_OPERATOR" MODIFY ("OPERATOR_ID" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_ROLE_OPERATOR" MODIFY ("SEQ_ID" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_ROLE_OPERATOR" MODIFY ("UPDATE_TIME" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_ROLE_OPERATOR" MODIFY ("CREATE_TIME" NOT NULL ENABLE);
