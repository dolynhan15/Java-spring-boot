CREATE OR REPLACE FUNCTION GET_DISPLAY_NAME( FIRST_NAME in nvarchar2, LAST_NAME in nvarchar2, USERNAME in nvarchar2)
RETURN nvarchar2 IS DISPLAY_NAME nvarchar2(1000);
BEGIN
	declare string_text nvarchar2(1000) ;
	begin
		 string_text := FIRST_NAME || LAST_NAME;
		IF string_text IS NULL THEN string_text := USERNAME;
		END IF;
	DISPLAY_NAME :=  string_text;
	RETURN UPPER( DISPLAY_NAME );
	end;
END GET_DISPLAY_NAME;
/