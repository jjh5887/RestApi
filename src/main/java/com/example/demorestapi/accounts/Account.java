package com.example.demorestapi.accounts;


import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity @Getter @EqualsAndHashCode(of = "id")
@Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Account {

    @Id @GeneratedValue
    private  Integer id;

    private String email;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;
}
