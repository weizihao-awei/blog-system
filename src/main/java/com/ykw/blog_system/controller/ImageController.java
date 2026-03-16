package com.ykw.blog_system.controller;

import com.ykw.blog_system.utils.SecurityUtil;
import com.ykw.blog_system.vo.ImageVO;
import com.ykw.blog_system.vo.ResultVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 图片上传控制器
 */
@RestController
@RequestMapping("/api/image")
public class ImageController {

    private final String uploadPath;

    public ImageController(@Value("${file.upload.path:uploads/}") String uploadPath) {
        // 如果是相对路径，转换为项目根目录下的绝对路径
        if (!uploadPath.startsWith("/") && !uploadPath.matches("^[A-Za-z]:.*")) {
            this.uploadPath = System.getProperty("user.dir") + "/" + uploadPath;
        } else {
            this.uploadPath = uploadPath;
        }
    }

    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png", "image/gif", "image/jpg", "image/webp"};
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 上传图片
     * @param category 图片分类（avatar、article等）
     * @param file 图片文件
     * @return 图片URI
     */
    @PostMapping("/upload")
    public ResultVO<ImageVO> uploadImage(
            @RequestParam("category") String category,
            @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return ResultVO.error("文件不能为空");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            return ResultVO.error("文件大小不能超过10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !isAllowedType(contentType)) {
            return ResultVO.error("只支持jpg、png、gif、webp格式的图片");
        }
        
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString() + "." + extension;
            
            Path categoryPath = Paths.get(uploadPath, category);
            if (!Files.exists(categoryPath)) {
                Files.createDirectories(categoryPath);
            }
            
            Path filePath = categoryPath.resolve(newFilename);
            file.transferTo(filePath.toFile());
            
            String imageUri = "/api/image/" + category + "/" + newFilename;
            
            ImageVO imageVO = new ImageVO();
            imageVO.setUri(imageUri);
            imageVO.setFilename(newFilename);

            
            return ResultVO.success("上传成功", imageVO);
        } catch (IOException e) {
            return ResultVO.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 更新图片（删除旧图片并上传新图片）
     * @param category 图片分类
     * @param oldUri 旧图片URI
     * @param file 新图片文件
     * @return 新图片URI
     */
    @PutMapping("/update")
    public ResultVO<ImageVO> updateImage(
            @RequestParam("category") String category,
            @RequestParam("oldUri") String oldUri,
            @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return ResultVO.error("文件不能为空");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            return ResultVO.error("文件大小不能超过10MB");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedType(contentType)) {
            return ResultVO.error("只支持jpg、png、gif、webp格式的图片");
        }
        
        try {
            if (oldUri != null && !oldUri.isEmpty()) {
                deleteImageByUri(oldUri);
            }
            
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString() + "." + extension;
            
            Path categoryPath = Paths.get(uploadPath, category);
            if (!Files.exists(categoryPath)) {
                Files.createDirectories(categoryPath);
            }
            
            Path filePath = categoryPath.resolve(newFilename);
            file.transferTo(filePath.toFile());
            
            String imageUri = "/api/image/" + category + "/" + newFilename;
            
            ImageVO imageVO = new ImageVO();
            imageVO.setUri(imageUri);
            imageVO.setFilename(newFilename);

            
            return ResultVO.success("更新成功", imageVO);
        } catch (IOException e) {
            return ResultVO.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 根据URI获取图片
     * @param category 图片分类
     * @param filename 文件名
     * @param response HTTP响应
     */
    @GetMapping("/{category}/{filename}")
    public void getImage(
            @PathVariable("category") String category,
            @PathVariable("filename") String filename,
            HttpServletResponse response) {
        
        Path filePath = Paths.get(uploadPath, category, filename);
        File file = filePath.toFile();
        
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        try {
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            response.setContentType(contentType);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"");
            
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除图片
     * @param uri 图片URI
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public ResultVO<Void> deleteImage(@RequestParam("uri") String uri) {
        try {
            boolean deleted = deleteImageByUri(uri);
            if (deleted) {
                return ResultVO.success("删除成功");
            } else {
                return ResultVO.error("文件不存在或删除失败");
            }
        } catch (Exception e) {
            return ResultVO.error("删除失败：" + e.getMessage());
        }
    }

    private boolean deleteImageByUri(String uri) throws IOException {
        if (uri == null || uri.isEmpty()) {
            return false;
        }
        
        String[] parts = uri.split("/");
        if (parts.length < 4) {
            return false;
        }
        
        String category = parts[3];
        String filename = parts[4];
        
        Path filePath = Paths.get(uploadPath, category, filename);
        File file = filePath.toFile();
        
        if (file.exists()) {
            return file.delete();
        }
        
        return false;
    }

    private boolean isAllowedType(String contentType) {
        for (String type : ALLOWED_TYPES) {
            if (type.equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "jpg";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "jpg";
        }
        
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
}