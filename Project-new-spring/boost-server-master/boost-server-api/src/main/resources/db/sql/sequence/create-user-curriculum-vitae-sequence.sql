BEGIN
  DECLARE
  seqval NUMBER;
  BEGIN
    SELECT NVL(MAX(CURRICULUM_VITAE_ID),0)  + 1
    INTO seqval
    FROM USER_CURRICULUM_VITAE;
   
  
    execute immediate('CREATE SEQUENCE USER_CURRICULUM_VITAE_SEQ MINVALUE 1 START WITH '||seqval||' INCREMENT BY 1 ORDER CACHE 20');
  END;
END;
/