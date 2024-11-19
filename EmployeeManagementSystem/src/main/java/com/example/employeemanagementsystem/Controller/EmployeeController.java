package com.example.employeemanagementsystem.Controller;

import com.example.employeemanagementsystem.ApiResponse.Response;
import com.example.employeemanagementsystem.Model.Employee;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/employee/management")
public class EmployeeController {

    ArrayList<Employee> employees = new ArrayList<>();

// Get all employees: Retrieves a list of all employees.
    @GetMapping("/get")
    public ResponseEntity<?> getEmployee(){
        return ResponseEntity.ok(employees);
    }

    //2. Add a new employee: Adds a new employee to the system.
    @PostMapping("/add")
    public ResponseEntity<?> addEmployee(@RequestBody @Valid Employee employee, Errors errors){
        if(errors.hasErrors()){
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }
        employees.add(employee);
        return ResponseEntity.status(201).body(new Response("Employee added Successfully"));
    }
// 201 :

    // method i used to help me check if the id exists

    private Employee findEmployeeById(String id) {
        for (Employee employee : employees) {
            if (employee.getId().equals(id)) {
                return employee;
            }
        }
        return null;
    }

//3. Update an employee: Updates an existing employee's information.
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable String id, @RequestBody @Valid Employee employee, Errors errors) {
        Employee existingEmployee = findEmployeeById(id);
        if (existingEmployee == null) {
            return ResponseEntity.status(404).body(new Response("Employee with the given ID does not exist"));
        }
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }
        employees.set(employees.indexOf(existingEmployee), employee);
        return ResponseEntity.status(200).body(new Response("Employee updated successfully"));
    }


//4. Delete an employee: Deletes an employee from the system.
//▪ Verify that the employee exists.
@DeleteMapping("/delete/{id}")
public ResponseEntity<?> deleteEmployee(@PathVariable String id) {
    Employee employee = findEmployeeById(id);
    if (employee == null) {
        return ResponseEntity.status(404).body(new Response("Employee with the given ID does not exist"));
    }
    employees.remove(employee);
    return ResponseEntity.status(200).body(new Response("Employee deleted successfully"));
}

//5. Search Employees by Position: Retrieves a list of employees based on their
//position (supervisor or coordinator).
//Note:
//▪ Ensure that the position parameter is valid (either "supervisor" or "coordinator").
    @GetMapping("/search/by/position")
 public ResponseEntity<?> searchEmployeeByPosition(@RequestParam @Pattern(regexp = "^supervisor|coordinator$", message = "Position must be either supervisor or coordinator only") String position){
        ArrayList<Employee> employeesByPosition = new ArrayList<>();
        for(Employee employee : employees){
            if(employee.getPosition().equals(position)){
                employeesByPosition.add(employee);
            }
        }
        if (employeesByPosition.isEmpty()) {
            return ResponseEntity.status(404).body(new Response("No employees found with the given position"));
        }
        return ResponseEntity.status(200).body(employeesByPosition);
}

// 6. Get Employees by Age Range: Retrieves a list of employees within a specified age range.
//Note:
//▪ Ensure that minAge and maxAge are valid age values.
@GetMapping("/get/employee/age")
public ResponseEntity<?> getEmployeesByAge(@RequestParam @Min(26) @Max(65) int minAge, @RequestParam @Min(26) @Max(65) int maxAge) {
    ArrayList<Employee> employeesByAge = new ArrayList<>();
    for (Employee employee : employees) {
        if (employee.getAge() >= minAge && employee.getAge() <= maxAge) {
            employeesByAge.add(employee);
        }
    }
    if (employeesByAge.isEmpty()) {
        return ResponseEntity.status(404).body(new Response("No employees found in the given age range"));
    }
    return ResponseEntity.status(200).body(employeesByAge);
}

    //7. Apply for annual leave: Allow employees to apply for annual leave.
//Note:
//▪ Verify that the employee exists.
//▪ The employee must not be on leave (the onLeave flag must be false).
//▪ The employee must have at least one day of annual leave remaining.
//▪ Behavior:
//▪ Set the onLeave flag to true.
//▪ Reduce the annualLeave by 1.
@PutMapping("/annual/leave/{id}")
public ResponseEntity<?> annualLeave(@PathVariable String id) {
     Employee employee = findEmployeeById(id);
        if (employee == null) {
            return ResponseEntity.status(404).body(new Response("Employee with the given ID does not exist"));
        }
        if (employee.isOnLeave()) {
            return ResponseEntity.status(400).body(new Response("Employee is already on leave"));
        }
        if (employee.getAnnualLeave() > 0) {
            employee.setOnLeave(true);
            employee.setAnnualLeave(employee.getAnnualLeave() - 1);
            return ResponseEntity.status(200).body(new Response("Annual leave applied successfully"));
        } else {
            return ResponseEntity.status(400).body(new Response("Employee does not have enough annual leave"));
        }
    }

//8. Get Employees with No Annual Leave: Retrieves a list of employees who have used up all their annual leave.
@GetMapping("/get/no/annual/leave")
public ResponseEntity<?> getEmployeesNoAnnualLeave(){
 ArrayList<Employee> employeesWithNoAnnulLeave = new ArrayList<>();
for(Employee employee : employees){
    if (employee.getAnnualLeave() == 0){
        employeesWithNoAnnulLeave.add(employee);
    }
}
if (employeesWithNoAnnulLeave.isEmpty()) {
         return ResponseEntity.status(404).body(new Response("No employees with No Annal leave found"));
     }
     return ResponseEntity.status(200).body(employeesWithNoAnnulLeave);
}

//9. Promote Employee: Allows a supervisor to promote an employee to the position of supervisor if they meet certain criteria. Note:
//▪ Verify that the employee with the specified ID exists.
//▪ Ensure that the requester (user making the request) is a supervisor.
//▪ Validate that the employee's age is at least 30 years.
//▪ Confirm that the employee is not currently on leave.
//▪ Change the employee's position to "supervisor" if they meet the criteria.
@PutMapping("/promote/{supervisorID}")
public ResponseEntity<?> promoteEmployee(@PathVariable String supervisorID, @RequestParam String id) {
       Employee supervisor = findEmployeeById(supervisorID);
        if (supervisor == null || !supervisor.getPosition().equals("supervisor")) {
            return ResponseEntity.status(400).body(new Response("Only a supervisor can promote an employee"));
        }
        Employee employeeToPromote = findEmployeeById(id);
        if (employeeToPromote == null) {
            return ResponseEntity.status(404).body(new Response("Employee with the given ID does not exist"));
        }
        if (employeeToPromote.getPosition().equals("supervisor")) {
            return ResponseEntity.status(400).body(new Response("Employee is already a supervisor"));
        }
        if (employeeToPromote.getAge() >= 30 && !employeeToPromote.isOnLeave()) {
            employeeToPromote.setPosition("supervisor");
            return ResponseEntity.status(200).body(new Response("Employee promoted to supervisor"));
        } else {
            return ResponseEntity.status(400).body(new Response("Employee does not meet the promotion criteria"));
        }
    }



}
