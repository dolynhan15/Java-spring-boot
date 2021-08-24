BEGIN
  DECLARE
  seqval NUMBER;
  BEGIN
    SELECT NVL(MAX(ID),0)  + 1
    INTO seqval
    FROM ASSESSMENT_PERSONAL_QUESTION;
   
  
    execute immediate('CREATE SEQUENCE ASSESSMENT_PERSONAL_QUES_SEQ MINVALUE 1 START WITH '||seqval||' INCREMENT BY 1 ORDER CACHE 20');
  END;
END;
/