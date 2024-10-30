package payroll.controller;

import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.entity.Order;
import payroll.exception.OrderNotFoundException;
import payroll.service.OrderService;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public CollectionModel<EntityModel<Order>> getAllOrder() {
        List<EntityModel<Order>> orders = orderService.getAllOrders();

        return CollectionModel.of(orders,
                linkTo(methodOn(OrderController.class).getAllOrder()).withSelfRel());
    }

    @GetMapping("/orders/{id}")
    public EntityModel<Order> getOrderById(@PathVariable Long id) {
        return orderService.findOrderById(id);
    }

    @PostMapping("/orders")
    public ResponseEntity<EntityModel<Order>> createNewOrder(@RequestBody Order order) {
        EntityModel<Order> newOrder = orderService.createNewOrder(order);
        return ResponseEntity.created(linkTo(methodOn(OrderController.class)
                                     .getOrderById(newOrder.getContent()
                                                           .getId())).toUri())
                             .body(newOrder);
    }

    @DeleteMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            EntityModel<Order> orderCancel = orderService.cancelOrder(id);
            return ResponseEntity.ok(orderCancel);
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                                 .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                                 .body(Problem.create()
                                              .withTitle("Method not allowed")
                                              .withDetail(e.getMessage()));
        }
    }

    @PutMapping("/orders/{id}/complete")
    public ResponseEntity<?> complete(@PathVariable Long id) {
        try {
            EntityModel<Order> orderCancel = orderService.completeOrder(id);
            return ResponseEntity.ok(orderCancel);
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                                 .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                                 .body(Problem.create()
                                              .withTitle("Method not allowed")
                                              .withDetail(e.getMessage()));
        }
    }
}
