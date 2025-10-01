package com.ms.pedidos.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.ms.pedidos.config.RabbitConfig;
import com.ms.pedidos.domain.PedidoDto;
import com.ms.pedidos.domain.StatusPedidoDto;

@Service
public class PedidoMessagingService {

    private final RabbitTemplate rabbitTemplate;

    private final StatusService statusService;

    public PedidoMessagingService(RabbitTemplate rabbitTemplate, StatusService statusService) {

        this.rabbitTemplate = rabbitTemplate;
        this.statusService = statusService;
    }

    public void publicarPedido(PedidoDto pedidoDto) {

        statusService.setStatus(pedidoDto.getId(), "ENVIADO");
        rabbitTemplate.convertAndSend(RabbitConfig.ENTRADA, pedidoDto);
    }

    public void publicarStatusSucesso(StatusPedidoDto statusPedidoDto) {

        rabbitTemplate.convertAndSend(RabbitConfig.SUCESSO, statusPedidoDto);
    }

    public void publicarStatusFalha(StatusPedidoDto statusPedidoDto) {

        rabbitTemplate.convertAndSend(RabbitConfig.FALHA, statusPedidoDto);
    }
}
