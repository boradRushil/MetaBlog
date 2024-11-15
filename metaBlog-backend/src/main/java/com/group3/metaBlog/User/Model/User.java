package com.group3.metaBlog.User.Model;

import com.group3.metaBlog.Blog.Model.Blog;
import com.group3.metaBlog.Enum.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 4, message = "Minimum username length: 4 characters")
    private String username;

    @Getter
    @Column(unique = true, nullable = false)
    private String email;

    @Column(columnDefinition = "boolean default false")
    private Boolean isEmailVerified = false;

    @Column(nullable = false)
    private String password;

    @Column(length = 4000, unique = true)
    private String accessToken;

    @Column(length = 4000, unique = true)
    private String refreshToken;

    private Double lastLoginTime;

    private Double registerAt;

    private String imageURL;

    @Column(length = 5000)
    private String bio;

    private String githubURL;

    private String linkedinURL;

    @Column(columnDefinition = "boolean default false")
    private Boolean isTermsAccepted = false;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(columnDefinition = "boolean default false")
    private Boolean isResetPasswordRequested = false;

    @Column(columnDefinition = "boolean default false")
    private Boolean isAccountLocked = false;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Blog> blogs;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_saved_blogs",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "blog_id")
    )
    private List<Blog> savedBlogs;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isAccountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
