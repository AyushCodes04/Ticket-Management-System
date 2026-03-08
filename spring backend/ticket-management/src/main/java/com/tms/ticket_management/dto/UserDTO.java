package com.tms.ticket_management.dto;

import com.tms.ticket_management.model.User;
import lombok.Data;

@Data
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private User.Role role;

    public static UserDTO fromUser(User user) {
        UserDTO dto=new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
}
