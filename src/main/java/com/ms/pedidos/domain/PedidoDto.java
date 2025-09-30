package com.ms.pedidos.domain;

import java.io.Serializable;
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
public class PedidoDto implements Serializable {

    private UUID id;

    private String produto;

    private int quantidade;

    private LocalDateTime dataCriacao;

}

