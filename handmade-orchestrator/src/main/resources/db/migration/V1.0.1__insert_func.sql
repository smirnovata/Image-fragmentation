DROP FUNCTION IF EXISTS add_processing(UUID, VARCHAR, INT, INT, BOOLEAN, VARCHAR, INT);
DROP FUNCTION IF EXISTS add_session(UUID, VARCHAR);
DROP FUNCTION IF EXISTS add_status(VARCHAR);
DROP FUNCTION IF EXISTS add_if_file(UUID, VARCHAR);


CREATE FUNCTION add_processing (
    in_session UUID,
    in_status VARCHAR,
    in_form_numbers INT,
    in_form_height INT,
    in_form_try_seg BOOLEAN,
    in_form_try_orig_si VARCHAR,
    in_form_background INT
) RETURNS void AS $$
    DECLARE
        var_session_id INT;
        var_status_id INT;
        var_un_status_id INT;
    BEGIN
        var_session_id := (SELECT s.session_id from HO_SESSION s where s.session_name = in_session);
        var_status_id := (SELECT s.status_id from HO_STATUS s where s.name = in_status);
        var_un_status_id := (SELECT s.status_id
                            FROM HO_STATUS s
                            WHERE s.name = 'UNDEFINED');

        UPDATE HO_PROCESSING
        SET status_id = var_un_status_id
        WHERE session_id = var_session_id;

        INSERT INTO HO_PROCESSING VALUES
        (nextval('processing_id_seq'), var_session_id, var_status_id,
            in_form_numbers, in_form_height, in_form_try_seg, in_form_try_orig_si,
            in_form_background);

    END;
$$ LANGUAGE plpgsql;


CREATE FUNCTION add_status (
    name VARCHAR(120)
) RETURNS void AS $$
    BEGIN
        INSERT INTO HO_STATUS VALUES
            (nextval('status_id_seq'), name);
    END;
$$ LANGUAGE plpgsql;


CREATE FUNCTION add_session (
    in_session UUID,
    in_creating_date VARCHAR
) RETURNS void AS $$
    BEGIN
        IF NOT EXISTS
                (
                SELECT 1
                FROM HO_SESSION h
                WHERE h.session_name = in_session
                ) THEN
                BEGIN
                    INSERT INTO HO_SESSION VALUES
                    (nextval('session_id_seq'), in_session, in_creating_date);
                END;
        END IF;
    END;
$$ LANGUAGE plpgsql;


CREATE FUNCTION add_if_file (
    in_session UUID,
    in_file_name VARCHAR
) RETURNS void AS $$
    DECLARE
        var_session_id INT;
    BEGIN
        var_session_id := (SELECT s.session_id from HO_SESSION s where s.session_name = in_session);

        INSERT INTO HO_IF_FILE VALUES
        (nextval('file_id_seq'), var_session_id, in_file_name);

    END;
$$ LANGUAGE plpgsql;