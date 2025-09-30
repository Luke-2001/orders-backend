package com.ms.pedidos.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class StatusService {

    private final Map<UUID, String> statusMap = new ConcurrentHashMap<>();

    public void setStatus(UUID id, String status) {

        statusMap.put(id, status);
    }

    public String getStatus(UUID id) {

        return statusMap.getOrDefault(id, "DESCONHECIDO");
    }
}
