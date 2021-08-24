BEGIN
  DECLARE
  seqval NUMBER;
  BEGIN
    SELECT NVL(MAX(ROLE_ID),0)  + 1
    INTO seqval
    FROM ROLE_COMPANY;
   
  
    execute immediate('CREATE SEQUENCE ROLE_SEQ MINVALUE 1 START WITH '||seqval||' INCREMENT BY 1 ORDER CACHE 20');
  END;
END;
/