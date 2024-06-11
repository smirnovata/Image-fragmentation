package ru.tasm.image.fragmentation.model.dao;

import lombok.Getter;

@Getter
public enum DBCommands {
    ADD_PROCESSING("SELECT add_processing($1, $2, $3, $4, $5, $6, $7)"),
    ADD_SESSION("SELECT add_session($1, $2)"),
    ADD_STATUS("SELECT add_status($1)"),
    ADD_IF_FILE("SELECT add_if_file($1, $2)"),

    DELETE_PROCESSING("SELECT delete_processing($1)"),
    DELETE_SESSION("SELECT delete_session($1)"),
    DELETE_STATUS("SELECT delete_status($1)"),
    DELETE_IF_FILE("SELECT delete_session_if_files($1)"),

    GET_ALL_PROCESSING("SELECT * FROM get_all_processing()"),
    GET_SESSIONS("SELECT * FROM get_sessions()"),
    GET_STATUSES("SELECT * FROM get_statuses()"),
    GET_IF_FILES("SELECT * FROM get_if_files()"),

    GET_PROCESSING("SELECT * FROM get_processing($1)"),
    GET_SESSION("SELECT * FROM get_session($1)"),
    GET_SESSION_IF_FILES("SELECT * FROM get_session_if_files($1)"),

    UPDATE_PROCESSING_STATUS("SELECT * FROM update_processing_status($1, $2)"),
    
    ADD_PROCESSING_PS("SELECT add_processing(?, ?, ?, ?, ?, ?, ?)"),
    ADD_SESSION_PS("SELECT add_session(?, ?)"),
    ADD_STATUS_PS("SELECT add_status(?)"),
    ADD_IF_FILE_PS("SELECT add_if_file(?, ?)"),

    DELETE_PROCESSING_PS("SELECT delete_processing(?)"),
    DELETE_SESSION_PS("SELECT delete_session(?)"),
    DELETE_STATUS_PS("SELECT delete_status(?)"),
    DELETE_IF_FILE_PS("SELECT delete_session_if_files(?)"),

    GET_ALL_PROCESSING_PS("SELECT * FROM get_all_processing()"),
    GET_SESSIONS_PS("SELECT * FROM get_sessions()"),
    GET_STATUSES_PS("SELECT * FROM get_statuses()"),
    GET_IF_FILES_PS("SELECT * FROM get_if_files()"),

    GET_PROCESSING_PS("SELECT * FROM get_processing(?)"),
    GET_SESSION_PS("SELECT * FROM get_session(?)"),
    GET_SESSION_IF_FILES_PS("SELECT * FROM get_session_if_files(?)"),

    UPDATE_PROCESSING_STATUS_PS("SELECT * FROM update_processing_status(?, ?)");

    DBCommands(String command) {
        this.command = command;
    }

    private final String command;
}
