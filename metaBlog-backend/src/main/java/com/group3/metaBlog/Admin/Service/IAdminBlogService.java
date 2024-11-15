package com.group3.metaBlog.Admin.Service;

import com.group3.metaBlog.Admin.DTO.AdminRequestDto;
import com.group3.metaBlog.Admin.DTO.RegisterAdminDto;
import org.springframework.http.ResponseEntity;

public interface IAdminBlogService {
    ResponseEntity<Object> getPendingBlogs();
    ResponseEntity<Object> getApprovedBlogs();
    ResponseEntity<Object> getRejectedBlogs();
    ResponseEntity<Object> updateBlogStatus(AdminRequestDto requestDto);
    ResponseEntity<Object> registerAdmin(RegisterAdminDto requestDto);
}
