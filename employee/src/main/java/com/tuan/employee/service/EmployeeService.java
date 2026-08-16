package com.tuan.employee.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tuan.employee.dto.request.EmployeeCreateDTO;
import com.tuan.employee.model.Employee;
import com.tuan.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final Cloudinary cloudinary;

    /**
     * Tạo mới nhân viên.
     * - Upload ảnh lên Cloudinary (nếu có).
     * - Lưu thông tin nhân viên vào DB.
     */
    @Transactional
    public Employee createEmployee(EmployeeCreateDTO dto) {
        String avatarUrl = null;

        MultipartFile file = dto.getAvatarFile();
        if (file != null && !file.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

                avatarUrl = (String) uploadResult.get("secure_url");
                log.info("Upload ảnh thành công: {}", avatarUrl);
            } catch (IOException e) {
                log.error("Upload ảnh thất bại", e);
                throw new RuntimeException("Không thể upload ảnh: " + e.getMessage());
            }
        }

        Employee employee = Employee.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .department(dto.getDepartment())
                .avatarUrl(avatarUrl)
                .build();

        return employeeRepository.save(employee);
    }
}
