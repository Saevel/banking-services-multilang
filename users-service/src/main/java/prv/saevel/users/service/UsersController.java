package prv.saevel.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static prv.saevel.users.service.Lenses.lens;

@RestController
public class UsersController {

    @Autowired
    private UsersRepository repository;

    @GetMapping("users")
    public List<User> getAllActiveUsers(){
        return repository.findActiveUsers()
                .stream()
                .map(this::toBusinessModel)
                .collect(Collectors.toList());
    }

    @PostMapping("users")
    public User createUser(@RequestBody User user){
        // TODO: Validation? Service?
        // TODO: Creating accounts for new users
        return toBusinessModel(repository.save(toPersistenceModel(UserStatus.ACTIVE).apply(user)));
    }

    @DeleteMapping("users/{id}")
    public boolean deleteUserById(@PathVariable long id){
        // TODO: Removing accounts for deleted users
        return repository.findById(id)
                .map(lens(user -> user.setStatus(UserStatus.DELETED)))
                .map(repository::save)
                .isPresent();
    }

    private User toBusinessModel(UserModel model){
        return new User(model.getUserId(), model.getUsername(), model.getName(), model.getSurname(), model.getCountry());
    }

    private Function<User, UserModel> toPersistenceModel(UserStatus status){
        return user -> new UserModel(
                user.getUserId(), user.getUsername(), user.getName(), user.getSurname(), user.getCountry(), status
        );
    }
}