--
-- Default Administrator (Passw0rd)
--
DELETE FROM US_MENU_ITEM WHERE MENU_ID = 5700;
Insert into US_MENU_ITEM (MENU_ID,URL,MENU_NAME,DESCRIPTION,PARENT_ID,IS_LEAF,IS_DISPLAY,DISPLAY_ORDER,TREE_LEVEL,VERSION,IS_BUTTON,CREATE_TIME,
UPDATE_TIME,ACCESS_TYPE) values (5700,'lotto_console_web/#/views/game_betting_influences','彩票投注设置','Game_Betting_Influences',5,1,1,52,2,0,0,
sysdate,sysdate, 0);

-- permission
DELETE FROM US_ROLE_MENU_PERMISSION WHERE MENU_ID = 5700;
INSERT INTO US_ROLE_MENU_PERMISSION (SEQ_ID, ROLE_ID, MENU_ID ) VALUES (SEQ_ROLE_MENU_PERMISSION.NEXTVAL, 1, 5700);

DELETE FROM US_MENU_ITEM_LABEL WHERE API_ID = 5700;
Insert into US_MENU_ITEM_LABEL (LANG_CODE,LABEL,API_ID) values ('zh_CN','彩票投注设置',5700);
Insert into US_MENU_ITEM_LABEL (LANG_CODE,LABEL,API_ID) values ('en_US','Game Betting Influences',5700);