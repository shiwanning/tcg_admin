DELETE FROM US_MENU_ITEM WHERE MENU_ID = 50000;

DELETE FROM US_MENU_ITEM_LABEL WHERE API_ID = 50000;

-- Permission
DELETE FROM US_ROLE_MENU_PERMISSION WHERE MENU_ID = 50000;
