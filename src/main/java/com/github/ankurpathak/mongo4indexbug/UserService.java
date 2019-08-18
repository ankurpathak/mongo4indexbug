package com.github.ankurpathak.mongo4indexbug;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements IUserService {
    private final IUserRepository nameRepository;

    public UserService(IUserRepository nameRepository) {
        this.nameRepository = nameRepository;
    }

    @Override
    public User save(User user){
        return nameRepository.save(user);
    }
}
