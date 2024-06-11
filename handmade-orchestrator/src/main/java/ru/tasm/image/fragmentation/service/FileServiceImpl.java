package ru.tasm.image.fragmentation.service;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.server.StreamResource;
import jakarta.enterprise.context.Dependent;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import ru.tasm.image.fragmentation.config.Config;
import ru.tasm.image.fragmentation.dao.api.IFFileDao;
import ru.tasm.image.fragmentation.dao.api.SessionDao;
import ru.tasm.image.fragmentation.model.IFFile;
import ru.tasm.image.fragmentation.model.dao.IFFileEntity;
import ru.tasm.image.fragmentation.model.dao.SessionEntity;
import ru.tasm.image.fragmentation.model.exception.DataBaseException;
import ru.tasm.image.fragmentation.model.exception.FileRuntimeException;
import ru.tasm.image.fragmentation.service.api.FileService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.nio.file.FileVisitResult.CONTINUE;

@Dependent
@Slf4j
public class FileServiceImpl implements FileService {
    IFFileDao ifFileDao;
    SessionDao sessionDao;

    transient Config config;

    public FileServiceImpl(Config config, IFFileDao ifFileDao, SessionDao sessionDao) {
        this.config = config;
        this.ifFileDao = ifFileDao;
        this.sessionDao = sessionDao;
    }

    @Transactional
    @Override
    public void saveOrigFile(IFFile file) throws DataBaseException {
        log.debug("[{}] trying save", file.sessionId());
        try {
            String extension = FilenameUtils.getExtension(file.fileName());
            if (extension.isEmpty()) {
                extension = "png";
            }
            String resultFolder = getResultFolderPath(file.sessionId());
            String fileName = "original" + "." + extension;
            File resultFile =
                    new File(resultFolder + File.separator + fileName);
            createDirectory(new File(resultFolder));
            createDirectory(new File(resultFolder + File.separator + "result"));
            Files.copy(file.file().toPath(), resultFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            sessionDao.addSessionEntity(new SessionEntity(file.sessionId(),
                    LocalDateTime.now().toString()));
            ifFileDao.addIFFile(new IFFileEntity(file.sessionId(),
                    resultFile.getAbsolutePath()));
        } catch (IOException e) {
            throw new FileRuntimeException(e);
        }
    }

    @Override
    public IFFile getOrigFile(UUID id) throws DataBaseException {
        IFFileEntity original = ifFileDao.getFilesBySession(id).stream()
                .filter(f -> f.filePath().contains("original"))
                .findFirst()
                .orElse(null);
        if (original != null) {
            return new IFFile(id, new File(original.filePath()).getName(),
                    new File(original.filePath()));
        }
        return null;
    }

    @Override
    public File getResultFolder(UUID id) {
        return new File(getResultFolderPath(id));
    }

    @Override
    public void clearSessionFolder(UUID id) throws DataBaseException {
        ifFileDao.deleteIFFiles(id);
        removeDirectory(config.getFolder() + File.separator + id.toString());
    }

    @Override
    public IFFile getResultZipFile(UUID id) {
        String name = config.getFolder() + File.separator + id.toString()
                + File.separator + "result.zip";
        Path resultFolderPath = Path.of(getResultFolderPath(id));
        try (ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(name))) {
            Files.walkFileTree(resultFolderPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                    try {
                        Path targetFile = resultFolderPath.relativize(file);
                        outputStream.putNextEntry(new ZipEntry(targetFile.toString()));
                        byte[] bytes = Files.readAllBytes(file);
                        outputStream.write(bytes, 0, bytes.length);
                        outputStream.closeEntry();
                    } catch (IOException e) {
                        throw new FileRuntimeException(e);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new FileRuntimeException(e);
        }
        return new IFFile(id, "result.zip", new File(name));
    }

    @Override
    public void clearResultDirectoryByUUID(UUID id) throws DataBaseException {
        List<IFFileEntity> filesBySession = ifFileDao.getFilesBySession(id);
        if (filesBySession != null && filesBySession.isEmpty()) {
            String path = config.getFolder() + File.separator
                    + id.toString() + File.separator + "archive" + File.separator +
                    "result";
            if (new File(path).exists()) {
                removeDirectory(path);
                createDirectory(new File(path));
            }
        }
    }

    @Override
    public StreamResource getStreamResourceFromFile(IFFile file) {
        return getStreamResourceFromFile(file.file());
    }

    @Override
    public StreamResource getStreamResourceFromFile(File file) {
        return new StreamResource(
                file.getName(),
                () -> {
                    try {
                        return new ByteArrayInputStream(
                                FileUtils.readFileToByteArray(file));
                    } catch (IOException e) {
                        throw new FileRuntimeException(e);
                    }
                });
    }

    @Override
    public File getOneResultZipFile(UUID id, String name) {
        String resultZipName = config.getFolder() + File.separator + id.toString()
                + File.separator + name + "_result.zip";
        Path resultFolderPath = Path.of(getResultFolderPath(id)
                + File.separator + "result");
        try (ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(resultZipName))) {
            Files.walkFileTree(resultFolderPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                    try {
                        if (file.getFileName().toString().split("_")[0].equals(name.split("№")[1])) {
                            Path targetFile = resultFolderPath.relativize(file);
                            outputStream.putNextEntry(new ZipEntry(targetFile.toString()));
                            byte[] bytes = Files.readAllBytes(file);
                            outputStream.write(bytes, 0, bytes.length);
                            outputStream.closeEntry();
                        }
                    } catch (IOException e) {
                        throw new FileRuntimeException(e);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new FileRuntimeException(e);
        }
        return new File(resultZipName);
    }

    private static void createDirectory(File path) {
        if (path.mkdirs()) {
            log.info("directory was created: {}", path);
        }
    }

    private String getResultFolderPath(UUID id) {
        return config.getFolder() + File.separator + id.toString()
                + File.separator + "archive";
    }

    private void removeDirectory(String path) {
        try {
            if (new File(path).exists()) {
                Files.walkFileTree(Path.of(path), new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file,
                                                     BasicFileAttributes attrs) throws IOException {
                        log.debug("The file was deleted: {}", file);
                        Files.delete(file);
                        return CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir,
                                                              IOException exc) throws IOException {
                        log.info("the directory was deleted: {}", dir);
                        if (exc == null) {
                            Files.delete(dir);
                            return CONTINUE;
                        } else {
                            throw exc;
                        }
                    }
                });
            }
        } catch (IOException e) {
            log.error("Failed to delete files", e);
        }
    }
}
