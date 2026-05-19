package com.ticketsystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectRequestDTO {

    @NotBlank(message = "Project name is required")
    private String projectName;

    private String projectDescription;

    private String projectStatus = "ACTIVE";

    private String projectOwner;
}
