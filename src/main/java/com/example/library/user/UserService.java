package com.example.library.user;

import com.example.library.loan.LoanRepository;
import com.example.library.user.constants.Role;
import com.example.library.user.dto.CreateUpdateUserRequest;
import com.example.library.user.dto.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUsers(final int page, final int size, final String sortBy, final String sortOrder) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository
                .findAll(pageable)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public UserResponse getUser(final UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(EntityNotFoundException::new);
        return mapper.toDto(user);
    }

    public UserResponse createUser(final CreateUpdateUserRequest request) {
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        return mapper.toDto(user);
    }

    public UserResponse updateUser(final UUID userId, final CreateUpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(EntityNotFoundException::new);

        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);
        return mapper.toDto(user);
    }

    @Transactional
    public void deleteUser(final UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Resource not found");
        }
        loanRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }
}
