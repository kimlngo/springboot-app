package payroll.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.assembler.EmployeeModelAssembler;
import payroll.entity.Employee;
import payroll.exception.EmployeeNotFoundException;
import payroll.repository.EmployeeRepository;
import payroll.service.EmployeeService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class EmployeeController {

    private final EmployeeRepository repository;
    private EmployeeModelAssembler assembler;
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler, EmployeeService employeeService) {
        this.repository = repository;
        this.assembler = assembler;
        this.employeeService = employeeService;
    }

    @GetMapping("/employees")
    public CollectionModel<EntityModel<Employee>> getAllEmployees() {
        return CollectionModel.of(employeeService.getAllEmployees(),
                linkTo(methodOn(EmployeeController.class).getAllEmployees()).withSelfRel());
    }

    @GetMapping("/employees/{id}")
    public EntityModel<Employee> getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    @PostMapping("/employees")
    public ResponseEntity<?> createNewEmployee(@RequestBody Employee newEmployee) {
        EntityModel<Employee> entityModel = employeeService.createNewEmployee(newEmployee);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF)
                                    .toUri())
                .body(entityModel);
    }


    @PutMapping("/employees/{id}")
    public ResponseEntity<?> updateEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
        try {
            EntityModel<Employee> entityModel = employeeService.updateEmployee(newEmployee, id);
            return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                                 .body(entityModel);
        } catch (EmployeeNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.noContent().build();
        } catch (EmployeeNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}