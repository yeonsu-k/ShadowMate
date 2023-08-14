package com.newsainturtle.shadowmate.auth.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CertifyEmailRequest {
    @Email
    @NotNull
    private String email;
}
