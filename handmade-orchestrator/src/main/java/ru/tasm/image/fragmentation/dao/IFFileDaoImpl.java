package ru.tasm.image.fragmentation.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import ru.tasm.image.fragmentation.dao.api.IFFileDao;
import ru.tasm.image.fragmentation.model.dao.DBCommands;
import ru.tasm.image.fragmentation.model.dao.IFFileEntity;
import ru.tasm.image.fragmentation.model.exception.DataBaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class IFFileDaoImpl implements IFFileDao {

    AgroalDataSource dataSource;


    public IFFileDaoImpl(AgroalDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<IFFileEntity> getFiles() throws DataBaseException {
        List<IFFileEntity> result = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     DBCommands.GET_IF_FILES_PS.getCommand())) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                IFFileEntity from = IFFileEntity.from(resultSet);
                result.add(from);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return result;
    }

    @Override
    public List<IFFileEntity> getFilesBySession(UUID session) throws DataBaseException {
        List<IFFileEntity> result = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     DBCommands.GET_SESSION_IF_FILES_PS.getCommand())) {
            ps.setObject(1, session);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                IFFileEntity from = IFFileEntity.from(resultSet);
                result.add(from);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
        return result;
    }

    @Override
    @Transactional
    public void addIFFile(IFFileEntity entity) throws DataBaseException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     DBCommands.ADD_IF_FILE_PS.getCommand())) {
            ps.setObject(1, entity.session());
            ps.setObject(2, entity.filePath());
            ps.executeQuery();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }

    @Override
    @Transactional
    public void deleteIFFiles(UUID session) throws DataBaseException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     DBCommands.DELETE_IF_FILE_PS.getCommand())) {
            ps.setObject(1, session);
            ps.executeQuery();
        } catch (SQLException e) {
            throw new DataBaseException(e);
        }
    }
}
