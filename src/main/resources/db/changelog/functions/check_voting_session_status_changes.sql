CREATE OR REPLACE FUNCTION public.check_voting_session_status_changes()
    RETURNS void
    LANGUAGE plpgsql
AS
$$
DECLARE
    session_record RECORD;
BEGIN
    -- start
    FOR session_record IN
        SELECT id
        FROM voting_sessions
        WHERE status = 'WAITING'
          AND first_round_start_time IS NOT NULL
          AND first_round_start_time <= now()
        LOOP
            PERFORM pg_notify('voting_session_start', session_record.id::text);
        END LOOP;

    -- second phase
    FOR session_record IN
        SELECT id
        FROM voting_sessions
        WHERE status = 'VOTING'
          AND second_round_start_time IS NOT NULL
          AND second_round_start_time <= now()
        LOOP
            PERFORM pg_notify('voting_session_second_phase', session_record.id::text);
        END LOOP;

    -- end
    FOR session_record IN
        SELECT id
        FROM voting_sessions
        WHERE status IN ('VOTING', 'FINAL_VOTING')
          AND end_time IS NOT NULL
          AND end_time <= now()
        LOOP
            PERFORM pg_notify('voting_session_end', session_record.id::text);
        END LOOP;
END;
$$;