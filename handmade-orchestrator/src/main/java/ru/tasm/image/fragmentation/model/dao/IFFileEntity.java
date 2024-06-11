package ru.tasm.image.fragmentation.model.dao;

import io.vertx.mutiny.sqlclient.Row;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public record IFFileEntity(UUID session, String filePath) {
    public static IFFileEntity from(Row row) {
        return new IFFileEntity(UUID.fromString(row.getString("session_uuid")),
                row.getString("file_path"));
    }

    public static IFFileEntity from(ResultSet resultSet) throws SQLException {
        return new IFFileEntity(UUID.fromString(
                resultSet.getString("session_uuid")),
                resultSet.getString("file_path"));
    }
}
