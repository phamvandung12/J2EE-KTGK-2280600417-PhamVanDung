package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.Role;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.RoleRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Controller
public class AuthController {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AccountRepository accountRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("account", new Account());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("account") Account account,
                           BindingResult bindingResult,
                           Model model) {
        Optional<Account> existing = accountRepository.findByLoginName(account.getLoginName());
        if (existing.isPresent()) {
            bindingResult.rejectValue("loginName", "error.account", "Username đã tồn tại");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Gán quyền mặc định STUDENT
        Role studentRole = roleRepository.findByName("STUDENT")
            .orElseGet(() -> roleRepository.save(new Role(null, "STUDENT")));

        account.setPassword(passwordEncoder.encode(account.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(studentRole);
        account.setRoles(roles);

        accountRepository.save(account);

        return "redirect:/login";
    }
}
