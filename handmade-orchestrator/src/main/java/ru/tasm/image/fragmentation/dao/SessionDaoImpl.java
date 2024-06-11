package ru.tasm.image.fragmentation.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import ru.tasm.image.fragmentation.dao.api.SessionDao;
import ru.tasm.image.fragmentation.model.dao.DBCommands;
import ru.tasm.image.fragmentation.model.dao.SessionEntity;
import ru.tasm.image.fragmentation.model.exception.DataBaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class SessionDaoImpl implements SessionDao {
    AgroalDataSource dataSource;

    public SessionDaoImpl(AgroalDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<SessionEntity> getSessions() throws DataBaseException {
        List<SessionEntity> result = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     DBCommands.GET_SESSIONS_PS.getCommand())) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                SessionEntity from = SessionEntity.from(resultSet);
                result.add(from);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return result;
    }

    @Override
    public SessionEntity getSessionEntityBySession(UUID session) throws DataBaseException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     DBCommands.GET_SESSION_PS.getCommand())) {
            ps.setObject(1, session);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                return SessionEntity.from(resultSet);
            }
            return null;
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    @Override
    @Transactional
    public void addSessionEntity(SessionEntity entity) throws DataBaseException {
        log.debug("[{}] add new session", entity.session());
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     DBCommands.ADD_SESSION_PS.getCommand())) {
            ps.setObject(1, entity.session());
            ps.setObject(2, entity.creatingDate());
            ps.executeQuery();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    @Override
    @Transactional
    public void deleteSessionEntity(UUID session) throws DataBaseException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     DBCommands.DELETE_SESSION_PS.getCommand())) {
            ps.setObject(1, session);
            ps.executeQuery();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }
}
