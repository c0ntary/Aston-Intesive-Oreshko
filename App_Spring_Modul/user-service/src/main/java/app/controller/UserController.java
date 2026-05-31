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
    public ResponseEntity<UserDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(mapper.toDto(service.getUserById(id)));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(
                service.getAllUsers().stream().map(mapper::toDto).toList()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(
            @PathVariable("id") Long id,
            @RequestBody UserRequest req
    ) {
        User updated = service.updateUser(id, mapper.toEntity(req));
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}