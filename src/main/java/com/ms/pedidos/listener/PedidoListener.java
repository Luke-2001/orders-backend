package com.ms.pedidos.listener;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ms.pedidos.domain.PedidoDto;
import com.ms.pedidos.domain.StatusPedidoDto;
import com.ms.pedidos.domain.StatusPedidoEnum;
import com.ms.pedidos.service.PedidoPublisher;
import com.ms.pedidos.service.StatusService;
import com.rabbitmq.client.Channel;

@Component
public class PedidoListener {

    private final PedidoPublisher publisher;

    private final StatusService statusService;

    private final Random random = new Random();

    public PedidoListener(PedidoPublisher publisher, StatusService statusService) {

        this.publisher = publisher;
        this.statusService = statusService;
    }

    @RabbitListener(queues = "${rabbit.queue.entrada:pedidos.entrada.lucas}")
    public void receive(PedidoDto pedido, Message message, Channel channel) throws Exception {

        statusService.setStatus(pedido.getId(), "PROCESSANDO");
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {

            System.out.println("Iniciando processamento pedido: " + pedido.getId());
            Thread.sleep(1000 + random.nextInt(2000));

            double n = random.nextDouble();

            if (n < 0.2) {
                throw new RuntimeException("ExcecaoDeProcessamento simulada");
            }

            StatusPedidoDto status = new StatusPedidoDto();
            status.setIdPedido(pedido.getId());
            status.setStatus(StatusPedidoEnum.SUCESSO);
            status.setDataProcessamento(LocalDateTime.now());

            publisher.publicarStatusSucesso(status);
            statusService.setStatus(pedido.getId(), "SUCESSO");

            channel.basicAck(deliveryTag, false);
            System.out.println("Processamento SUCESSO pedido: " + pedido.getId());

        } catch (Exception ex) {

            System.err.println("Processamento FALHOU pedido: " + pedido.getId() + " -> " + ex.getMessage());

            StatusPedidoDto status = new StatusPedidoDto();
            status.setIdPedido(pedido.getId());
            status.setStatus(StatusPedidoEnum.FALHA);
            status.setMensagemErro(ex.getMessage());
            status.setDataProcessamento(LocalDateTime.now());

            publisher.publicarStatusFalha(status);
            statusService.setStatus(pedido.getId(), "FALHA");

            channel.basicReject(deliveryTag, false);
        }
    }
}
