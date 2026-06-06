package app.controller;

import app.dto.UserDto;
import app.dto.UserRequest;
import app.entity.User;
import app.mapper.UserMapper;
import app.service.UserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

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
        UserDto dto = mapper.toDto(created);

        dto.add(linkTo(methodOn(UserController.class).getById(dto.getId())).withSelfRel());
        dto.add(linkTo(methodOn(UserController.class).getAll()).withRel("all-users"));

        return ResponseEntity.status(201).body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable("id") Long id) {
        UserDto dto = mapper.toDto(service.getUserById(id));

        dto.add(linkTo(methodOn(UserController.class).getById(id)).withSelfRel());
        dto.add(linkTo(methodOn(UserController.class).getAll()).withRel("all-users"));
        dto.add(linkTo(methodOn(UserController.class).delete(id)).withRel("delete"));
        dto.add(linkTo(methodOn(UserController.class).update(id, null)).withRel("update"));

        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<UserDto>> getAll() {
        List<UserDto> users = service.getAllUsers()
                .stream()
                .map(mapper::toDto)
                .toList();

        users.forEach(u ->
                u.add(linkTo(methodOn(UserController.class).getById(u.getId())).withSelfRel())
        );

        CollectionModel<UserDto> model = CollectionModel.of(
                users,
                linkTo(methodOn(UserController.class).getAll()).withSelfRel()
        );

        return ResponseEntity.ok(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(
            @PathVariable("id") Long id,
            @RequestBody UserRequest req
    ) {
        User updated = service.updateUser(id, mapper.toEntity(req));
        UserDto dto = mapper.toDto(updated);

        dto.add(linkTo(methodOn(UserController.class).getById(id)).withSelfRel());
        dto.add(linkTo(methodOn(UserController.class).getAll()).withRel("all-users"));

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}