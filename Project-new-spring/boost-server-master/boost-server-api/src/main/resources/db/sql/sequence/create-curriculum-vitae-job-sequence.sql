BEGIN
  DECLARE
  seqval NUMBER;
  BEGIN
    SELECT NVL(MAX(CURRICULUM_VITAE_JOB_ID),0)  + 1
    INTO seqval
    FROM CURRICULUM_VITAE_JOB;
   
  
    execute immediate('CREATE SEQUENCE CURRICULUM_VITAE_JOB_SEQ MINVALUE 1 START WITH '||seqval||' INCREMENT BY 1 ORDER CACHE 20');
  END;
END;
/