BEGIN
  DECLARE
  seqval NUMBER;
  BEGIN
    SELECT NVL(MAX(VACANCY_ID),0)  + 1
    INTO seqval
    FROM VACANCY;
   
  
    execute immediate('CREATE SEQUENCE VACANCY_SEQ MINVALUE 1 START WITH '||seqval||' INCREMENT BY 1 ORDER CACHE 20');
  END;
END;
/