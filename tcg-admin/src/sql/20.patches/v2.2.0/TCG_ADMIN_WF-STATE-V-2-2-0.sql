Insert into TCG_ADMIN.WF_STATE (STATE_ID,STATE_NAME,TYPE,DESCRIPTION,MENU_ID,PARENT_ID,VIEW_URL_ID) values (200,'C-Merchant-Pending','MER','processed create merchant',20200,null,'wps-console-web/#/merchantTask');
Insert into TCG_ADMIN.WF_STATE (STATE_ID,STATE_NAME,TYPE,DESCRIPTION,MENU_ID,PARENT_ID,VIEW_URL_ID) values (201,'C-Merchant-Approved','MER','Approved create merchant',20201,201,'wps-console-web/#/merchantTask');
Insert into TCG_ADMIN.WF_STATE (STATE_ID,STATE_NAME,TYPE,DESCRIPTION,MENU_ID,PARENT_ID,VIEW_URL_ID) values (202,'C-Merchant-Rejected','MER','Reject create merchant',20202,202,'wps-console-web/#/merchantTask');