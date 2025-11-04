package ru.pt.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.pt.domain.FileEntity;
import ru.pt.repository.FileRepository;

import javax.sql.DataSource;
import jakarta.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final DataSource dataSource;

    public FileService(FileRepository fileRepository, DataSource dataSource) {
        this.fileRepository = fileRepository;
        this.dataSource = dataSource;
    }

    @Transactional
    public FileEntity createMeta(String fileType, String fileDesc, String productCode, Integer packageCode) {
        long id = nextId();
        FileEntity e = new FileEntity();
        e.setId(id);
        e.setFileType(fileType);
        e.setFileDesc(fileDesc);
        e.setProductCode(productCode);
        e.setPackageCode(packageCode);
        e.setDeleted(false);
        return fileRepository.save(e);
    }

    @Transactional
    public void uploadBody(Long id, MultipartFile file) {
        FileEntity entity = fileRepository.findActiveById(id)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        try {
            entity.setFileBody(file.getBytes());
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read uploaded file", e);
        }
        fileRepository.save(entity);
    }

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

    public byte[] download(Long id) {
        FileEntity entity = fileRepository.findActiveById(id)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        if (entity.getFileBody() == null) {
            throw new IllegalArgumentException("File body is empty");
        }
        return entity.getFileBody();
    }

    @Transactional
    public void softDelete(Long id) {
        FileEntity entity = fileRepository.findActiveById(id)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        entity.setDeleted(true);
        fileRepository.save(entity);
    }

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

    public byte[] getFile(String fileType, Map<String, String> keyValues) {

        String productCode = keyValues.get("product");
        String packageCode = keyValues.get("packageCode");

        FileEntity entity = fileRepository.findActiveByFileTypeAndProductCodeAndPackageCode(fileType, productCode, Integer.parseInt(packageCode))
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        return process(entity.getId(), keyValues);
    }

    private long nextId() {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("select nextval('pt_files_seq')");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
            throw new IllegalStateException("No value from sequence pt_files_seq");
        } catch (SQLException e) {
            throw new IllegalStateException("Sequence pt_files_seq is missing or inaccessible", e);
        }
    }
}


