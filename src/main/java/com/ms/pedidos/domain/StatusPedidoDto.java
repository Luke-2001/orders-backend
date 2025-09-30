package com.ms.pedidos.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusPedidoDto {

    private UUID idPedido;

    private StatusPedidoEnum status;

    private LocalDateTime dataProcessamento;

    private String mensagemErro;
}
