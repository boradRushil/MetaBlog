package com.group3.metaBlog.Admin.Service;

import com.group3.metaBlog.Admin.DTO.AdminResponseDto;
import com.group3.metaBlog.Admin.DTO.AdminRequestDto;
import com.group3.metaBlog.Admin.DTO.RegisterAdminDto;
import com.group3.metaBlog.Config.ApplicationConfig;
import com.group3.metaBlog.Email.Service.IEmailService;
import com.group3.metaBlog.Enum.BlogStatus;
import com.group3.metaBlog.Admin.Repository.IAdminBlogRepository;
import com.group3.metaBlog.Blog.Model.Blog;
import com.group3.metaBlog.OTP.Service.IOTPService;
import com.group3.metaBlog.User.Model.User;
import com.group3.metaBlog.User.Repository.IUserRepository;
import com.group3.metaBlog.Utils.MetaBlogResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.group3.metaBlog.Enum.Role;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminBlogService implements IAdminBlogService {

    private static final Logger logger = LoggerFactory.getLogger(AdminBlogService.class);
    private final IAdminBlogRepository adminBlogRepository;
    private final IUserRepository userRepository;
    private final ApplicationConfig applicationConfig;
    private final IOTPService otpService;
    private final IEmailService emailService;

    @Override
    public ResponseEntity<Object> getPendingBlogs() {
        try {
            List<Blog> blogs = adminBlogRepository.findByStatus(BlogStatus.PENDING);
            List<AdminResponseDto> responseDTO = blogs.stream().map(blog -> AdminResponseDto.builder()
                    .id(blog.getId())
                    .title(blog.getTitle())
                    .content(blog.getContent())
                    .imageUrl(blog.getImageUrl())
                    .author(blog.getAuthor().getUsername())
                    .createdOn(blog.getCreatedOn())
                    .status(blog.getStatus().name())
                    .build()).toList();
            return ResponseEntity.ok(MetaBlogResponse.builder()
                    .success(true)
                    .message("Pending blogs retrieved successfully")
                    .data(responseDTO)
                    .build());
        } catch (Exception e) {
            logger.error("Error retrieving pending blogs: {}", e.getMessage());
            return new ResponseEntity<>(MetaBlogResponse.builder()
                    .success(false)
                    .message("Error retrieving pending blogs")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> getApprovedBlogs() {
        try {
            List<Blog> blogs = adminBlogRepository.findByStatus(BlogStatus.APPROVED);
            List<AdminResponseDto> responseDTO = blogs.stream().map(blog -> AdminResponseDto.builder()
                    .id(blog.getId())
                    .title(blog.getTitle())
                    .content(blog.getContent())
                    .imageUrl(blog.getImageUrl())
                    .author(blog.getAuthor().getUsername())
                    .createdOn(blog.getCreatedOn())
                    .status(blog.getStatus().name())
                    .build()).toList();
            return ResponseEntity.ok(MetaBlogResponse.builder()
                    .success(true)
                    .message("Approved blogs retrieved successfully")
                    .data(responseDTO)
                    .build());
        } catch (Exception e) {
            logger.error("Error retrieving approved blogs: {}", e.getMessage());
            return new ResponseEntity<>(MetaBlogResponse.builder()
                    .success(false)
                    .message("Error retrieving approved blogs")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> getRejectedBlogs() {
        try {
            List<Blog> blogs = adminBlogRepository.findByStatus(BlogStatus.REJECTED);
            List<AdminResponseDto> responseDTO = blogs.stream().map(blog -> AdminResponseDto.builder()
                    .id(blog.getId())
                    .title(blog.getTitle())
                    .content(blog.getContent())
                    .imageUrl(blog.getImageUrl())
                    .author(blog.getAuthor().getUsername())
                    .createdOn(blog.getCreatedOn())
                    .status(blog.getStatus().name())
                    .build()).toList();
            return ResponseEntity.ok(MetaBlogResponse.builder()
                    .success(true)
                    .message("Rejected blogs retrieved successfully")
                    .data(responseDTO)
                    .build());
        } catch (Exception e) {
            logger.error("Error retrieving rejected blogs: {}", e.getMessage());
            return new ResponseEntity<>(MetaBlogResponse.builder()
                    .success(false)
                    .message("Error retrieving rejected blogs")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> updateBlogStatus(AdminRequestDto requestDto) {
        try {
            Blog blog = adminBlogRepository.findById(requestDto.getBlogId())
                    .orElseThrow(() -> new RuntimeException("Blog not found"));

            String APPROVED_STATUS = "APPROVED";
            String REJECTED_STATUS = "REJECTED";
            if (APPROVED_STATUS.trim().equalsIgnoreCase(requestDto.getStatus())) {
                blog.setStatus(BlogStatus.APPROVED);
            } else if (REJECTED_STATUS.trim().equalsIgnoreCase(requestDto.getStatus())) {
                blog.setStatus(BlogStatus.REJECTED);
            } else {
                return new ResponseEntity<>(MetaBlogResponse.builder()
                        .success(false)
                        .message("Invalid status")
                        .build(), HttpStatus.BAD_REQUEST);
            }

            blog.setReviewedOn((double) System.currentTimeMillis());
            adminBlogRepository.save(blog);

            return ResponseEntity.ok(MetaBlogResponse.builder()
                    .success(true)
                    .message("Blog status updated successfully")
                    .data(null)
                    .build());
        } catch (Exception e) {
            logger.error("Error updating blog status: {}", e.getMessage());
            return new ResponseEntity<>(MetaBlogResponse.builder()
                    .success(false)
                    .message("Error updating blog status")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> registerAdmin(RegisterAdminDto request) {
        try {
            Optional<User> user = userRepository.findByEmail(request.getEmail());
            if(user.isPresent())
            {
                return new ResponseEntity<>(MetaBlogResponse.builder()
                        .success(false)
                        .message("User already exists")
                        .build(), HttpStatus.BAD_REQUEST);
            }
            Optional<User> userByUserName = userRepository.findByUsername(request.getUsername());
            if(userByUserName.isPresent())
            {
                return new ResponseEntity<>(MetaBlogResponse.builder()
                        .success(false)
                        .message("Username already exists")
                        .build(), HttpStatus.BAD_REQUEST);
            }
            User admin = User.builder()
                    .email(request.getEmail())
                    .username(request.getUsername())
                    .password(applicationConfig.passwordEncoder().encode(request.getPassword()))
                    .role(Role.Admin)
                    .registerAt((double) (System.currentTimeMillis()))
                    .lastLoginTime((double) (System.currentTimeMillis()))
                    .isEmailVerified(false)
                    .isAccountLocked(false)
                    .isResetPasswordRequested(false)
                    .isTermsAccepted(true)
                    .build();

            userRepository.save(admin);
            Long admin_id = admin.getId();

            int otp = otpService.generateOTP();
            otpService.registerOTP(otp, admin_id);

            try {
                emailService.sendVerificationOTP(request.getEmail(), otp);
            } catch (MessagingException e) {
                logger.error("Error sending email to the admin with email: {}", request.getEmail());
                logger.error("Message of the error: {}", e.getMessage());
                return new ResponseEntity<>(MetaBlogResponse.builder()
                        .success(false)
                        .message("Error sending email to the admin.")
                        .build(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            logger.info("OTP sent to the admin with email ");
            userRepository.save(admin);

            return ResponseEntity.ok(MetaBlogResponse.builder()
                    .success(true)
                    .message("Admin registered successfully")
                    .data(null)
                    .build());
        } catch (Exception e) {
            logger.error("Error registering admin: {}", e.getMessage());
            return new ResponseEntity<>(MetaBlogResponse.builder()
                    .success(false)
                    .message("Error registering admin")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
