package com.fluffyletter.controller;

import com.fluffyletter.config.FluffyProperties;
import com.fluffyletter.service.AuthHeaderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/admin/uploads")
public class AdminUploadController {

    private static final long MAX_BYTES = 10L * 1024L * 1024L;
    private static final Set<String> ALLOWED_EXT = Set.of("png", "jpg", "jpeg", "webp", "gif");

    private final AuthHeaderService authHeaderService;
    private final FluffyProperties fluffyProperties;

    public AdminUploadController(AuthHeaderService authHeaderService, FluffyProperties fluffyProperties) {
        this.authHeaderService = authHeaderService;
        this.fluffyProperties = fluffyProperties;
    }

    @PostMapping(value = "/product-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadProductImage(@RequestHeader("Authorization") String authorization,
                                                  @RequestParam(required = false) Long productId,
                                                  @RequestPart("file") MultipartFile file,
                                                  HttpServletRequest request) throws Exception {
        authHeaderService.requireAdmin(authorization);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is required");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("file too large (max 10MB)");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new IllegalArgumentException("only image/* is allowed");
        }

        String ext = extFromFilename(file.getOriginalFilename());
        if (!StringUtils.hasText(ext)) {
            ext = extFromContentType(contentType);
        }
        if (!StringUtils.hasText(ext) || !ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("unsupported image type");
        }

        Path root = Path.of(fluffyProperties.getUpload().getDir()).toAbsolutePath().normalize();
        String pid = productId == null ? "tmp" : String.valueOf(productId);
        LocalDate now = LocalDate.now();

        Path dir = root
                .resolve("products")
                .resolve(pid)
                .resolve(String.valueOf(now.getYear()))
                .resolve(String.format("%02d", now.getMonthValue()));
        Files.createDirectories(dir);

        String filename = UUID.randomUUID() + "." + ext;
        Path target = dir.resolve(filename);
        file.transferTo(target);

        // URL path: /uploads/products/{pid}/{yyyy}/{MM}/{file}
        String urlPath = "/uploads/products/" + pid + "/" + now.getYear() + "/" + String.format("%02d", now.getMonthValue()) + "/" + filename;

        String origin = request.getHeader("Origin");
        return Map.of(
                "success", true,
                "url", urlPath,
                "contentType", contentType,
                "size", file.getSize(),
                "origin", origin == null ? "" : origin
        );
    }

    private static String extFromFilename(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) return "";
        String name = originalFilename.trim();
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) return "";
        return name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private static String extFromContentType(String contentType) {
        String ct = contentType.toLowerCase(Locale.ROOT);
        if (ct.contains("jpeg")) return "jpg";
        if (ct.contains("png")) return "png";
        if (ct.contains("webp")) return "webp";
        if (ct.contains("gif")) return "gif";
        return "";
    }
}
