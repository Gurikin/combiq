package ru.atott.combiq.service.user;

import com.hazelcast.core.IMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.atott.combiq.dao.entity.UserEntity;
import ru.atott.combiq.dao.repository.UserRepository;
import ru.atott.combiq.service.ServiceException;
import ru.atott.combiq.service.bean.User;
import ru.atott.combiq.service.bean.UserQualifier;
import ru.atott.combiq.service.bean.UserType;
import ru.atott.combiq.service.mapper.UserMapper;
import ru.atott.combiq.service.site.EventService;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserMapper userMapper = new UserMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventService eventService;

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
        userEntity.setRegisterDate(new Date());

        eventService.createRegisterUserEvent(UserType.github, context.getLogin());

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
        userEntity.setRegisterDate(new Date());

        eventService.createRegisterUserEvent(UserType.vk, context.getUid());

        userEntity = userRepository.index(userEntity);
        return userMapper.map(userEntity);
    }

    @Override
    public User registerUserViaFacebook(FacebookRegistrationContext context) {
        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(context.getId());
        userEntity.setName(context.getName());
        userEntity.setType(UserType.facebook.name());
        userEntity.setAvatarUrl(context.getAvatarUrl());
        userEntity.setRegisterDate(new Date());

        eventService.createRegisterUserEvent(UserType.facebook, context.getId());

        userEntity = userRepository.index(userEntity);
        return userMapper.map(userEntity);
    }

    @Override
    public User registerUserViaStackexchange(StackexchangeRegistrationContext context) {
        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(context.getUid());
        userEntity.setLocation(context.getLocation());
        userEntity.setName(context.getName());
        userEntity.setType(UserType.stackexchange.name());
        userEntity.setAvatarUrl(context.getAvatarUrl());
        userEntity.setRegisterDate(new Date());

        eventService.createRegisterUserEvent(UserType.stackexchange, context.getUid());

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

        if (userEntity.getRegisterDate() == null) {
            userEntity.setRegisterDate(new Date());
        }

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

        if (userEntity.getRegisterDate() == null) {
            userEntity.setRegisterDate(new Date());
        }

        userRepository.save(userEntity);
        return userMapper.map(userEntity);
    }

    @Override
    public User updateFacebookUser(FacebookRegistrationContext context) {
        UserEntity userEntity = userRepository.findByLoginAndType(context.getId(), UserType.facebook.name()).get(0);

        userEntity.setLogin(context.getId());
        userEntity.setName(context.getName());
        userEntity.setType(UserType.facebook.name());
        userEntity.setAvatarUrl(context.getAvatarUrl());

        if (userEntity.getRegisterDate() == null) {
            userEntity.setRegisterDate(new Date());
        }

        userRepository.save(userEntity);
        return userMapper.map(userEntity);
    }

    @Override
    public User updateStackexchangeUser(StackexchangeRegistrationContext context) {
        UserEntity userEntity = userRepository.findByLoginAndType(context.getUid(), UserType.stackexchange.name()).get(0);

        userEntity.setLogin(context.getUid());
        userEntity.setLocation(context.getLocation());
        userEntity.setName(context.getName());
        userEntity.setType(UserType.stackexchange.name());
        userEntity.setAvatarUrl(context.getAvatarUrl());

        if (userEntity.getRegisterDate() == null) {
            userEntity.setRegisterDate(new Date());
        }

        userRepository.save(userEntity);
        return userMapper.map(userEntity);
    }

    @Override
    public Page<User> getRegisteredUsers(long page, long size) {
        Pageable pageable = new PageRequest((int) page, (int) size, Sort.Direction.DESC, UserEntity.REGISTER_DATE_FIELD);
        Page<UserEntity> result = userRepository.findAll(pageable);
        return result.map(userEntity -> userMapper.map(userEntity));
    }

    @Override
    public List<User> getLastRegisteredUsers(long count) {
        PageRequest pageRequest = new PageRequest(0, (int) count, Sort.Direction.DESC, UserEntity.REGISTER_DATE_FIELD);
        List<UserEntity> users = userRepository.findAll(pageRequest).getContent();
        return userMapper.toList(users);
    }

    @Override
    public User findById(String userId) {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        IMap userMap = hazelcastInstance.getMap("User");
        if (userMap.containsKey(userId)) {
            return (User) userMap.get(userId);
        }

        UserEntity userEntity = userRepository.findOne(userId);
        User user = userMapper.map(userEntity);
        if (userEntity != null) {
            userMap.put(userId, user);
            return user;
        }

        return null;
    }

    public long getCountRegisteredUsers() {
        return userRepository.count();
    }

    @Override
    public long getCountRegisteredUsersSince(Date since) {
        return userRepository.countByRegisterDateGreaterThanEqual(since);
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

    public synchronized void updateNick(String id, String nick) throws ServiceException {
        if (!isNickUniq(nick)) {
            throw new ServiceException(String.format("Ник %s занят, придумайте другой.", nick));
        }
        if (!StringUtils.isEmpty(nick) && nick.matches("^[A-Za-z0-9]{1}[A-Za-z0-9 ]{0,39}$")) {
            nick = nick.trim();
            UserEntity userEntity = userRepository.findOne(id);
            userEntity.setNick(nick);
            userRepository.save(userEntity);
        }
        else {
            throw new ServiceException(String.format("Ник %s не корректный", nick));
        }
    }

    public boolean isNickUniq(String nick) {
        return !userRepository.findByNick(nick).stream().anyMatch(obj -> obj.getNick().equals(nick));
    }
}
