package dev.earlspilner.users.service;

import dev.earlspilner.users.dto.UserDto;
import dev.earlspilner.users.entity.User;
import dev.earlspilner.users.mapper.UserMapper;
import dev.earlspilner.users.repository.UserRepository;
import dev.earlspilner.users.rest.advice.UserExistsException;
import dev.earlspilner.users.rest.advice.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alexander Dudkin
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDto saveUser(UserDto dto) {
        if (userRepository.existsByUsername(dto.username()) || userRepository.existsByEmail(dto.email()))
            throw new UserExistsException("User already exists");

        User user = userMapper.toUserEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUser(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toUserDto);
    }

    @Override
    @Transactional
    public UserDto updateUser(String username, UserDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        user.setName(dto.name());
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));

        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}