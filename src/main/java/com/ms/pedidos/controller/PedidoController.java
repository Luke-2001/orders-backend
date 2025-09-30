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
import com.ms.pedidos.service.PedidoPublisher;
import com.ms.pedidos.service.StatusService;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoPublisher publisher;

    private final StatusService statusService;

    public PedidoController(PedidoPublisher publisher, StatusService statusService) {

        this.publisher = publisher;
        this.statusService = statusService;
    }

    @PostMapping
    public ResponseEntity<?> criarPedido(@RequestBody PedidoDto pedido) {

        if (pedido.getProduto() == null || pedido.getProduto().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("produto vazio");
        }
        if (pedido.getQuantidade() <= 0) {
            return ResponseEntity.badRequest().body("quantidade deve ser maior que zero");
        }
        if (pedido.getId() == null) {
            pedido.setId(UUID.randomUUID());
        }
        if (pedido.getDataCriacao() == null) {
            pedido.setDataCriacao(LocalDateTime.now());
        }

        statusService.setStatus(pedido.getId(), "ENVIADO");
        publisher.publicarPedido(pedido);
        return ResponseEntity.accepted().location(URI.create("/api/pedidos/status/" + pedido.getId()))
                .body(pedido.getId());
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<?> status(@PathVariable("id") UUID id) {

        String s = statusService.getStatus(id);
        return ResponseEntity.ok(s);
    }
}
