package app.service;

import app.dto.UserDto;
import app.dto.UserRequest;

import java.util.List;

public interface UserService {

    UserDto createUser(UserRequest request);

    UserDto getUserById(Long id);

    List<UserDto> getAllUsers();

    UserDto updateUser(Long id, UserRequest request);

    void deleteUser(Long id);
}