package prv.saevel.users.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long userId;

    private String username;

    private String name;

    private String surname;

    private String country;
}
