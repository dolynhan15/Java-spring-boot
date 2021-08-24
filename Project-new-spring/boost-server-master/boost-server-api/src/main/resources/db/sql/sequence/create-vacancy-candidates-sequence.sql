BEGIN
  DECLARE
  seqval NUMBER;
  BEGIN
    SELECT NVL(MAX(VACANCY_CANDIDATE_ID),0)  + 1
    INTO seqval
    FROM VACANCY_CANDIDATES;
   
  
    execute immediate('CREATE SEQUENCE VACANCY_CANDIDATES_SEQ MINVALUE 1 START WITH '||seqval||' INCREMENT BY 1 ORDER CACHE 20');
  END;
END;
/