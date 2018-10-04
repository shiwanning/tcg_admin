--------------------------------------------------------
--  �w�إ��ɮ� - �P���T-�G��-15-2017   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table US_MENU_ITEM_AUX
--------------------------------------------------------

  CREATE TABLE "TCG_ADMIN"."US_MENU_ITEM_AUX" 
   (	"MENU_ID" NUMBER(9,0), 
	"API_TYPE" NUMBER(3,0), 
	"API_FAMILY" NUMBER(3,0)
   ) ;

--------------------------------------------------------
--  DDL for Index MENU_ITEM_AUX_UNIQ
--------------------------------------------------------

  CREATE UNIQUE INDEX "TCG_ADMIN"."MENU_ITEM_AUX_UNIQ" ON "TCG_ADMIN"."US_MENU_ITEM_AUX" ("MENU_ID") ;
--------------------------------------------------------
--  Constraints for Table US_MENU_ITEM_AUX
--------------------------------------------------------

  ALTER TABLE "TCG_ADMIN"."US_MENU_ITEM_AUX" ADD CONSTRAINT "MENU_ITEM_AUX_UNIQ" UNIQUE ("MENU_ID");
