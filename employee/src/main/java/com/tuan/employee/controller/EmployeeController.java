package com.tuan.employee.controller;

import com.tuan.employee.dto.request.EmployeeCreateDTO;
import com.tuan.employee.model.Employee;
import com.tuan.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * POST /api/v1/employees – Tạo nhân viên mới (upload ảnh).
     * Yêu cầu: Token hợp lệ (xác thực qua JwtFilter).
     * Dùng @ModelAttribute để nhận cả text và file từ form-data.
     */
    @PostMapping
    public ResponseEntity<Employee> createEmployee(
            @ModelAttribute EmployeeCreateDTO dto ) {
        Employee saved = employeeService.createEmployee(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

}
