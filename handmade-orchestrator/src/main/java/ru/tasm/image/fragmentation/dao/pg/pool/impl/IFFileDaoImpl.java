package ru.tasm.image.fragmentation.dao.pg.pool.impl;

import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import lombok.extern.slf4j.Slf4j;
import ru.tasm.image.fragmentation.dao.api.IFFileDao;
import ru.tasm.image.fragmentation.model.dao.DBCommands;
import ru.tasm.image.fragmentation.model.dao.IFFileEntity;
import ru.tasm.image.fragmentation.model.exception.DataBaseException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
@Slf4j
@Alternative
public class IFFileDaoImpl implements IFFileDao {
    PgPool client;

    public IFFileDaoImpl(PgPool client) {
        this.client = client;
    }

    @Override
    public List<IFFileEntity> getFiles() {
        return client.preparedQuery(DBCommands.GET_IF_FILES.getCommand())
                .execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(IFFileEntity::from)
                .subscribe().asStream().toList();
    }

    @Override
    public List<IFFileEntity> getFilesBySession(UUID session) {
        return client.preparedQuery(DBCommands.GET_SESSION_IF_FILES.getCommand())
                .execute(Tuple.of(session))
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(IFFileEntity::from)
                .subscribe().asStream().toList();
    }

    @Override
    public void addIFFile(IFFileEntity entity) throws DataBaseException {
        try {
            log.info("[{}] add new IFFile", entity.session());
            client.preparedQuery(DBCommands.ADD_IF_FILE.getCommand())
                    .execute(Tuple.of(entity.session(), entity.filePath()))
                    .subscribeAsCompletionStage().get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new DataBaseException(e);
        }
    }

    @Override
    public void deleteIFFiles(UUID session) {
        client.preparedQuery(DBCommands.DELETE_IF_FILE.getCommand())
                .executeAndForget(Tuple.of(session));
    }
}
