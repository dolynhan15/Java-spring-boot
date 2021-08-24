CREATE OR REPLACE TRIGGER SLOT_AFTER_UPDATE_VACANCY
AFTER UPDATE ON VACANCY
FOR EACH ROW
DECLARE
v_id NUMBER;
v_count NUMBER;
v_count_open_slot NUMBER;
v_count_closet_slot NUMBER;
v_new_created_date TIMESTAMP;
v_old_status NUMBER ;
v_new_status NUMBER ;
v_suspend_status NUMBER;
v_open_status NUMBER;
v_seat_open NUMBER;
v_seat_suspended NUMBER;
v_seat_temp_suspended NUMBER;
v_seat_deleted NUMBER;
BEGIN
-- Vacancy seat status: 1-open; 2-closed; 3-suspended; 4-temp_suspended; 5-deleted;
	v_seat_open := 1;
	v_seat_suspended := 3;
	v_seat_temp_suspended := 4;
	v_seat_deleted := 5;
-- Vacancy status: 1-open; 2-suspend
	v_open_status := 1;
	v_suspend_status := 2;
    v_old_status := IS_SUSPEND_RANGE(:OLD.STATUS, :OLD.START_SUSPEND_DATE, :OLD.SUSPEND_DAYS, SYS_EXTRACT_UTC(SYSTIMESTAMP));
    v_new_status := IS_SUSPEND_RANGE(:NEW.STATUS, :NEW.START_SUSPEND_DATE, :NEW.SUSPEND_DAYS, SYS_EXTRACT_UTC(SYSTIMESTAMP));
-- open -> open
    IF (v_new_status = v_open_status
     AND v_old_status = v_open_status
     AND :NEW.NUMBER_OF_SEAT > 0) THEN
        IF :NEW.NUMBER_OF_SEAT > :OLD.NUMBER_OF_SEAT THEN

            v_count_open_slot := :OLD.NUMBER_OF_SEAT + 1;

            FOR i in v_count_open_slot .. :NEW.NUMBER_OF_SEAT LOOP
                SELECT VACANCY_SEAT_SEQ.nextval
                INTO v_id
                FROM dual;

                INSERT INTO VACANCY_SEAT (ID, VACANCY_ID, STATUS, CREATED_DATE, RESPONSIBLE_STAFF_ID)
                VALUES(v_id, :NEW.VACANCY_ID, v_seat_open, SYS_EXTRACT_UTC(SYSTIMESTAMP), :NEW.CONTACT_PERSON_ID);
            END LOOP;

  	 	ELSIF :NEW.NUMBER_OF_SEAT < :OLD.NUMBER_OF_SEAT THEN

            v_count := :OLD.NUMBER_OF_SEAT - :NEW.NUMBER_OF_SEAT;

            UPDATE VACANCY_SEAT
            SET STATUS = v_seat_deleted, DELETED_DATE = SYS_EXTRACT_UTC(SYSTIMESTAMP)
            WHERE ID IN (SELECT ID FROM (SELECT ID FROM VACANCY_SEAT vs_temp WHERE VACANCY_ID = :NEW.VACANCY_ID AND STATUS = v_seat_open ORDER BY vs_temp.CREATED_DATE) WHERE ROWNUM <= v_count);

       END IF;
-- open -> permanent suspend
	ELSIF (:NEW.STATUS = v_suspend_status
     AND v_old_status = v_open_status
     AND :NEW.NUMBER_OF_SEAT > 0) THEN

        UPDATE VACANCY_SEAT
        SET STATUS = v_seat_suspended, SUSPEND_FROM_DATE = :NEW.START_SUSPEND_DATE
        WHERE VACANCY_ID = :NEW.VACANCY_ID AND STATUS = v_seat_open;
-- open -> temporary suspend
   ELSIF (:NEW.STATUS = v_open_status
     AND v_new_status = v_suspend_status
     AND v_old_status = v_open_status
     AND :NEW.NUMBER_OF_SEAT > 0) THEN

     	 v_new_created_date := :NEW.START_SUSPEND_DATE + numtodsinterval(:NEW.SUSPEND_DAYS, 'day' );

         SELECT COUNT(ID) INTO v_count_open_slot
         FROM VACANCY_SEAT
         WHERE VACANCY_ID = :NEW.VACANCY_ID AND STATUS = v_seat_open;

         UPDATE VACANCY_SEAT
         SET STATUS = v_seat_temp_suspended, SUSPEND_FROM_DATE = :NEW.START_SUSPEND_DATE, SUSPENDED_DAYS = :NEW.SUSPEND_DAYS, END_SUSPEND_DATE = v_new_created_date
         WHERE VACANCY_ID = :NEW.VACANCY_ID AND STATUS = v_seat_open;

        FOR i in (1) .. v_count_open_slot LOOP

            SELECT VACANCY_SEAT_SEQ.nextval
            INTO v_id
            FROM dual;

            INSERT INTO VACANCY_SEAT (ID, VACANCY_ID, STATUS, CREATED_DATE, RESPONSIBLE_STAFF_ID)
            VALUES(v_id, :NEW.VACANCY_ID, v_seat_open, v_new_created_date, :NEW.CONTACT_PERSON_ID);

        END LOOP;
-- permanent suspend -> open
    ELSIF (:OLD.STATUS = v_suspend_status
     AND v_new_status = v_open_status
     AND :NEW.NUMBER_OF_SEAT > 0) THEN

        SELECT COUNT(ID) INTO v_count
        FROM VACANCY_SEAT
        WHERE VACANCY_ID = :NEW.VACANCY_ID AND STATUS = v_seat_suspended;


        IF :NEW.NUMBER_OF_SEAT > :OLD.NUMBER_OF_SEAT THEN

            v_count_open_slot := :NEW.NUMBER_OF_SEAT - :OLD.NUMBER_OF_SEAT + v_count;

        ELSIF :NEW.NUMBER_OF_SEAT < :OLD.NUMBER_OF_SEAT THEN

            v_count_open_slot := :OLD.NUMBER_OF_SEAT - :NEW.NUMBER_OF_SEAT + v_count;

        END IF;

        FOR i in (1) .. v_count_open_slot LOOP

            SELECT VACANCY_SEAT_SEQ.nextval
            INTO v_id
            FROM dual;

            INSERT INTO VACANCY_SEAT (ID, VACANCY_ID, STATUS, CREATED_DATE, RESPONSIBLE_STAFF_ID)
            VALUES(v_id, :NEW.VACANCY_ID, v_seat_open, SYS_EXTRACT_UTC(SYSTIMESTAMP), :NEW.CONTACT_PERSON_ID);

        END LOOP;

-- temporary suspend -> open
    ELSIF (:OLD.STATUS = v_open_status
     AND v_new_status = v_open_status
     AND v_old_status = v_suspend_status
     AND :NEW.NUMBER_OF_SEAT > 0) THEN

        UPDATE VACANCY_SEAT
        SET END_SUSPEND_DATE = SYS_EXTRACT_UTC(SYSTIMESTAMP)
        WHERE VACANCY_ID = :NEW.VACANCY_ID AND STATUS = v_seat_temp_suspended;

        UPDATE VACANCY_SEAT
        SET CREATED_DATE = SYS_EXTRACT_UTC(SYSTIMESTAMP)
        WHERE VACANCY_ID = :NEW.VACANCY_ID AND STATUS = v_seat_open;

        IF :NEW.NUMBER_OF_SEAT > :OLD.NUMBER_OF_SEAT THEN

            v_count_open_slot := :NEW.NUMBER_OF_SEAT - :OLD.NUMBER_OF_SEAT;

            FOR i in (1) .. v_count_open_slot LOOP

                SELECT VACANCY_SEAT_SEQ.nextval
                INTO v_id
                FROM dual;

                INSERT INTO VACANCY_SEAT (ID, VACANCY_ID, STATUS, CREATED_DATE, RESPONSIBLE_STAFF_ID)
                VALUES(v_id, :NEW.VACANCY_ID, v_seat_open, SYS_EXTRACT_UTC(SYSTIMESTAMP), :NEW.CONTACT_PERSON_ID);

            END LOOP;

        ELSIF :NEW.NUMBER_OF_SEAT < :OLD.NUMBER_OF_SEAT THEN

            v_count := :OLD.NUMBER_OF_SEAT - :NEW.NUMBER_OF_SEAT;

            UPDATE VACANCY_SEAT
            SET STATUS = v_seat_deleted, DELETED_DATE = SYS_EXTRACT_UTC(SYSTIMESTAMP)
            WHERE ID IN (SELECT ID FROM (SELECT ID FROM VACANCY_SEAT vs_temp WHERE VACANCY_ID = :NEW.VACANCY_ID AND STATUS = v_seat_open ORDER BY vs_temp.CREATED_DATE) WHERE ROWNUM <= v_count);

        END IF;

    END IF;
END SLOT_AFTER_UPDATE_VACANCY;
/