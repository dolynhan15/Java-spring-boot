BEGIN
  DECLARE
  seqval NUMBER;
  BEGIN
    SELECT NVL(MAX(ID),0)  + 1
    INTO seqval
    FROM APPOINTMENT_TIME_RANGE;
   
  
    execute immediate('CREATE SEQUENCE APPOINTMENT_TIME_RANGE_SEQ MINVALUE 1 START WITH '||seqval||' INCREMENT BY 1 ORDER CACHE 20');
  END;
END;
/