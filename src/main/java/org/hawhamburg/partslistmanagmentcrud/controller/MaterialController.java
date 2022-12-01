package org.hawhamburg.partslistmanagmentcrud.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.hawhamburg.partslistmanagmentcrud.controller.dto.ApiError;
import org.hawhamburg.partslistmanagmentcrud.controller.dto.MaterialDTO;
import org.hawhamburg.partslistmanagmentcrud.model.Material;
import org.hawhamburg.partslistmanagmentcrud.service.MaterialAlreadyExistsException;
import org.hawhamburg.partslistmanagmentcrud.service.MaterialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/materials")
public class MaterialController {

    private static final String RESOURCE_IDENTIFIER = "/{name}";

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @Operation(
            summary = "Retrieve all Materials",
            description = "This retrieves all Materials.")
    @GetMapping
    public List<MaterialDTO> getAllMaterials() {
        return materialService.fetchAllMaterials().stream().map(MaterialController::toMaterialDTO).toList();
    }

    @ApiResponse(
            responseCode = "200",
            description = "Found Material"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Material not found",
            content = @Content
    )
    @GetMapping(RESOURCE_IDENTIFIER)
    public ResponseEntity<MaterialDTO> getMaterial(@PathVariable String name) {
        Material material = materialService.fetchMaterial(name);
        if (material == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(toMaterialDTO(material));
        }

    }

    @ApiResponse(
            responseCode = "201",
            description = "Created new Material",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MaterialDTO.class))
            }
    )
    @ApiResponse(
            responseCode = "409",
            description = "Material already exists",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))
            }
    )
    @PostMapping
    public ResponseEntity<?> createNewMaterial(@RequestBody MaterialDTO materialDTO) {
        try {
            Material newMaterial = materialService.createNewMaterial(materialDTO.name(), materialDTO.price());
            URI locationHeader = ServletUriComponentsBuilder.fromCurrentRequest().path(RESOURCE_IDENTIFIER).buildAndExpand(newMaterial.getName()).toUri();
            return ResponseEntity.created(locationHeader).body(toMaterialDTO(newMaterial));

        } catch (MaterialAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(e.getMessage()));
        }
    }

    @ApiResponse(
            responseCode = "200",
            description = "Replaced Material",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MaterialDTO.class))
            }

    )
    @ApiResponse(
            responseCode = "201",
            description = "Created new Material",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MaterialDTO.class))
            }
    )
    @ApiResponse(
            responseCode = "400",
            description = "Resource identifier and material name do not match",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))
            }
    )
    @PutMapping(RESOURCE_IDENTIFIER)
    public ResponseEntity<?> createOrReplaceMaterial(@PathVariable String name, @RequestBody MaterialDTO materialDTO) {
        if (!name.equals(materialDTO.name()))
            return ResponseEntity.badRequest().body(new ApiError("Resource identifier and material name do not match"));

        Pair<Boolean, Material> replacedMaterialPair = materialService.createOrReplaceMaterial(materialDTO.name(), materialDTO.price());
        URI locationHeader = ServletUriComponentsBuilder.fromCurrentRequest().path(RESOURCE_IDENTIFIER).buildAndExpand(name).toUri();

        if (replacedMaterialPair.getLeft()) {
            return ResponseEntity.ok(toMaterialDTO(replacedMaterialPair.getRight()));
        } else {
            return ResponseEntity.created(locationHeader).body(toMaterialDTO(replacedMaterialPair.getRight()));
        }
    }

    @ApiResponse(
            responseCode = "204",
            description = "Deleted Material",
            content = @Content
    )
    @ApiResponse(
            responseCode = "404",
            description = "Material not found",
            content = @Content
    )
    @DeleteMapping(RESOURCE_IDENTIFIER)
    public ResponseEntity<?> deleteMaterial(@PathVariable String name) {
        if (materialService.removeMaterial(name) == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    public static MaterialDTO toMaterialDTO(Material material) {
        return new MaterialDTO(material.getName(), material.getPrice());
    }
}
