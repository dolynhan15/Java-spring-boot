BEGIN
  DECLARE
  seqval NUMBER;
  BEGIN
    SELECT NVL(MAX(ID),0)  + 1
    INTO seqval
    FROM REFERRAL_CLAIM_GIFT;
   
  
    execute immediate('CREATE SEQUENCE REFERRAL_CLAIM_GIFT_SEQ MINVALUE 1 START WITH '||seqval||' INCREMENT BY 1 ORDER CACHE 20');
  END;
END;
/