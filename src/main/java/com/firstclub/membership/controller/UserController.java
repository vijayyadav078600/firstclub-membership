package com.firstclub.membership.controller;

import com.firstclub.membership.dto.request.CreateUserRequest;
import com.firstclub.membership.dto.response.ApiResponse;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.CohortType;
import com.firstclub.membership.exception.MembershipException;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management")
public class UserController {

    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new MembershipException("Email already registered: " + request.getEmail());
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .cohort(request.getCohort() != null ? request.getCohort() : CohortType.REGULAR)
                .build();
        User saved = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("User created", saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(userRepository.findAll()));
    }
}
