--------------------------------------------------------
--  DDL for Table US_MERCHANT_MENU_CATEGORY
--------------------------------------------------------

  CREATE TABLE "TCG_ADMIN"."US_MERCHANT_MENU_CATEGORY"
   (	"MERCHANT_CODE" NVARCHAR2(32),
	"MENU_CATEGORY_NAME" NVARCHAR2(100),
	"VERSION" NUMBER(8,0) DEFAULT 0,
	"CREATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP,
	"UPDATE_TIME" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP
   );
--------------------------------------------------------
--  DDL for Index US_MERCHANT_MENU_CATEGORY__PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TCG_ADMIN"."US_MERCHANT_MENU_CATEGORY_PK" ON "TCG_ADMIN"."US_MERCHANT_MENU_CATEGORY" ("MERCHANT_CODE", "MENU_CATEGORY_NAME");
--------------------------------------------------------
--  Constraints for Table US_MERCHANT_MENU_CATEGORY
--------------------------------------------------------

  ALTER TABLE "TCG_ADMIN"."US_MERCHANT_MENU_CATEGORY" MODIFY ("MERCHANT_CODE" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_MERCHANT_MENU_CATEGORY" MODIFY ("MENU_CATEGORY_NAME" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_MERCHANT_MENU_CATEGORY" ADD CONSTRAINT "US_MERCHANT_MENU_CATEGORY_PK" PRIMARY KEY ("MERCHANT_CODE", "MENU_CATEGORY_NAME");
