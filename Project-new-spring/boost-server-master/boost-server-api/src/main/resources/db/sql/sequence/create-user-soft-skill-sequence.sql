BEGIN
  DECLARE
  seqval NUMBER;
  BEGIN
    SELECT NVL(MAX(USER_SOFT_SKILL_ID),0)  + 1
    INTO seqval
    FROM USER_SOFT_SKILL;
   
  
    execute immediate('CREATE SEQUENCE USER_SOFT_SKILL_SEQ MINVALUE 1 START WITH '||seqval||' INCREMENT BY 1 ORDER CACHE 20');
  END;
END;
/