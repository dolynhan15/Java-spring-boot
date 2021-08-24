BEGIN
  DECLARE
  seqval NUMBER;
  BEGIN
    SELECT NVL(MAX(ASSESSMENT_LEVEL_ID),0)  + 1
    INTO seqval
    FROM ASSESSMENT_LEVEL;
   
  
    execute immediate('CREATE SEQUENCE ASSESSMENT_LEVEL_SEQ MINVALUE 1 START WITH '||seqval||' INCREMENT BY 1 ORDER CACHE 20');
  END;
END;
/