package com.example.booksyspa.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.booksyspa.assemblers.PedidoAssembler;
import com.example.booksyspa.model.Pedido;
import com.example.booksyspa.service.PedidoService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v2/pedidos")
@CrossOrigin("*")
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoAssembler pedidoAssembler;

    public PedidoController(PedidoService pedidoService, PedidoAssembler pedidoAssembler) {
        this.pedidoService = pedidoService;
        this.pedidoAssembler = pedidoAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Pedido>> getAllPedidos() {
        log.info("GET /api/v2/pedidos");
        List<EntityModel<Pedido>> pedidos = pedidoService.getPedidos().stream()
                .map(pedidoAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(pedidos);
    }

    @GetMapping("/{id}")
    public EntityModel<Pedido> getPedidoById(@PathVariable int id) {
        log.info("GET /api/v2/pedidos/{}", id);
        return pedidoAssembler.toModel(pedidoService.getPedidoId(id));
    }

    @GetMapping(value = "/cliente/{idCliente}")
    public CollectionModel<EntityModel<Pedido>> getPedidosByCliente(@PathVariable int idCliente) {
        log.info("GET /api/v2/pedidos/cliente/{}", idCliente);
        List<EntityModel<Pedido>> pedidos = pedidoService.getPedidosPorCliente(idCliente).stream()
                .map(pedidoAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(pedidos);
    }

    @GetMapping(value = "/libro/{idLibro}")
    public CollectionModel<EntityModel<Pedido>> getPedidosByLibro(@PathVariable int idLibro) {
        log.info("GET /api/v2/pedidos/libro/{}", idLibro);
        List<EntityModel<Pedido>> pedidos = pedidoService.getPedidosPorLibro(idLibro).stream()
                .map(pedidoAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(pedidos);
    }

    @GetMapping(value = "/estado")
    public CollectionModel<EntityModel<Pedido>> getPedidosByEstado(@RequestParam String estado) {
        log.info("GET /api/v2/pedidos/estado?estado={}", estado);
        List<EntityModel<Pedido>> pedidos = pedidoService.getPedidosPorEstado(estado).stream()
                .map(pedidoAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(pedidos);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Pedido>> createPedido(@Valid @RequestBody Pedido pedido) {
        log.info("POST /api/v2/pedidos");
        return new ResponseEntity<>(pedidoAssembler.toModel(pedidoService.savePedido(pedido)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Pedido>> updatePedido(@PathVariable int id, @Valid @RequestBody Pedido pedido) {
        log.info("PUT /api/v2/pedidos/{}", id);
        return ResponseEntity.ok(pedidoAssembler.toModel(pedidoService.updatePedido(id, pedido)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable int id) {
        log.info("DELETE /api/v2/pedidos/{}", id);
        pedidoService.deletePedido(id);
        return ResponseEntity.noContent().build();
    }
}
