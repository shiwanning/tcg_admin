
DELETE FROM US_MENU_ITEM WHERE MENU_ID in (5900);
Insert into US_MENU_ITEM (MENU_ID,URL,MENU_NAME,DESCRIPTION,PARENT_ID,IS_LEAF,IS_DISPLAY,DISPLAY_ORDER,TREE_LEVEL,IS_BUTTON,ACCESS_TYPE)
values ('5900', 'lotto_console_web/#/views/play_setting','商户彩票产品', 'Lottery_Game_Management', '3', '1', '1', '70', '2', '0', '0');

DELETE FROM US_MENU_ITEM_LABEL WHERE API_ID in (5900);
Insert into US_MENU_ITEM_LABEL (LANG_CODE,LABEL,API_ID) values ('zh_CN','商户彩票产品',5900);
Insert into US_MENU_ITEM_LABEL (LANG_CODE,LABEL,API_ID) values ('en_US','Lottery Game Management',5900);

-- Permission
DELETE FROM US_ROLE_MENU_PERMISSION WHERE MENU_ID = 5900;
