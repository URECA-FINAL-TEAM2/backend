package com.beautymeongdang.domain.user.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroomerRegisterRequestDTO {
    private String nickName;
    private String phone;
    private String skill;
}