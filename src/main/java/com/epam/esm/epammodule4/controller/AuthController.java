package com.epam.esm.epammodule4.controller;

import com.epam.esm.epammodule4.exception.EmailAlreadyInUseException;
import com.epam.esm.epammodule4.exception.TokenRefreshException;
import com.epam.esm.epammodule4.exception.UsernameAlreadyTakenException;
import com.epam.esm.epammodule4.model.dto.request.CreateUserRequest;
import com.epam.esm.epammodule4.model.dto.request.LoginRequest;
import com.epam.esm.epammodule4.model.dto.request.SignupRequest;
import com.epam.esm.epammodule4.model.dto.request.TokenRefreshRequest;
import com.epam.esm.epammodule4.model.dto.response.JwtResponse;
import com.epam.esm.epammodule4.model.dto.response.MessageResponse;
import com.epam.esm.epammodule4.model.dto.response.TokenRefreshResponse;
import com.epam.esm.epammodule4.model.entity.RefreshToken;
import com.epam.esm.epammodule4.security.jwt.JwtUtils;
import com.epam.esm.epammodule4.service.UserService;
import com.epam.esm.epammodule4.service.implementation.RefreshTokenService;
import com.epam.esm.epammodule4.service.implementation.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;
    private final ModelMapper modelMapper;

    @PostMapping("/login")
    public JwtResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwtToken = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return new JwtResponse(jwtToken, refreshToken.getToken(), userDetails.getId(),
                userDetails.getUsername(), userDetails.getEmail(), roles);
    }

    @PostMapping("/register")
    public MessageResponse registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userService.existsByUsername(signupRequest.getUsername())) {
            throw new UsernameAlreadyTakenException("Error: Username is already taken!");
        }

        if (userService.existsByEmail(signupRequest.getEmail())) {
            throw new EmailAlreadyInUseException("Error: Email is already in use!");
        }

        CreateUserRequest createRequest = modelMapper.map(signupRequest, CreateUserRequest.class);

        userService.create(createRequest);

        return new MessageResponse("User registered successfully!");
    }

    @PostMapping("/refreshtoken")
    public TokenRefreshResponse refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUser(user);
                    return new TokenRefreshResponse(token, requestRefreshToken);
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    @PostMapping("/logout")
    public MessageResponse logout() {
        SecurityContextHolder.clearContext();

        return new MessageResponse("Logout successfully!");
    }
}