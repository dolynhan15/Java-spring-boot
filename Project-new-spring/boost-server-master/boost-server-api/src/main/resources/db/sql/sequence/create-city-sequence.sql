BEGIN
  DECLARE
  seqval NUMBER;
  BEGIN
    SELECT NVL(MAX(CITY_ID),0)  + 1
    INTO seqval
    FROM CITY;
   
  
    execute immediate('CREATE SEQUENCE CITY_SEQ MINVALUE 1 START WITH '||seqval||' INCREMENT BY 1 ORDER CACHE 20');
  END;
END;
/