package com.example.library.user;

import com.example.library.user.dto.CreateUpdateUserRequest;
import com.example.library.user.dto.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    public List<UserResponse> getAllUsers(int page, int size, String sortBy, String sortOrder) {
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

    public UserResponse createUser(CreateUpdateUserRequest request) {
        User user = User.builder()
                .username(request.username())
                .password(request.password())
                .build();

        userRepository.save(user);
        return mapper.toDto(user);
    }

    public UserResponse updateUser(final UUID userId, CreateUpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(EntityNotFoundException::new);

        user.setUsername(request.username());
        user.setPassword(request.password());

        userRepository.save(user);
        return mapper.toDto(user);
    }

    public void deleteUser(final UUID userId) {
        userRepository.findById(userId)
                        .orElseThrow(EntityNotFoundException::new);
        userRepository.deleteById(userId);
    }
}
