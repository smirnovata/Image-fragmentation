package ru.tasm.image.fragmentation.dao.pg.pool.impl;

import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import ru.tasm.image.fragmentation.dao.api.StatusDao;
import ru.tasm.image.fragmentation.model.dao.DBCommands;
import ru.tasm.image.fragmentation.model.dao.StatusEntity;
import ru.tasm.image.fragmentation.model.exception.DataBaseException;

import java.util.List;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
@Alternative
public class StatusDaoImpl implements StatusDao {
    PgPool client;

    public StatusDaoImpl(PgPool client) {
        this.client = client;
    }

    @Override
    public List<StatusEntity> getStatuses() {
        return client.preparedQuery(DBCommands.GET_STATUSES.getCommand())
                .execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(StatusEntity::from)
                .subscribe().asStream().toList();
    }

    @Override
    public void addStatus(StatusEntity status) throws DataBaseException {
        try {
            client.preparedQuery(DBCommands.ADD_STATUS.getCommand())
                    .execute(Tuple.of(status.status()))
                    .subscribeAsCompletionStage().get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new DataBaseException(e);
        }
    }

    @Override
    public void deleteStatus(String name) {
        client.preparedQuery(DBCommands.DELETE_STATUS.getCommand())
                .executeAndForget(Tuple.of(name));
    }
}
