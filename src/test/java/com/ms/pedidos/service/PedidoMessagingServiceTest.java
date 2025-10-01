package com.ms.pedidos.service;

import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.ms.pedidos.domain.PedidoDto;

class PedidoMessagingServiceTest {

    @Test
    public void publicarPedido_deve_chamar_rabbitTemplate() {

        RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
        StatusService statusService = new StatusService();
        PedidoMessagingService pedidoMessagingService = new PedidoMessagingService(rabbitTemplate, statusService);

        PedidoDto pedidoDto = new PedidoDto();
        pedidoDto.setId(UUID.randomUUID());
        pedidoDto.setProduto("Caneta");
        pedidoDto.setQuantidade(10);

        pedidoMessagingService.publicarPedido(pedidoDto);

        verify(rabbitTemplate).convertAndSend("pedidos.entrada.lucas", pedidoDto);
    }
}
