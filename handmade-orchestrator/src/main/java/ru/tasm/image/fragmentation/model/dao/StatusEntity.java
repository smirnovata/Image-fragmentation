package ru.tasm.image.fragmentation.model.dao;

import io.vertx.mutiny.sqlclient.Row;
import ru.tasm.image.fragmentation.model.Status;

import java.sql.ResultSet;
import java.sql.SQLException;

public record StatusEntity(String status) {
    public StatusEntity(Status status) {
        this(status.name());
    }

    public static StatusEntity from(Row row) {
        return new StatusEntity(row.getString("status"));
    }

    public static StatusEntity from(ResultSet row) throws SQLException {
        return new StatusEntity(row.getString("status"));
    }
}
