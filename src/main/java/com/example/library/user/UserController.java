package com.example.library.user;

import com.example.library.user.dto.CreateUserRequest;
import com.example.library.user.dto.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") @Min(0) final int page,
            @RequestParam(defaultValue = "10") @Min(1) final int size,
            @RequestParam(defaultValue = "username") @Pattern(regexp = "username") final String sortBy,
            @RequestParam(defaultValue = "desc") @Pattern(regexp = "ASC|DESC", flags = Pattern.Flag.CASE_INSENSITIVE) final String sortOrder
    ) {
        return ResponseEntity.ok(userService.getAllUsers(page, size, sortBy, sortOrder));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(final Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getUser(currentUser.getId()));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN') or @ownership.isProfileOwner(principal, #userId)")
    public ResponseEntity<UserResponse> getUser(@PathVariable final UUID userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody final CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @ownership.isProfileOwner(principal, #userId)")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable final UUID userId,
            @Valid @RequestBody final CreateUserRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable final UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
