package app.controller;

import app.dto.UserDto;
import app.dto.UserRequest;
import app.entity.User;
import app.mapper.UserMapper;
import app.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    public UserController(UserService service, UserMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserRequest req) {
        User created = service.createUser(mapper.toEntity(req));
        return ResponseEntity.status(201).body(mapper.toDto(created));
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        return mapper.toDto(service.getUserById(id));
    }

    @GetMapping
    public List<UserDto> getAll() {
        return service.getAllUsers().stream().map(mapper::toDto).toList();
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserRequest req) {
        service.getUserById(id);
        User updated = service.updateUser(id, mapper.toEntity(req));
        return mapper.toDto(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
