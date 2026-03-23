package com.example.demo.service;

import com.example.demo.model.Account;
import com.example.demo.model.Role;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initDefaultAccounts() {
        // Lưu trong DB đúng tên ADMIN, STUDENT
        Role adminRole = roleRepository.findByName("ADMIN")
            .orElseGet(() -> roleRepository.save(new Role(null, "ADMIN")));
        Role studentRole = roleRepository.findByName("STUDENT")
            .orElseGet(() -> roleRepository.save(new Role(null, "STUDENT")));

        accountRepository.findByLoginName("admin").ifPresentOrElse(existingAdmin -> {
            Set<Role> roles = existingAdmin.getRoles() != null ? existingAdmin.getRoles() : new HashSet<>();
            // Đảm bảo admin CHỈ có quyền ADMIN
            roles.add(adminRole);
            roles.remove(studentRole);
            existingAdmin.setRoles(roles);
            accountRepository.save(existingAdmin);
        }, () -> {
            Account admin = new Account();
            admin.setLoginName("admin");
            admin.setPassword(passwordEncoder.encode("123"));
            admin.setEmail("admin@example.com");
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole); // admin chỉ có role ADMIN
            admin.setRoles(roles);
            accountRepository.save(admin);
        });

        accountRepository.findByLoginName("user1").ifPresentOrElse(existingUser -> {
            Set<Role> roles = existingUser.getRoles() != null ? existingUser.getRoles() : new HashSet<>();
            if (!roles.contains(studentRole)) {
                roles.add(studentRole);
            }
            existingUser.setRoles(roles);
            accountRepository.save(existingUser);
        }, () -> {
            Account user = new Account();
            user.setLoginName("user1");
            user.setPassword(passwordEncoder.encode("123"));
            user.setEmail("user1@example.com");
            Set<Role> roles = new HashSet<>();
            roles.add(studentRole);
            user.setRoles(roles);
            accountRepository.save(user);
        });
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByLoginName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Could not find user " + username));

        return new org.springframework.security.core.userdetails.User(
                account.getLoginName(),
                account.getPassword(),
                account.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                        .collect(Collectors.toSet())
        );
    }
}
