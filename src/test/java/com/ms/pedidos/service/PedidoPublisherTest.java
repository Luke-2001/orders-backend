package com.ms.pedidos.service;

import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.ms.pedidos.domain.PedidoDto;

class PedidoPublisherTest {

    @Test
    public void publicarPedido_deve_chamar_rabbitTemplate() {

        RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
        StatusService statusService = new StatusService();
        PedidoPublisher publisher = new PedidoPublisher(rabbitTemplate, statusService);

        PedidoDto pedido = new PedidoDto();
        pedido.setId(UUID.randomUUID());
        pedido.setProduto("Caneta");
        pedido.setQuantidade(10);

        publisher.publicarPedido(pedido);

        verify(rabbitTemplate).convertAndSend("pedidos.entrada.lucas", pedido);
    }
}
