package com.mediconnect.backend.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public List<UserResponse> getAll() {
        return repo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public UserResponse getById(Long id) {
        User u = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
        return toResponse(u);
    }

    public UserResponse create(UserRequest req) {
        if (repo.existsByUserId(req.getUserId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "userId already exists");
        if (repo.existsByGmail(req.getGmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "gmail already exists");

        User saved = repo.save(new User(req.getUserId(), req.getName(), req.getGmail()));
        return toResponse(saved);
    }

    public UserResponse update(Long id, UserRequest req) {
        User u = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));

        repo.findByUserId(req.getUserId()).ifPresent(existing -> {
            if (!existing.getId().equals(id))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "userId already exists");
        });
        repo.findByGmail(req.getGmail()).ifPresent(existing -> {
            if (!existing.getId().equals(id))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "gmail already exists");
        });

        u.setUserId(req.getUserId());
        u.setName(req.getName());
        u.setGmail(req.getGmail());
        return toResponse(repo.save(u));
    }

    public void delete(Long id) {
        if (!repo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id);
        repo.deleteById(id);
    }


    private UserResponse toResponse(User u) {
    return new UserResponse(u.getId(), u.getUserId(), u.getName(), u.getGmail());
}
}