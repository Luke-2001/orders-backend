package com.ms.pedidos.listener;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ms.pedidos.domain.PedidoDto;
import com.ms.pedidos.domain.StatusPedidoDto;
import com.ms.pedidos.domain.StatusPedidoEnum;
import com.ms.pedidos.service.PedidoMessagingService;
import com.ms.pedidos.service.StatusService;
import com.rabbitmq.client.Channel;

@Component
public class PedidoListener {

    private final PedidoMessagingService pedidoMessagingService;

    private final StatusService statusService;

    private final Random random = new Random();

    public PedidoListener(PedidoMessagingService pedidoMessagingService, StatusService statusService) {

        this.pedidoMessagingService = pedidoMessagingService;
        this.statusService = statusService;
    }

    @RabbitListener(queues = "${rabbit.queue.entrada:pedidos.entrada.lucas}")
    public void receive(PedidoDto pedidoDto, Message message, Channel channel) throws Exception {

        statusService.setStatus(pedidoDto.getId(), "PROCESSANDO");
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {

            System.out.println("Iniciando processamento pedido: " + pedidoDto.getId());
            Thread.sleep(1000 + random.nextInt(2000));

            double n = random.nextDouble();

            if (n < 0.2) {
                throw new RuntimeException("Falha simulada no processamento do pedido");
            }

            StatusPedidoDto statusPedidoDto = new StatusPedidoDto();
            statusPedidoDto.setIdPedido(pedidoDto.getId());
            statusPedidoDto.setStatus(StatusPedidoEnum.SUCESSO);
            statusPedidoDto.setDataProcessamento(LocalDateTime.now());

            pedidoMessagingService.publicarStatusSucesso(statusPedidoDto);
            statusService.setStatus(pedidoDto.getId(), "SUCESSO");

            channel.basicAck(deliveryTag, false);
            System.out.println("Processamento SUCESSO pedido: " + pedidoDto.getId());

        } catch (Exception ex) {

            System.err.println("Processamento FALHOU pedido: " + pedidoDto.getId() + " -> " + ex.getMessage());

            StatusPedidoDto statusPedidoDto = new StatusPedidoDto();
            statusPedidoDto.setIdPedido(pedidoDto.getId());
            statusPedidoDto.setStatus(StatusPedidoEnum.FALHA);
            statusPedidoDto.setMensagemErro(ex.getMessage());
            statusPedidoDto.setDataProcessamento(LocalDateTime.now());

            pedidoMessagingService.publicarStatusFalha(statusPedidoDto);
            statusService.setStatus(pedidoDto.getId(), "FALHA");

            channel.basicReject(deliveryTag, false);
        }
    }
}
