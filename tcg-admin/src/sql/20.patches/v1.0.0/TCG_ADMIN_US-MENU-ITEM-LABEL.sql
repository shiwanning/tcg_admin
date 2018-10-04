--------------------------------------------------------
--  已建立檔案 - 星期三-二月-15-2017   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table US_MENU_ITEM_LABEL
--------------------------------------------------------

  CREATE TABLE "TCG_ADMIN"."US_MENU_ITEM_LABEL" 
   (	"LANG_CODE" VARCHAR2(5 BYTE), 
	"LABEL" VARCHAR2(258 BYTE), 
	"API_ID" NUMBER(8,0)
   ) ;
--------------------------------------------------------
--  DDL for Index API_LABEL_UNIQ
--------------------------------------------------------

  CREATE UNIQUE INDEX "TCG_ADMIN"."API_LABEL_UNIQ" ON "TCG_ADMIN"."US_MENU_ITEM_LABEL" ("LANG_CODE", "API_ID") 
  ;
--------------------------------------------------------
--  Constraints for Table US_MENU_ITEM_LABEL
--------------------------------------------------------

  ALTER TABLE "TCG_ADMIN"."US_MENU_ITEM_LABEL" ADD CONSTRAINT "API_LABEL_UNIQ" UNIQUE ("LANG_CODE", "API_ID")
  USING INDEX  ENABLE;
  ALTER TABLE "TCG_ADMIN"."US_MENU_ITEM_LABEL" MODIFY ("API_ID" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_MENU_ITEM_LABEL" MODIFY ("LABEL" NOT NULL ENABLE);
  ALTER TABLE "TCG_ADMIN"."US_MENU_ITEM_LABEL" MODIFY ("LANG_CODE" NOT NULL ENABLE);
