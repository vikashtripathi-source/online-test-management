package com.tech.test.controller;

import com.tech.test.dto.AddressDTO;
import com.tech.test.enums.AddressType;
import com.tech.test.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addresses")
@Tag(name = "Address Management API", description = "Operations related to managing student addresses including Home and College address types")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    @Operation(summary = "Create Address", description = "Create a new address entry for a student (Home or College type)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Address successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid address data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO savedAddress = addressService.createAddress(addressDTO);
        return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get All Addresses", description = "Retrieve all address entries from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all addresses"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Address By ID", description = "Retrieve a specific address by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the address"),
        @ApiResponse(responseCode = "404", description = "Address not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AddressDTO> getAddressById(
            @Parameter(description = "ID of the address to retrieve") @PathVariable Long id) {
        return addressService
                .getAddressById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Address", description = "Update an existing address with new information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Address successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid address data"),
        @ApiResponse(responseCode = "404", description = "Address not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AddressDTO> updateAddress(
            @Parameter(description = "ID of the address to update") @PathVariable Long id,
            @Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO updatedAddress = addressService.updateAddress(id, addressDTO);
        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Address", description = "Delete an address from the system by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Address successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Address not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteAddress(
            @Parameter(description = "ID of the address to delete") @PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get Addresses By Type", description = "Retrieve all addresses filtered by type (HOME or COLLEGE)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved addresses by type"),
        @ApiResponse(responseCode = "400", description = "Invalid address type"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AddressDTO>> getAddressesByType(
            @Parameter(description = "Type of address to filter (HOME or COLLEGE)") @PathVariable AddressType type) {
        return ResponseEntity.ok(addressService.getAddressesByType(type));
    }
}
