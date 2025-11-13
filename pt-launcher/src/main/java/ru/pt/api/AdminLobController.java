package ru.pt.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.pt.api.dto.product.LobModel;
import ru.pt.api.dto.product.LobVar;
import ru.pt.api.service.product.LobService;
import ru.pt.hz.JsonExampleBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/lobs")
public class AdminLobController {

    private final LobService lobService;

    public AdminLobController(LobService lobService) {
        this.lobService = lobService;
    }

    // get /admin/lobs return id, Code, Name from repository
    @GetMapping
    public List<Map<String, Object>> listLobs() {
        return lobService.listActiveSummaries().stream()
                .map(row -> Map.of(
                        "id", row[0],
                        "mpCode", row[1],
                        "mpName", row[2]
                ))
                .collect(Collectors.toList());
    }

    // get /admin/lobs/{lob_code} returns json
    @GetMapping("/{code}")
    public ResponseEntity<LobModel> getByCode(@PathVariable("code") String code) {
        return ResponseEntity.ok(lobService.getByCode(code));
    }

    // post /admin/lobs insert new record
    @PostMapping
    public ResponseEntity<LobModel> createLob(@RequestBody LobModel payload) {
        LobModel created = lobService.create(payload);
        return ResponseEntity.ok(created);
    }

// put /admin/lobs/{lob_code} replace json, fix name and mpCode/id rules
    @PutMapping("/{code}")
    public ResponseEntity<LobModel> updateLob(@PathVariable("code") String code, @RequestBody LobModel payload) {
        return ResponseEntity.ok(lobService.updateByCode(code, payload));
    }

    // delete /admin/lobs/{lob_code} soft delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLob(@PathVariable("id") Integer id) {
        boolean deleted = lobService.softDeleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // get /admin/lobs/example returns json example
    @GetMapping("/{code}/example")
    public ResponseEntity<String> getJsonExample(@PathVariable("code") String code) {
        LobModel lob = lobService.getByCode(code);
        if (lob == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            String jsonExample = JsonExampleBuilder.buildJsonExample(
                    lob.getMpVars().stream()
                    .map(LobVar::getVarPath)
                    .collect(Collectors.toList())
            );
            return ResponseEntity.ok(jsonExample);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


}


