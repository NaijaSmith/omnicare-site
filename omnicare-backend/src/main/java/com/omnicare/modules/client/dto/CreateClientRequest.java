package com.omnicare.modules.client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateClientRequest {
    @NotBlank(message = "Business name is required")
    private String businessName;

    @NotBlank(message = "Contact person is required")
    private String contactPerson;

    @Email
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String physicalAddress;
}
