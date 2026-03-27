package com.tech.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "Image upload request for student profile picture")
public class ImageUploadDTO {

    @NotNull(message = "Image file is required")
    @Schema(
            description = "Image file to upload (max 2MB)",
            type = "string",
            format = "binary",
            example = "profile.jpg")
    private MultipartFile image;

    public long getImageSize() {
        return image != null ? image.getSize() : 0;
    }
}
