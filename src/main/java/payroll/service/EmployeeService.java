package payroll.service;

import lombok.AllArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import payroll.assembler.EmployeeModelAssembler;
import payroll.entity.Employee;
import payroll.exception.EmployeeNotFoundException;
import payroll.repository.EmployeeRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeService {
    private final EmployeeRepository repository;
    private final EmployeeModelAssembler assembler;

    public List<EntityModel<Employee>> getAllEmployees() {
        return repository.findAll()
                         .stream()
                         .map(assembler::toModel)
                         .toList();
    }

    public EntityModel<Employee> getEmployeeById(Long id) {
        return repository.findById(id)
                         .map(assembler::toModel)
                         .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public EntityModel<Employee> createNewEmployee(Employee newEmployee) {
        return assembler.toModel(repository.save(newEmployee));
    }

    public EntityModel<Employee> updateEmployee(Employee employeeUpdate, Long id) {
        Employee updatedEmployee = repository.findById(id)
                                             .map(employee -> {
                                                 employee.setName(employeeUpdate.getName());
                                                 employee.setRole(employeeUpdate.getRole());
                                                 return repository.save(employee);
                                             })
                                             .orElseThrow(() -> new EmployeeNotFoundException(id));

        return assembler.toModel(updatedEmployee);
    }

    public void deleteEmployee(Long id) {
        Employee employee = repository.findById(id)
                                      .orElseThrow(() -> new EmployeeNotFoundException(id));

        repository.deleteById(employee.getId());
    }
}
