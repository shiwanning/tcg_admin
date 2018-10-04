--------------------------------------------------------
--  已建立檔案 - 星期三-二月-15-2017   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table US_MERCHANT_MAIL
--------------------------------------------------------

  CREATE TABLE "TCG_ADMIN"."US_MERCHANT_MAIL" 
   (	"MERCHANT_ID" NUMBER(8,0), 
	"MERCHANT_CODE" NVARCHAR2(32), 
	"SMTP_HOST" NVARCHAR2(32), 
	"SMTP_PORT" NVARCHAR2(6), 
	"SMTP_USER" NVARCHAR2(32), 
	"SMTP_PWD" NVARCHAR2(32), 
	"IS_AUTH" NUMBER(2,0), 
	"VERSION" NUMBER(8,0) DEFAULT 0, 
	"CREATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP, 
	"UPDATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP
   ) ;

   COMMENT ON COLUMN "TCG_ADMIN"."US_MERCHANT_MAIL"."MERCHANT_ID" IS '品牌 ID';
   COMMENT ON COLUMN "TCG_ADMIN"."US_MERCHANT_MAIL"."MERCHANT_CODE" IS '品牌代碼';
   COMMENT ON COLUMN "TCG_ADMIN"."US_MERCHANT_MAIL"."SMTP_HOST" IS 'SMTP Host Address';
   COMMENT ON COLUMN "TCG_ADMIN"."US_MERCHANT_MAIL"."SMTP_PORT" IS 'STMP Host Port';
   COMMENT ON COLUMN "TCG_ADMIN"."US_MERCHANT_MAIL"."SMTP_USER" IS 'SMTP User name';
   COMMENT ON COLUMN "TCG_ADMIN"."US_MERCHANT_MAIL"."SMTP_PWD" IS 'SMTP User Password';
   COMMENT ON COLUMN "TCG_ADMIN"."US_MERCHANT_MAIL"."IS_AUTH" IS '是否需要認證';
   COMMENT ON COLUMN "TCG_ADMIN"."US_MERCHANT_MAIL"."VERSION" IS 'Version Control';
   COMMENT ON COLUMN "TCG_ADMIN"."US_MERCHANT_MAIL"."CREATE_TIME" IS '新增時間';
   COMMENT ON COLUMN "TCG_ADMIN"."US_MERCHANT_MAIL"."UPDATE_TIME" IS '更新時間';
   COMMENT ON TABLE "TCG_ADMIN"."US_MERCHANT_MAIL"  IS '品牌 SMTP Settings';
--------------------------------------------------------
--  DDL for Index US_MERCHANT_CODE_UK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TCG_ADMIN"."US_MERCHANT_CODE_UK" ON "TCG_ADMIN"."US_MERCHANT_MAIL" ("MERCHANT_CODE") 
  ;
--------------------------------------------------------
--  DDL for Index US_MERCHANT_MAIL_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TCG_ADMIN"."US_MERCHANT_MAIL_PK" ON "TCG_ADMIN"."US_MERCHANT_MAIL" ("MERCHANT_ID") 
  ;
--------------------------------------------------------
--  Constraints for Table US_MERCHANT_MAIL
--------------------------------------------------------

  ALTER TABLE "TCG_ADMIN"."US_MERCHANT_MAIL" ADD CONSTRAINT "US_MERCHANT_MAIL_PK" PRIMARY KEY ("MERCHANT_ID")
  USING INDEX  ENABLE;
  ALTER TABLE "TCG_ADMIN"."US_MERCHANT_MAIL" MODIFY ("CREATE_TIME" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_MERCHANT_MAIL" MODIFY ("UPDATE_TIME" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_MERCHANT_MAIL" MODIFY ("VERSION" NOT NULL ENABLE);
