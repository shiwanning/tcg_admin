CREATE TABLE WF_STATE_REL(
  SYSID NUMBER,
  FROM_STATE_ID NUMBER(3,0),
  TO_STATE_ID NUMBER(3,0),
  CONSTRAINT WF_STATE_REL_PK PRIMARY KEY(SYSID)
);
