CREATE OR REPLACE FUNCTION INTERVAL_OF_GIFT_BY_DURATION
(ACTIVE_DATE TIMESTAMP, EXPIRED_DATE TIMESTAMP, NOW_TIME TIMESTAMP)
RETURN NUMBER
IS
DURATION  INTERVAL DAY(9) TO SECOND(6);
s VARCHAR2(26);
days_s VARCHAR2(26);
time_s VARCHAR2(26);
N NUMBER;
BEGIN
	DURATION := NOW_TIME - NOW_TIME;
	IF NOW_TIME <= EXPIRED_DATE AND NOW_TIME >= ACTIVE_DATE THEN
      DURATION := EXPIRED_DATE - NOW_TIME;
  	ELSIF  NOW_TIME < ACTIVE_DATE THEN
      DURATION := ACTIVE_DATE - NOW_TIME;
  	ELSE
      DURATION := NOW_TIME - EXPIRED_DATE;
 	END IF;
 	s := TO_CHAR(DURATION);
	days_s := SUBSTR(s,2,INSTR(s,' ')-2);
	time_s := SUBSTR(s,2+LENGTH(days_s)+1);
	N := 86400*TO_NUMBER(days_s) + 3600*TO_NUMBER(SUBSTR(time_s,1,2)) + 60*TO_NUMBER(SUBSTR(time_s,4,2)) + TO_NUMBER(SUBSTR(time_s,7));
	IF SUBSTR(s,1,1) = '-' THEN
		N := - N;
	END IF;
  	RETURN N;
END INTERVAL_OF_GIFT_BY_DURATION;
/