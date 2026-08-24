package com.evalorithm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    private String firstName;
    private String lastName;
    private String phone;
    private String profilePhotoUrl;
}
