package com.tms.ticket_management.controller;

import com.tms.ticket_management.config.JwtService;
import com.tms.ticket_management.dto.AuthDTO;
import com.tms.ticket_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthDTO.LoginResponse> login(@RequestBody AuthDTO.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails=userService.loadUserByUsername(request.getUsername());
        String token=jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthDTO.LoginResponse(
                token,
                userDetails.getUsername(),
                userDetails.getAuthorities().iterator().next().getAuthority()
        ));
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthDTO.RegisterRequest request) {
        User user=new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(User.Role.USER);

        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully");
    }
}
