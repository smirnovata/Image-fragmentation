package ru.tasm.image.fragmentation.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import ru.tasm.image.fragmentation.dao.api.StatusDao;
import ru.tasm.image.fragmentation.model.dao.DBCommands;
import ru.tasm.image.fragmentation.model.dao.StatusEntity;
import ru.tasm.image.fragmentation.model.exception.DataBaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class StatusDaoImpl implements StatusDao {
    AgroalDataSource dataSource;

    public StatusDaoImpl(AgroalDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<StatusEntity> getStatuses() throws DataBaseException {
        List<StatusEntity> result = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     DBCommands.GET_STATUSES_PS.getCommand())) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                StatusEntity from = StatusEntity.from(resultSet);
                result.add(from);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return result;
    }

    @Override
    @Transactional
    public void addStatus(StatusEntity status) throws DataBaseException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     DBCommands.ADD_STATUS_PS.getCommand())) {
            ps.setObject(1, status.status());
            ps.executeQuery();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    @Override
    @Transactional
    public void deleteStatus(String name) throws DataBaseException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     DBCommands.DELETE_STATUS_PS.getCommand())) {
            ps.setObject(1, name);
            ps.executeQuery();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }
}
