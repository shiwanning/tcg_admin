--所有ROLE新增權限

INSERT INTO US_BPM_ROLE_GROUP
            (SEQ_ID,
             ROLE_ID,
             GROUP_NAME)
SELECT SEQ_BPM_ROLE_GROUP.NEXTVAL,
       ROLE_ID,
       GROUP_NAME
FROM   US_ROLE UR,
       US_BPM_GROUP UBRG
WHERE  NOT EXISTS(SELECT 1
                  FROM   US_BPM_ROLE_GROUP
                  WHERE  ROLE_ID = UR.ROLE_ID
                         AND GROUP_NAME = UBRG.GROUP_NAME);