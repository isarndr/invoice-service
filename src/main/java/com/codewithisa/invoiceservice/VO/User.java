package com.codewithisa.invoiceservice.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class User {
    @Schema(example = "1")
    private Long userId;
    @Schema(example = "isarndr")
    private String username;
    @Schema(example = "isa@yahoo.com")
    private String emailAddress;
    @Schema(example = "123")
    private String password;

    public User(Long id, String email, String password) {
        this.userId = id;
        this.emailAddress = email;
        this.password = password;
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.emailAddress = email;
        this.password = password;
    }

    public User() {

    }


}
