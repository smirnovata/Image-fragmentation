package ru.tasm.image.fragmentation.model.dao;

import io.vertx.mutiny.sqlclient.Row;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public record SessionEntity(UUID session, String creatingDate) {

    public static SessionEntity from(Row row) {
        return new SessionEntity(row.getUUID("session_uuid"),
                row.getString("creating_date"));
    }

    public static SessionEntity from(ResultSet resultSet) throws SQLException {
        return new SessionEntity(UUID.fromString(resultSet.getString("session_uuid")),
                resultSet.getString("creating_date"));
    }
}
