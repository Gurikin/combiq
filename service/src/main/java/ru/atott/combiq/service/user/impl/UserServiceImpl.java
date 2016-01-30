package ru.atott.combiq.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.atott.combiq.dao.entity.UserEntity;
import ru.atott.combiq.dao.repository.UserRepository;
import ru.atott.combiq.service.ServiceException;
import ru.atott.combiq.service.bean.User;
import ru.atott.combiq.service.bean.UserQualifier;
import ru.atott.combiq.service.bean.UserType;
import ru.atott.combiq.service.mapper.UserMapper;
import ru.atott.combiq.service.user.GithubRegistrationContext;
import ru.atott.combiq.service.user.UserNotFoundException;
import ru.atott.combiq.service.user.UserService;
import ru.atott.combiq.service.user.VkRegistrationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserMapper userMapper = new UserMapper();
    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByLoginAndType(String login, UserType type) {
        return userMapper.safeMap(findEntityByLoginAndType(login, type));
    }

    @Override
    public User findByQualifier(UserQualifier userQualifier) {
        return findByLoginAndType(userQualifier.getLogin(), userQualifier.getType());
    }

    @Override
    public User registerUserViaGithub(GithubRegistrationContext context) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(context.getEmail());
        userEntity.setLogin(context.getLogin());
        userEntity.setHome(context.getHome());
        userEntity.setLocation(context.getLocation());
        userEntity.setName(context.getName());
        userEntity.setType(UserType.github.name());
        userEntity.setAvatarUrl(context.getAvatarUrl());

        userEntity = userRepository.index(userEntity);
        return userMapper.map(userEntity);
    }

    @Override
    public User registerUserViaVk(VkRegistrationContext context) {
        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(context.getUid());
        userEntity.setLocation(context.getLocation());
        userEntity.setName(context.getName());
        userEntity.setType(UserType.vk.name());
        userEntity.setAvatarUrl(context.getAvatarUrl());

        userEntity = userRepository.index(userEntity);
        return userMapper.map(userEntity);
    }

    @Override
    public User updateGithubUser(GithubRegistrationContext context) {
        UserEntity userEntity = userRepository.findByLoginAndType(context.getLogin(), UserType.github.name()).get(0);

        userEntity.setLogin(context.getLogin());
        userEntity.setHome(context.getHome());
        userEntity.setLocation(context.getLocation());
        userEntity.setName(context.getName());
        userEntity.setType(UserType.github.name());
        userEntity.setAvatarUrl(context.getAvatarUrl());

        userRepository.save(userEntity);
        return userMapper.map(userEntity);
    }

    @Override
    public User updateVkUser(VkRegistrationContext context) {
        UserEntity userEntity = userRepository.findByLoginAndType(context.getUid(), UserType.vk.name()).get(0);

        userEntity.setLogin(context.getUid());
        userEntity.setLocation(context.getLocation());
        userEntity.setName(context.getName());
        userEntity.setType(UserType.vk.name());
        userEntity.setAvatarUrl(context.getAvatarUrl());

        userRepository.save(userEntity);
        return userMapper.map(userEntity);
    }

    @Override
    public User findById(String userId) {
        UserEntity userEntity = userRepository.findOne(userId);

        if (userEntity != null) {
            return userMapper.map(userEntity);
        }

        return null;
    }

    @Override
    public void grantRole(UserQualifier userQualifier, String role) throws UserNotFoundException {
        UserEntity entity = findEntityByQualifier(userQualifier);

        if (entity == null) {
            throw new UserNotFoundException(userQualifier.toString());
        }

        List<String> roles = Optional
                .ofNullable(entity.getRoles())
                .map(ArrayList::new)
                .orElse(new ArrayList<>())
                .stream()
                .distinct()
                .collect(Collectors.toList());

        if (!roles.contains(role)) {
            roles.add(role);
        }

        entity.setRoles(roles);

        userRepository.save(entity);
    }

    @Override
    public void revokeRole(UserQualifier userQualifier, String role) throws UserNotFoundException {
        UserEntity entity = findEntityByQualifier(userQualifier);

        if (entity == null) {
            throw new UserNotFoundException(userQualifier.toString());
        }

        List<String> roles = Optional
                .ofNullable(entity.getRoles())
                .map(ArrayList::new)
                .orElse(new ArrayList<>())
                .stream()
                .distinct()
                .collect(Collectors.toList());

        roles.remove(role);

        entity.setRoles(roles);

        userRepository.save(entity);
    }

    private UserEntity findEntityByLoginAndType(String login, UserType type) {
        List<UserEntity> result = userRepository.findByLoginAndType(login, type.name());

        UserEntity userEntity = null;
        if (result.size() == 1) {
            userEntity = result.get(0);
        }

        if (userEntity != null) {
            return userEntity;
        }

        if (result.size() == 0) {
            return null;
        }

        throw new ServiceException(String.format("There are more then one user with login: %s", login));
    }

    private UserEntity findEntityByQualifier(UserQualifier userQualifier) {
        return findEntityByLoginAndType(userQualifier.getLogin(), userQualifier.getType());
    }
}
