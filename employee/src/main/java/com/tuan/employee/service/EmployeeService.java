package com.tuan.employee.service;

import com.cloudinary.Cloudinary;
import com.tuan.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepositoryl;
    private final Cloudinary cloudinary;


}
