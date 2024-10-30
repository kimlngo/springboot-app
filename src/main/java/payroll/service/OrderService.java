package payroll.service;

import lombok.AllArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import payroll.assembler.OrderModelAssembler;
import payroll.entity.Order;
import payroll.entity.Status;
import payroll.exception.OrderNotFoundException;
import payroll.repository.OrderRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderModelAssembler assembler;

    public List<EntityModel<Order>> getAllOrders() {
        return orderRepository.findAll()
                              .stream()
                              .map(assembler::toModel)
                              .collect(Collectors.toList());
    }

    public EntityModel<Order> findOrderById(Long id) {
        return orderRepository.findById(id)
                              .map(assembler::toModel)
                              .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public EntityModel<Order> createNewOrder(Order order) {
        order.setStatus(Status.IN_PROGRESS);
        return assembler.toModel(orderRepository.save(order));
    }

    public EntityModel<Order> cancelOrder(Long id) throws OrderNotFoundException, IllegalStateException {
        return updateOrderStatus(id, Status.CANCELLED);
    }

    public EntityModel<Order> completeOrder(Long id) throws OrderNotFoundException, IllegalStateException {
        return updateOrderStatus(id, Status.COMPLETED);
    }

    private EntityModel<Order> updateOrderStatus(Long id, Status status) throws OrderNotFoundException, IllegalStateException {
        Order order = orderRepository.findById(id)
                                     .orElseThrow(() -> new OrderNotFoundException(id));

        if (Status.IN_PROGRESS.equals(order.getStatus())) {
            order.setStatus(status);
            return assembler.toModel(orderRepository.save(order));
        }

        throw new IllegalStateException(
                String.format("You can't %s an order that is in the %s status",
                        order.getStatus().operation,
                        order.getStatus()));
    }
}
