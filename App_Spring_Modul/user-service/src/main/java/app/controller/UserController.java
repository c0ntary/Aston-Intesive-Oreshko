package app.controller;

import app.dto.UserDto;
import app.dto.UserRequest;
import app.hateoas.UserModelAssembler;
import app.service.UserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final UserModelAssembler assembler;

    public UserController(UserService service, UserModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @PostMapping
    public ResponseEntity<EntityModel<UserDto>> create(@RequestBody UserRequest req) {
        UserDto dto = service.createUser(req);
        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).getById(dto.id())).toUri())
                .body(assembler.toModel(dto));
    }

    @GetMapping("/{id}")
    public EntityModel<UserDto> getById(@PathVariable Long id) {
        return assembler.toModel(service.getUserById(id));
    }

    @GetMapping
    public CollectionModel<EntityModel<UserDto>> getAll() {
        List<EntityModel<UserDto>> users = service.getAllUsers()
                .stream()
                .map(assembler::toModel)
                .toList();

        return CollectionModel.of(
                users,
                linkTo(methodOn(UserController.class).getAll()).withSelfRel()
        );
    }

    @PutMapping("/{id}")
    public EntityModel<UserDto> update(@PathVariable Long id, @RequestBody UserRequest req) {
        return assembler.toModel(service.updateUser(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}