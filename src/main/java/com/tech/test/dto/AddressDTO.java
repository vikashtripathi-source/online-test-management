package com.tech.test.dto;

import com.tech.test.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    private Long id;
    
    @NotBlank(message = "Street address cannot be blank")
    private String streetAddress;
    
    @NotBlank(message = "City cannot be blank")
    private String city;
    
    private String state;
    
    @NotBlank(message = "Zip code cannot be blank")
    private String zipCode;
    
    @NotBlank(message = "Country cannot be blank")
    private String country;
    
    @NotNull(message = "Address type cannot be null")
    private AddressType addressType;
    
    private String phoneNumber;
    private String email;
}
