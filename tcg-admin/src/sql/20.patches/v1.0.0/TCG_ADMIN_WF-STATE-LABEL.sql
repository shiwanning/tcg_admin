--------------------------------------------------------
--  已建立檔案 - 星期三-二月-15-2017   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table WF_STATE_LABEL
--------------------------------------------------------

  CREATE TABLE "TCG_ADMIN"."WF_STATE_LABEL" 
   (	"STATE_ID" NUMBER(9,0), 
	"LANG_CODE" VARCHAR2(10 BYTE), 
	"LABEL" VARCHAR2(256 BYTE), 
	"DESCRIPTION" VARCHAR2(512 BYTE)
   ) ;

--------------------------------------------------------
--  DDL for Index STATE_LABEL_UNIQ
--------------------------------------------------------

  CREATE UNIQUE INDEX "TCG_ADMIN"."STATE_LABEL_UNIQ" ON "TCG_ADMIN"."WF_STATE_LABEL" ("STATE_ID", "LANG_CODE") 
  ;
--------------------------------------------------------
--  Constraints for Table WF_STATE_LABEL
--------------------------------------------------------

  ALTER TABLE "TCG_ADMIN"."WF_STATE_LABEL" ADD CONSTRAINT "STATE_LABEL_UNIQ" UNIQUE ("STATE_ID", "LANG_CODE")
  USING INDEX  ENABLE;
