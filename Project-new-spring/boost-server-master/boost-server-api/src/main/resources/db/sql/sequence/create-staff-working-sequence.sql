BEGIN
  DECLARE
  seqval NUMBER;
  BEGIN
    SELECT NVL(MAX(ID),0)  + 1
    INTO seqval
    FROM STAFF_WORKING;
   
  
    execute immediate('CREATE SEQUENCE STAFF_WORKING_SEQ MINVALUE 1 START WITH '||seqval||' INCREMENT BY 1 ORDER CACHE 20');
  END;
END;
/