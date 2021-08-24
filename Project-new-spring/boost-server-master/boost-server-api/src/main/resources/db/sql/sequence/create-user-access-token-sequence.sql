BEGIN
  DECLARE
  seqval NUMBER;
  BEGIN
    SELECT NVL(MAX(ACCESS_TOKEN_ID),0)  + 1
    INTO seqval
    FROM USER_ACCESS_TOKEN;
   
  
    execute immediate('CREATE SEQUENCE USER_ACCESS_TOKEN_SEQ MINVALUE 1 START WITH '||seqval||' INCREMENT BY 1 ORDER CACHE 20');
  END;
END;
/