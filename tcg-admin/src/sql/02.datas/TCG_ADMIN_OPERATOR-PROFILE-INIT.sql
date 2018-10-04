--------------------------------------------------------
--  已建立檔案 - 星期五-三月-10-2017   
--------------------------------------------------------
Insert into US_OPERATOR_PROFILE (OPERATOR_ID,PASSWD_LAST_MODIFY_DATE,MOBILE_NO,EMAIL,LAST_LOGIN_IP,NO_ACTIVE,PAGE_SIZE,VERSION,LAST_LOGIN_TIME,LAST_LOGOUT_TIME,CREATE_TIME,UPDATE_TIME,BIRTHDAY,LOGIN)
select operator_id,null,null,'',null,null,20,0,null,null,sysdate,sysdate,null,null from US_OPERATOR where operator_id not in (select operator_id from us_operator_profile);