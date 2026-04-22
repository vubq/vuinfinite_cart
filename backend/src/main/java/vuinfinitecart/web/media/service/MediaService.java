package vuinfinitecart.web.media.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vuinfinitecart.web.common.exception.AppException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final Cloudinary cloudinary;

    @SuppressWarnings("unchecked")
    public List<String> listFolders(String path) {
        try {
            String folderPath = normalizePath(path);
            Map result = cloudinary.api().subFolders(folderPath, ObjectUtils.emptyMap());
            List<Map> folders = (List<Map>) result.get("folders");
            return folders.stream().map(f -> (String) f.get("name")).toList();
        } catch (Exception e) {
            throw AppException.internalError("Failed to list folders: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Map> listFiles(String path, String type) {
        try {
            String folderPath = normalizePath(path);
            Map result = cloudinary.api().resources(ObjectUtils.asMap(
                    "type", type,
                    "prefix", folderPath,
                    "max_results", 500
            ));
            List<Map> resources = (List<Map>) result.get("resources");

            // Filter to only show files directly in this folder
            return resources.stream().filter(r -> {
                String resFolder = (String) r.get("folder");
                if (resFolder == null) resFolder = "";
                return resFolder.equals(folderPath);
            }).toList();
        } catch (Exception e) {
            throw AppException.internalError("Failed to list files: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Map> uploadFiles(MultipartFile[] files, String folder, boolean isPrivate) {
        List<Map> results = new ArrayList<>();
        String folderPath = normalizePath(folder);

        for (MultipartFile file : files) {
            try {
                String originalName = file.getOriginalFilename();
                String baseName = stripExtension(originalName);
                String resolvedPublicId = resolveUniquePublicId(folderPath, baseName, isPrivate ? "private" : "upload");

                Map params = ObjectUtils.asMap(
                        "public_id", resolvedPublicId,
                        "folder", folderPath,
                        "type", isPrivate ? "private" : "upload",
                        "use_filename", true,
                        "unique_filename", false,
                        "overwrite", false,
                        "resource_type", "auto"
                );
                results.add(cloudinary.uploader().upload(file.getBytes(), params));
            } catch (IOException e) {
                throw AppException.internalError("Upload failed for " + file.getOriginalFilename() + ": " + e.getMessage());
            }
        }
        return results;
    }

    public void createFolder(String path) {
        try {
            cloudinary.api().createFolder(path, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw AppException.internalError("Failed to create folder: " + e.getMessage());
        }
    }

    public void deleteFiles(List<String> publicIds, String type) {
        try {
            cloudinary.api().deleteResources(publicIds, ObjectUtils.asMap("type", type));
        } catch (Exception e) {
            throw AppException.internalError("Failed to delete files: " + e.getMessage());
        }
    }

    public void renameFile(String fromPublicId, String toPublicId, String type) {
        try {
            cloudinary.uploader().rename(fromPublicId, toPublicId, ObjectUtils.asMap("type", type));
        } catch (Exception e) {
            throw AppException.internalError("Failed to rename file: " + e.getMessage());
        }
    }

    /**
     * Rename a folder by moving all resources inside it to the new path, then deleting the old folder.
     * Cloudinary does not support native folder renaming.
     */
    @SuppressWarnings("unchecked")
    public void renameFolder(String oldPath, String newPath, String type) {
        try {
            // List all resources under old path
            Map result = cloudinary.api().resources(ObjectUtils.asMap(
                    "type", type,
                    "prefix", oldPath,
                    "max_results", 500
            ));
            List<Map> resources = (List<Map>) result.get("resources");

            // Rename each resource
            for (Map resource : resources) {
                String oldPublicId = (String) resource.get("public_id");
                String newPublicId = newPath + oldPublicId.substring(oldPath.length());
                cloudinary.uploader().rename(oldPublicId, newPublicId, ObjectUtils.asMap("type", type));
            }

            // Delete the old (now empty) folder
            cloudinary.api().deleteFolder(oldPath, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw AppException.internalError("Failed to rename folder: " + e.getMessage());
        }
    }

    /**
     * Delete a folder and ALL its contents (files and sub-folders recursively).
     */
    @SuppressWarnings("unchecked")
    public void deleteFolder(String path, String type) {
        try {
            // Delete all resources inside recursively
            deleteResourcesInFolder(path, type);

            // Now delete the (empty) folder itself
            cloudinary.api().deleteFolder(path, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw AppException.internalError("Failed to delete folder: " + e.getMessage());
        }
    }

    public String generateSignedUrl(String publicId) {
        return cloudinary.url().type("private").signed(true).generate(publicId);
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

    private String normalizePath(String path) {
        return (path == null || path.trim().isEmpty() || path.equals("/")) ? "" : path;
    }

    private String stripExtension(String filename) {
        if (filename == null) return "file";
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(0, dotIndex) : filename;
    }

    /**
     * Check if a public_id already exists in Cloudinary and find a unique name.
     * If "folder/name" exists → try "folder/name (1)", "folder/name (2)", ...
     */
    private String resolveUniquePublicId(String folder, String baseName, String type) {
        String candidate = folder.isEmpty() ? baseName : folder + "/" + baseName;
        if (!publicIdExists(candidate, type)) return baseName;

        int counter = 1;
        while (true) {
            String newBase = baseName + " (" + counter + ")";
            String newCandidate = folder.isEmpty() ? newBase : folder + "/" + newBase;
            if (!publicIdExists(newCandidate, type)) return newBase;
            counter++;
        }
    }

    @SuppressWarnings("unchecked")
    private boolean publicIdExists(String publicId, String type) {
        try {
            cloudinary.api().resource(publicId, ObjectUtils.asMap("type", type));
            return true;
        } catch (Exception e) {
            // Resource not found → safe to use
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private void deleteResourcesInFolder(String path, String type) throws Exception {
        // Delete all files directly under this prefix
        Map result = cloudinary.api().deleteResourcesByPrefix(path, ObjectUtils.asMap("type", type));

        // Recurse into sub-folders
        Map subResult = cloudinary.api().subFolders(path, ObjectUtils.emptyMap());
        List<Map> subFolders = (List<Map>) subResult.get("folders");
        for (Map sub : subFolders) {
            String subPath = (String) sub.get("path");
            deleteResourcesInFolder(subPath, type);
            cloudinary.api().deleteFolder(subPath, ObjectUtils.emptyMap());
        }
    }
}
