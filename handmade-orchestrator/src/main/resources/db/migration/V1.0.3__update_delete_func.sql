DROP FUNCTION IF EXISTS delete_processing(UUID);
DROP FUNCTION IF EXISTS delete_session(UUID);
DROP FUNCTION IF EXISTS delete_status(VARCHAR);
DROP FUNCTION IF EXISTS delete_session_if_files(UUID);

DROP FUNCTION IF EXISTS update_processing_status(UUID, VARCHAR);

CREATE FUNCTION delete_processing (
   in_session UUID
) RETURNS void AS $$
    DECLARE
        var_session_id INT;
    BEGIN
        var_session_id := (SELECT s.session_id from HO_SESSION s where s.session_name = in_session);
        DELETE FROM HO_PROCESSING
        WHERE session_id = var_session_id;
    END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION delete_session (
   in_session UUID
) RETURNS void AS $$
    BEGIN
        DELETE FROM HO_SESSION
        WHERE session_name = in_session;
    END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION delete_status (
   in_name VARCHAR
) RETURNS void AS $$
    BEGIN
        DELETE FROM HO_STATUS
        WHERE status_id = in_name;
    END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION delete_session_if_files (
   in_session UUID
) RETURNS void AS $$
    DECLARE
        var_session_id INT;
    BEGIN
        var_session_id := (SELECT s.session_id from HO_SESSION s where s.session_name = in_session);
        DELETE FROM HO_IF_FILE
        WHERE session_id = var_session_id;
    END;
$$ LANGUAGE plpgsql;


CREATE FUNCTION update_processing_status (
   in_session UUID,
   in_status VARCHAR
) RETURNS void AS $$
    DECLARE
        var_session_id INT;
        var_processing_id INT;
        var_status_id INT;
        var_un_status_id INT;
    BEGIN
        var_session_id := (SELECT s.session_id from HO_SESSION s where s.session_name = in_session);
        var_status_id := (SELECT s.status_id from HO_STATUS s where s.name = in_status);
        var_un_status_id := (SELECT s.status_id
                    FROM HO_STATUS s
                    WHERE s.name = 'UNDEFINED');
        SELECT
            c.processing_id
        INTO var_processing_id
        FROM HO_PROCESSING c
        WHERE c.session_id = var_session_id
            AND c.status_id != var_un_status_id;

        UPDATE HO_PROCESSING
        SET status_id = var_status_id
        WHERE processing_id = var_processing_id;
    END;
$$ LANGUAGE plpgsql;