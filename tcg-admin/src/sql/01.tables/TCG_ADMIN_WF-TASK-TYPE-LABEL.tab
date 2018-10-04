--------------------------------------------------------
--  已建立檔案 - 星期三-二月-15-2017   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table WF_TASK_TYPE_LABEL
--------------------------------------------------------

  CREATE TABLE "TCG_ADMIN"."WF_TASK_TYPE_LABEL" 
   (	"TYPE_CODE" VARCHAR2(5 BYTE), 
	"LANG_CODE" VARCHAR2(10 BYTE), 
	"LABEL" VARCHAR2(256 BYTE), 
	"DESCRIPTION" VARCHAR2(512 BYTE)
   ) ;

--------------------------------------------------------
--  DDL for Index TASK_TYPE_LABEL_UNIQ
--------------------------------------------------------

  CREATE UNIQUE INDEX "TCG_ADMIN"."TASK_TYPE_LABEL_UNIQ" ON "TCG_ADMIN"."WF_TASK_TYPE_LABEL" ("TYPE_CODE", "LANG_CODE") 
  ;
--------------------------------------------------------
--  Constraints for Table WF_TASK_TYPE_LABEL
--------------------------------------------------------

  ALTER TABLE "TCG_ADMIN"."WF_TASK_TYPE_LABEL" ADD CONSTRAINT "TASK_TYPE_LABEL_UNIQ" UNIQUE ("TYPE_CODE", "LANG_CODE")
  USING INDEX  ENABLE;
