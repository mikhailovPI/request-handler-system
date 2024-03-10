package ru.mikhailov.requesthandlersystem.master.user.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.mikhailov.requesthandlersystem.security.config.Permission;

import javax.persistence.*;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = Role.TABLE_ROLES, schema = Role.SCHEMA_TABLE)
public class Role {

    public static final String TABLE_ROLES = "roles";
    public static final String SCHEMA_TABLE = "public";
    public static final String ROLE_ID = "role_id";
    public static final String ROLE_NAME = "role_name";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ROLE_ID)
    Long id;

    @Column(name = ROLE_NAME)
    String name;

    @ElementCollection(targetClass = Permission.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permissions")
    Set<Permission> permissions = new HashSet<>();

    public Set<GrantedAuthority> getAuthorities() {
        return permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
    }

    public static Set<Permission> getPermissionsForRole(String roleName) {
        Set<Permission> permissions = EnumSet.noneOf(Permission.class);
        switch (roleName) {
            case "USER":
                permissions.add(Permission.USER);
                break;
            case "OPERATOR":
                permissions.add(Permission.OPERATOR);
                break;
            case "ADMIN":
                permissions.add(Permission.ADMIN);
                break;
        }
        return permissions;
    }
}
