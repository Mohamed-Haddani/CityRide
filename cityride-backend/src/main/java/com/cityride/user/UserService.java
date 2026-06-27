package com.cityride.user;

import com.cityride.common.exception.ResourceNotFoundException;
import com.cityride.user.dto.UpdateProfileRequest;
import com.cityride.user.dto.UserResponse;
import com.cityride.user.dto.UserSummary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Recupere l'entite ou leve une 404. Methode interne reutilisee par les autres services. */
    @Transactional(readOnly = true)
    public User getEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Utilisateur", id));
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserSummary getPublicProfile(Long id) {
        return UserMapper.toSummary(getEntity(id));
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(Long id) {
        return UserMapper.toResponse(getEntity(id));
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = getEntity(userId);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhone(request.phone());
        user.setCity(request.city());
        user.setPhotoUrl(request.photoUrl());
        return UserMapper.toResponse(userRepository.save(user));
    }
}
