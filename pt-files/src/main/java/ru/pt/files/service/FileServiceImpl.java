package ru.pt.files.service;

import jakarta.transaction.Transactional;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.stereotype.Service;
import ru.pt.api.dto.file.FileModel;
import ru.pt.api.service.file.FileService;
import ru.pt.files.entity.FileEntity;
import ru.pt.files.repository.FileRepository;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final DataSource dataSource;

    public FileServiceImpl(FileRepository fileRepository, DataSource dataSource) {
        this.fileRepository = fileRepository;
        this.dataSource = dataSource;
    }

    @Transactional
    @Override
    public FileModel createMeta(String fileType, String fileDesc, String productCode, Integer packageCode) {
        FileEntity e = new FileEntity();
        e.setFileType(fileType);
        e.setFileDesc(fileDesc);
        e.setProductCode(productCode);
        e.setPackageCode(packageCode);
        e.setDeleted(false);
        var saved = fileRepository.save(e);
        var model = new FileModel();
        model.setId(saved.getId());
        model.setFileDescription(saved.getFileDesc());
        model.setDeleted(saved.isDeleted());
        model.setFileType(model.getFileType());
        model.setProductCode(saved.getProductCode());
        model.setPackageCode(saved.getPackageCode());
        return model;
    }

    @Transactional
    @Override
    public void uploadBody(Long id, byte[] file) {
        FileEntity entity = fileRepository.findActiveById(id)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        entity.setFileBody(file);
        fileRepository.save(entity);
    }

    @Override
    public List<Map<String, Object>> list(String productCode) {
        List<Object[]> rows = fileRepository.listSummaries(productCode);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] r : rows) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", r[0]);
            m.put("fileType", r[1]);
            m.put("fileDescription", r[2]);
            m.put("productCode", r[3]);
            m.put("packageCode", r[4]);
            result.add(m);
        }
        return result;
    }

    @Override
    public byte[] download(Long id) {
        FileEntity entity = fileRepository.findActiveById(id)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        if (entity.getFileBody() == null) {
            throw new IllegalArgumentException("File body is empty");
        }
        return entity.getFileBody();
    }

    @Transactional
    @Override
    public void softDelete(Long id) {
        FileEntity entity = fileRepository.findActiveById(id)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        entity.setDeleted(true);
        fileRepository.save(entity);
    }

    @Override
    public byte[] process(Long id, Map<String, String> keyValues) {
        FileEntity entity = fileRepository.findActiveById(id)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        if (entity.getFileBody() == null) {
            throw new IllegalArgumentException("File body is empty");
        }

        try (PDDocument doc = Loader.loadPDF( entity.getFileBody())) {
            PDAcroForm form = doc.getDocumentCatalog().getAcroForm();
            if (form != null) {
                for (Map.Entry<String, String> e : keyValues.entrySet()) {
                    PDField field = form.getField(e.getKey());
                    if (field != null) {
                        System.out.println(e.getKey() + " " + e.getValue());
                        try {
                            field.setValue(e.getValue());
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                }
                form.flatten();
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to process PDF", ex);
        }
    }

    @Override
    public byte[] getFile(String fileType, Map<String, String> keyValues) {

        String productCode = keyValues.get("product");
        String packageCode = keyValues.get("packageCode");

        FileEntity entity = fileRepository.findActiveByFileTypeAndProductCodeAndPackageCode(fileType, productCode, Integer.parseInt(packageCode))
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        return process(entity.getId(), keyValues);
    }

}