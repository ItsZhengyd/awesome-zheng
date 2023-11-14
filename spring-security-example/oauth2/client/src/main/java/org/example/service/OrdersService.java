package org.example.service;

import org.example.entity.Orders;
import org.example.repository.OrdersRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdersService {

    private final OrdersRepository ordersRepository;

    public OrdersService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    public List<Orders> findAllByUserName(String userName){
        return ordersRepository.findAllByUserName(userName);
    }
}
