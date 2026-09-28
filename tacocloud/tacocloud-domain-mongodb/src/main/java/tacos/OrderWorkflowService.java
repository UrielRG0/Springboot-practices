package tacos;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tacos.OrderAuditLog;
import tacos.OrderStatus;
import tacos.TacoOrder;
import tacos.User;

@Service
public class OrderWorkflowService {

    public TacoOrder transitionStatus(TacoOrder order, OrderStatus newStatus, User actor, String reason) {
        OrderStatus current = order.getStatus();
        
        if (current == newStatus) {
            return order;
        }
        boolean isAdmin = actor.getRole() != null && actor.getRole().contains("ADMIN");
        boolean isOwner = order.getUser() != null && order.getUser().getId().equals(actor.getId());

        if (newStatus == OrderStatus.CANCELLED) {
            if (!isOwner && !isAdmin) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you cant cancel this order");
            if (current == OrderStatus.PREPARING || current == OrderStatus.READY || current == OrderStatus.OUT_FOR_DELIVERY || current == OrderStatus.DELIVERED) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Too late to cancell, the order is alredy been prepared");
            }
        } else {
            if (!isAdmin) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only the personal can be forward the order");
            boolean validTransition = false;
            switch (current) {
                case CREATED: if (newStatus == OrderStatus.ACCEPTED) validTransition = true; break;
                case ACCEPTED: if (newStatus == OrderStatus.PREPARING) validTransition = true; break;
                case PREPARING: if (newStatus == OrderStatus.READY) validTransition = true; break;
                case READY: if (newStatus == OrderStatus.OUT_FOR_DELIVERY) validTransition = true; break;
                case OUT_FOR_DELIVERY: if (newStatus == OrderStatus.DELIVERED) validTransition = true; break;
                default: break;
            }
            if (!validTransition) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid transicion of" + current + " a " + newStatus);
            }
        }
        order.setStatus(newStatus);
        order.getStatusHistory().add(new OrderAuditLog(current, newStatus, actor.getUsername(), reason));
        
        return order;
    }
}