package ru.tasm.image.fragmentation.dao.pg.pool.impl;


import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import ru.tasm.image.fragmentation.dao.api.ProcessingDao;
import ru.tasm.image.fragmentation.model.dao.DBCommands;
import ru.tasm.image.fragmentation.model.dao.ProcessingEntity;
import ru.tasm.image.fragmentation.model.dao.StatusEntity;
import ru.tasm.image.fragmentation.model.exception.DataBaseException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
@Alternative
public class ProcessingDaoImpl implements ProcessingDao {
    PgPool client;

    public ProcessingDaoImpl(PgPool client) {
        this.client = client;
    }

    @Override
    public List<ProcessingEntity> getAllProcessing() {
        return client.preparedQuery(DBCommands.GET_ALL_PROCESSING.getCommand())
                .execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(ProcessingEntity::from)
                .subscribe().asStream().toList();
    }

    @Override
    public List<ProcessingEntity> getProcessingBySession(UUID session) throws DataBaseException {
        return client.preparedQuery(DBCommands.GET_PROCESSING.getCommand())
                .execute(Tuple.of(session))
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(ProcessingEntity::from)
                .subscribe().asStream().toList();
    }

    @Override
    public void addProcessing(ProcessingEntity entity) throws DataBaseException {
        try {
            client.preparedQuery(DBCommands.ADD_PROCESSING.getCommand())
                    .execute(Tuple.of(entity.session(), entity.status().status(),
                            entity.numbers(),
                            entity.height(), entity.trySeg(), entity.backgroundColor()))
                    .subscribeAsCompletionStage().get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new DataBaseException(e);
        }
    }

    @Override
    public void deleteAllSessionProcessing(UUID session) {
        client.preparedQuery(DBCommands.DELETE_PROCESSING.getCommand())
                .executeAndForget(Tuple.of(session));
    }

    @Override
    public void updateProcessing(UUID id, StatusEntity status) {
        client.preparedQuery(DBCommands.UPDATE_PROCESSING_STATUS.getCommand())
                .executeAndForget(Tuple.of(id, status.status()));
    }
}
