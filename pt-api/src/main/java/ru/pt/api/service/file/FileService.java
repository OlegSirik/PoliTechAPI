package ru.pt.api.service.file;

import ru.pt.api.dto.file.FileModel;

import java.util.List;
import java.util.Map;

public interface FileService {

    FileModel createMeta(String fileType, String fileDesc, String productCode, Integer packageCode);

    void uploadBody(Long id, byte[] file);

    List<Map<String, Object>> list(String productCode);

    byte[] download(Long id);

    void softDelete(Long id);

    byte[] process(Long id, Map<String, String> keyValues);

    byte[] getFile(String fileType, Map<String, String> keyValues);

}
