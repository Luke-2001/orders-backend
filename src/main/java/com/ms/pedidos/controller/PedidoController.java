package com.ms.pedidos.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ms.pedidos.domain.PedidoDto;
import com.ms.pedidos.service.PedidoMessagingService;
import com.ms.pedidos.service.StatusService;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoMessagingService pedidoMessagingService;

    private final StatusService statusService;

    public PedidoController(PedidoMessagingService pedidoMessagingService, StatusService statusService) {

        this.pedidoMessagingService = pedidoMessagingService;
        this.statusService = statusService;
    }

    @PostMapping
    public ResponseEntity<?> criarPedido(@RequestBody PedidoDto pedidoDto) {

        if (pedidoDto.getProduto() == null || pedidoDto.getProduto().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("produto vazio");
        }

        if (pedidoDto.getQuantidade() <= 0) {
            return ResponseEntity.badRequest().body("quantidade deve ser maior que zero");
        }

        if (pedidoDto.getId() == null) {
            pedidoDto.setId(UUID.randomUUID());
        }

        if (pedidoDto.getDataCriacao() == null) {
            pedidoDto.setDataCriacao(LocalDateTime.now());
        }

        statusService.setStatus(pedidoDto.getId(), "ENVIADO");
        pedidoMessagingService.publicarPedido(pedidoDto);

        return ResponseEntity.accepted().location(URI.create("/api/pedidos/status/" + pedidoDto.getId()))
                .body(pedidoDto.getId());
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<?> status(@PathVariable("id") UUID id) {

        String s = statusService.getStatus(id);
        return ResponseEntity.ok(s);
    }
}
