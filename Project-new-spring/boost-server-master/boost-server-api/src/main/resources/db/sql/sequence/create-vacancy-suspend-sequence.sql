BEGIN
  DECLARE
  seqval NUMBER;
  BEGIN
    SELECT NVL(MAX(VACANCY_SUSPEND_ID),0)  + 1
    INTO seqval
    FROM VACANCY_SUSPEND;
   
  
    execute immediate('CREATE SEQUENCE VACANCY_SUSPEND_SEQ MINVALUE 1 START WITH '||seqval||' INCREMENT BY 1 ORDER CACHE 20');
  END;
END;
/