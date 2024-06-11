package ru.tasm.image.fragmentation.dao.pg.pool.impl;

import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import ru.tasm.image.fragmentation.dao.api.SessionDao;
import ru.tasm.image.fragmentation.model.dao.DBCommands;
import ru.tasm.image.fragmentation.model.dao.SessionEntity;
import ru.tasm.image.fragmentation.model.exception.DataBaseException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
@Alternative
public class SessionDaoImpl implements SessionDao {
    PgPool client;

    public SessionDaoImpl(PgPool client) {
        this.client = client;
    }

    @Override
    public List<SessionEntity> getSessions() {
        return client.preparedQuery(DBCommands.GET_SESSIONS.getCommand())
                .execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(SessionEntity::from)
                .subscribe().asStream().toList();
    }

    @Override
    public SessionEntity getSessionEntityBySession(UUID session) throws DataBaseException {
        try {
            return client.preparedQuery(DBCommands.GET_SESSION.getCommand())
                    .execute(Tuple.of(session))
                    .onItem().transform(RowSet::iterator)
                    .onItem().transform(iterator -> iterator.hasNext() ?
                            SessionEntity.from(iterator.next()) : null)
                    .subscribe().asCompletionStage().get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new DataBaseException(e);
        }
    }

    @Override
    public void addSessionEntity(SessionEntity entity) throws DataBaseException {
        try {
            client.preparedQuery(DBCommands.ADD_SESSION.getCommand())
                    .execute(Tuple.of(entity.session(), entity.creatingDate()))
                    .subscribeAsCompletionStage().get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new DataBaseException(e);
        }
    }

    @Override
    public void deleteSessionEntity(UUID session) {
        client.preparedQuery(DBCommands.DELETE_SESSION.getCommand())
                .executeAndForget(Tuple.of(session));
    }
}
