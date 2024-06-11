package ru.tasm.image.fragmentation.model;

import ru.tasm.image.fragmentation.model.dao.StatusEntity;

public enum Status {
    UNDEFINED,
    IN_PROGRESS,
    FINISHED,
    FAILED;

    public static Status from(StatusEntity status) {
        return Status.valueOf(status.status());
    }
}
