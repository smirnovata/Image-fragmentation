package ru.tasm.image.fragmentation.model.dao;

import io.vertx.mutiny.sqlclient.Row;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public record ProcessingEntity(UUID session,
                               StatusEntity status,
                               Integer numbers,
                               Integer height,
                               Boolean trySeg,
                               String formTryOrigSi,
                               Integer backgroundColor) {
    public static ProcessingEntity from(Row row) {
        return new ProcessingEntity(row.getUUID("session_uuid"),
                new StatusEntity(row.getString("status")),
                row.getInteger("numbers"),
                row.getInteger("height"),
                row.getBoolean("try_seg"),
                row.getString("form_try_orig_si"),
                row.getInteger("form_background"));
    }

    public static ProcessingEntity from(ResultSet row) throws SQLException {
        return new ProcessingEntity(UUID.fromString(row.getString("session_uuid")),
                new StatusEntity(row.getString("status")),
                row.getInt("numbers"),
                row.getInt("height"),
                row.getBoolean("try_seg"),
                row.getString("form_try_orig_si"),
                row.getInt("form_background"));
    }
}
