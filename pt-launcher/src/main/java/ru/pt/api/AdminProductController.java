package ru.pt.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pt.domain.productVersion.ProductVersionModel;
import ru.pt.service.ProductService;
import ru.pt.service.LobService;
import ru.pt.domain.lob.LobModel;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;
    private final LobService lobService;

    public AdminProductController(ProductService productService, LobService lobService) {
        this.productService = productService;
        this.lobService = lobService;
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        return productService.listSummaries();
    }

    @PostMapping
    public ResponseEntity<ProductVersionModel> create(@RequestBody ProductVersionModel payload) {
        ProductVersionModel created = productService.create(payload);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}/versions/{versionNo}")
    public ResponseEntity<ProductVersionModel> getVersion(@PathVariable("id") Integer id, @PathVariable("versionNo") Integer versionNo) {
        return ResponseEntity.ok(productService.getVersion(id, versionNo));
    }

    @PostMapping("/{id}/versions/{versionNo}/cmd/create")
    public ResponseEntity<ProductVersionModel> createVersion(@PathVariable("id") Integer id, @PathVariable("versionNo") Integer versionNo) {
        return ResponseEntity.ok(productService.createVersionFrom(id, versionNo).getProduct());
    }

    @PutMapping("/{id}/versions/{versionNo}")
    public ResponseEntity<ProductVersionModel> updateVersion(@PathVariable("id") Integer id,
                                                  @PathVariable("versionNo") Integer versionNo,
                                                  @RequestBody ProductVersionModel payload) {
        return ResponseEntity.ok(productService.updateVersion(id, versionNo, payload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Integer id) {
        productService.softDeleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/versions/{versionNo}")
    public ResponseEntity<Void> deleteVersion(@PathVariable("id") Integer id, @PathVariable("versionNo") Integer versionNo) {
        productService.deleteVersion(id, versionNo);
        return ResponseEntity.noContent().build();
    }

    // get /admin/lobs/example returns json example
    @GetMapping("/{id}/versions/{versionNo}/example_quote")
    public ResponseEntity<String> getJsonExampleQuote(@PathVariable("id") Integer id, @PathVariable("versionNo") Integer versionNo) {
        return ResponseEntity.ok(productService.getJsonExampleQuote(id, versionNo));
    }

    // get /admin/lobs/example returns json example
    @GetMapping("/{id}/versions/{versionNo}/example_save")
    public ResponseEntity<String> getJsonExampleSave(@PathVariable("id") Integer id, @PathVariable("versionNo") Integer versionNo) {
        return ResponseEntity.ok(productService.getJsonExampleSave(id, versionNo));
    }
}


