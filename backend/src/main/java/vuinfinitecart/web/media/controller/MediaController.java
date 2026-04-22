package vuinfinitecart.web.media.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vuinfinitecart.web.common.response.ApiResponse;
import vuinfinitecart.web.media.service.MediaService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/media")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
public class MediaController {

    private final MediaService mediaService;

    @GetMapping("/folders")
    public ApiResponse<List<String>> listFolders(@RequestParam(required = false) String path) {
        return ApiResponse.ok(mediaService.listFolders(path));
    }

    @GetMapping("/files")
    public ApiResponse<List<Map>> listFiles(
            @RequestParam(required = false) String path,
            @RequestParam(defaultValue = "upload") String type) {
        return ApiResponse.ok(mediaService.listFiles(path, type));
    }

    @PostMapping("/upload")
    public ApiResponse<List<Map>> uploadFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(required = false) String folder,
            @RequestParam(defaultValue = "false") boolean isPrivate) {
        return ApiResponse.ok("Files uploaded successfully", mediaService.uploadFiles(files, folder, isPrivate));
    }

    @PostMapping("/folders")
    public ApiResponse<Void> createFolder(@RequestParam String path) {
        mediaService.createFolder(path);
        return ApiResponse.ok("Folder created successfully");
    }

    /** Rename a folder — moves all its contents to the new path. */
    @PutMapping("/folders/rename")
    public ApiResponse<Void> renameFolder(
            @RequestParam String oldPath,
            @RequestParam String newPath,
            @RequestParam(defaultValue = "upload") String type) {
        mediaService.renameFolder(oldPath, newPath, type);
        return ApiResponse.ok("Folder renamed successfully");
    }

    @DeleteMapping("/files")
    public ApiResponse<Void> deleteFiles(
            @RequestParam List<String> publicIds,
            @RequestParam(defaultValue = "upload") String type) {
        mediaService.deleteFiles(publicIds, type);
        return ApiResponse.ok("Files deleted successfully");
    }

    @PutMapping("/files/rename")
    public ApiResponse<Void> renameFile(
            @RequestParam String fromPublicId,
            @RequestParam String toPublicId,
            @RequestParam(defaultValue = "upload") String type) {
        mediaService.renameFile(fromPublicId, toPublicId, type);
        return ApiResponse.ok("File renamed successfully");
    }

    /** Delete a folder and ALL its contents. */
    @DeleteMapping("/folders")
    public ApiResponse<Void> deleteFolder(
            @RequestParam String path,
            @RequestParam(defaultValue = "upload") String type) {
        mediaService.deleteFolder(path, type);
        return ApiResponse.ok("Folder deleted successfully");
    }

    @GetMapping("/private-url")
    public ApiResponse<String> getPrivateUrl(@RequestParam String publicId) {
        return ApiResponse.ok(mediaService.generateSignedUrl(publicId));
    }
}
