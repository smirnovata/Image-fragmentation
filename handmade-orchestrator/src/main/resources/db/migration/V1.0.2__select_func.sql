DROP FUNCTION IF EXISTS get_statuses();
DROP FUNCTION IF EXISTS get_all_processing();
DROP FUNCTION IF EXISTS get_sessions();
DROP FUNCTION IF EXISTS get_if_files();

DROP FUNCTION IF EXISTS get_processing(UUID);
DROP FUNCTION IF EXISTS get_session(UUID);
DROP FUNCTION IF EXISTS get_session_if_files(UUID);

CREATE FUNCTION get_statuses (
) RETURNS TABLE(status VARCHAR) AS $$
    BEGIN
        RETURN QUERY SELECT
                s."name" as status
            FROM HO_STATUS s;
    END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION get_all_processing (
) RETURNS TABLE(
    session_uuid UUID,
    status VARCHAR,
    numbers INT,
    height INT,
    try_seg BOOLEAN,
    form_try_orig_si VARCHAR,
    form_background INT) AS $$
    BEGIN
        RETURN QUERY SELECT
            s.session_name as session_uuid,
            st.name,
            c.form_numbers,
            c.form_height,
            c.form_try_seg,
            c.form_try_orig_si,
            c.form_background
        FROM HO_PROCESSING c
        JOIN HO_STATUS st ON c.status_id = st.status_id
        JOIN HO_SESSION s ON c.session_id = s.session_id;
    END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION get_sessions (
) RETURNS TABLE(
  session_uuid uuid,
  creating_date VARCHAR) AS $$
    BEGIN
        RETURN QUERY SELECT
            s.session_name as session_uuid,
            s.creating_date
        FROM HO_SESSION s;
    END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION get_if_files (
) RETURNS TABLE(
  session_uuid uuid,
  file_path VARCHAR) AS $$
    BEGIN
        RETURN QUERY SELECT
            s.session_name as session_uuid,
            c.file_name as file_path
        FROM HO_IF_FILE c
        JOIN HO_SESSION s ON c.session_id = s.session_id;
    END;
$$ LANGUAGE plpgsql;


CREATE FUNCTION get_processing (
   in_session UUID
) RETURNS TABLE(
    session_uuid UUID,
    status VARCHAR,
    numbers INT,
    height INT,
    try_seg BOOLEAN,
    form_try_orig_si VARCHAR,
    form_background INT) AS $$
    BEGIN
        RETURN QUERY SELECT
            s.session_name as session_uuid,
            st.name as status,
            c.form_numbers as numbers,
            c.form_height as height,
            c.form_try_seg as try_seg,
            c.form_try_orig_si as form_try_orig_si,
            c.form_background
        FROM HO_PROCESSING c
        JOIN HO_STATUS st ON c.status_id = st.status_id
        JOIN HO_SESSION s ON c.session_id = s.session_id
        WHERE session_name = in_session;
    END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION get_session (
   in_session UUID
) RETURNS TABLE(
  session_uuid uuid,
  creating_date VARCHAR) AS $$
    BEGIN
        RETURN QUERY SELECT
            s.session_name as session_uuid,
            s.creating_date
        FROM HO_SESSION s
        WHERE session_name = in_session;
    END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION get_session_if_files (
   in_session UUID
) RETURNS TABLE(
  session_uuid uuid,
  file_path VARCHAR) AS $$
    BEGIN
        RETURN QUERY SELECT
            s.session_name as session_uuid,
            c.file_name as file_path
        FROM HO_IF_FILE c
        JOIN HO_SESSION s ON c.session_id = s.session_id
        WHERE s.session_name = in_session;
    END;
$$ LANGUAGE plpgsql;

