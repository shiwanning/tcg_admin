delete from tcg_admin.us_menu_item_label where api_id in (select menu_id from tcg_admin.us_menu_item where parent_id in (2724,2722,2723,3581,605,606,3521,3522,604,302,4141,1032,2320,2280,2900,2920,2982,1190,202,496,500,3564,2940));
delete from tcg_admin.us_menu_item_label where api_id in (2724,2722,2723,3581,605,606,3521,3522,604,302,4141,1032,2320,2280,2900,2920,2982,1190,202,496,500,3564,2940);
