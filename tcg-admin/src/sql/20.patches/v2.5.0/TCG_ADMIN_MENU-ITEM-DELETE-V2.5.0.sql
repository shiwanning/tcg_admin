DELETE FROM US_ROLE_MENU_PERMISSION WHERE MENU_ID IN (SELECT MENU_ID FROM US_MENU_ITEM WHERE MENU_ID IN ('585','2946','3941','4021','3621','2761','2764','2769','2860','2926','2800','2790','2804') 
OR PARENT_ID IN ('585','2946','3941','4021','3621','2761','2764','2769','2860','2926','2800','2790','2804'));

DELETE FROM US_MENU_ITEM WHERE MENU_ID IN ('585','2946','3941','4021','3621','2761','2764','2769','2860','2926','2800','2790','2804') 
OR PARENT_ID IN ('585','2946','3941','4021','3621','2761','2764','2769','2860','2926','2800','2790','2804');

DELETE FROM US_MENU_ITEM_LABEL WHERE API_ID IN ('585','2946','3941','4021','3621','2761','2764','2769','2860','2926','2800','2790','2804');

update tcg_admin.us_menu_item set tree_level = 2 where parent_id = 9;
delete tcg_admin.us_menu_item where menu_id in (1190,3761,202,2726);
delete tcg_admin.us_menu_item_label where api_id in (1190,3761,202,2726);