BEGIN
  DECLARE
  seqval NUMBER;
  BEGIN
    SELECT NVL(MAX(ID),0)  + 1
    INTO seqval
    FROM USER_ATTRIBUTE_EVENT;
   
  
    execute immediate('CREATE SEQUENCE USER_ATTRIBUTE_EVENT_SEQ MINVALUE 1 START WITH '||seqval||' INCREMENT BY 1 ORDER CACHE 20');
  END;
END;
/