package ru.tasm.image.fragmentation.dao;


import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import ru.tasm.image.fragmentation.dao.api.ProcessingDao;
import ru.tasm.image.fragmentation.model.dao.DBCommands;
import ru.tasm.image.fragmentation.model.dao.ProcessingEntity;
import ru.tasm.image.fragmentation.model.dao.StatusEntity;
import ru.tasm.image.fragmentation.model.exception.DataBaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ProcessingDaoImpl implements ProcessingDao {
    AgroalDataSource dataSource;

    public ProcessingDaoImpl(AgroalDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<ProcessingEntity> getAllProcessing() throws DataBaseException {
        List<ProcessingEntity> result = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     DBCommands.GET_ALL_PROCESSING_PS.getCommand())) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                ProcessingEntity from = ProcessingEntity.from(resultSet);
                result.add(from);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return result;
    }

    @Override
    public List<ProcessingEntity> getProcessingBySession(UUID session) throws DataBaseException {
        List<ProcessingEntity> result = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     DBCommands.GET_PROCESSING_PS.getCommand())) {
            ps.setObject(1, session);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                ProcessingEntity from = ProcessingEntity.from(resultSet);
                result.add(from);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return result;
    }

    @Override
    @Transactional
    public void addProcessing(ProcessingEntity entity) throws DataBaseException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     DBCommands.ADD_PROCESSING_PS.getCommand())) {
            ps.setObject(1, entity.session());
            ps.setObject(2, entity.status().status());
            ps.setObject(3, entity.numbers());
            ps.setObject(4, entity.height());
            ps.setObject(5, entity.trySeg());
            ps.setObject(6, entity.formTryOrigSi());
            ps.setObject(7, entity.backgroundColor());
            ps.executeQuery();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    @Override
    @Transactional
    public void deleteAllSessionProcessing(UUID session) throws DataBaseException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     DBCommands.DELETE_PROCESSING_PS.getCommand())) {
            ps.setObject(1, session);
            ps.executeQuery();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    @Override
    @Transactional
    public void updateProcessing(UUID id, StatusEntity status) throws DataBaseException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     DBCommands.UPDATE_PROCESSING_STATUS_PS.getCommand())) {
            ps.setObject(1, id);
            ps.setObject(2, status.status());
            ps.executeQuery();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }
}
