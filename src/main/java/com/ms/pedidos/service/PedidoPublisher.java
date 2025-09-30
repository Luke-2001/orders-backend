package com.ms.pedidos.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.ms.pedidos.config.RabbitConfig;
import com.ms.pedidos.domain.PedidoDto;
import com.ms.pedidos.domain.StatusPedidoDto;

@Service
public class PedidoPublisher {

    private final RabbitTemplate rabbitTemplate;

    private final StatusService statusService;

    public PedidoPublisher(RabbitTemplate rabbitTemplate, StatusService statusService) {

        this.rabbitTemplate = rabbitTemplate;
        this.statusService = statusService;
    }

    public void publicarPedido(PedidoDto pedido) {

        statusService.setStatus(pedido.getId(), "ENVIADO");
        rabbitTemplate.convertAndSend(RabbitConfig.ENTRADA, pedido);
    }

    public void publicarStatusSucesso(StatusPedidoDto status) {

        rabbitTemplate.convertAndSend(RabbitConfig.SUCESSO, status);
    }

    public void publicarStatusFalha(StatusPedidoDto status) {

        rabbitTemplate.convertAndSend(RabbitConfig.FALHA, status);
    }
}
