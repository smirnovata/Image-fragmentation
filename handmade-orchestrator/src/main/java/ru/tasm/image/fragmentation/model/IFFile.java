package ru.tasm.image.fragmentation.model;

import java.io.File;
import java.util.UUID;

public record IFFile(
        UUID sessionId,
        String fileName,
        File file) {
}
