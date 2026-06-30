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
import org.springframework.web.bind.annotation.RestController;

import com.example.booksyspa.assemblers.ClienteAssembler;
import com.example.booksyspa.model.Cliente;
import com.example.booksyspa.service.ClienteService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v2/clientes")
@CrossOrigin("*")
public class ClienteController {

    private final ClienteService clienteService;
    private final ClienteAssembler clienteAssembler;

    public ClienteController(ClienteService clienteService, ClienteAssembler clienteAssembler) {
        this.clienteService = clienteService;
        this.clienteAssembler = clienteAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Cliente>> getAllClientes() {
        log.info("GET /api/v2/clientes");
        List<EntityModel<Cliente>> clientes = clienteService.getClientes().stream()
                .map(clienteAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(clientes);
    }

    @GetMapping(value = "/{id}")
    public EntityModel<Cliente> getClienteById(@PathVariable int id) {
        log.info("GET /api/v2/clientes/{}", id);
        return clienteAssembler.toModel(clienteService.getClienteId(id));
    }

    @GetMapping(value = "/rut/{rut}")
    public EntityModel<Cliente> getClienteByRut(@PathVariable String rut) {
        log.info("GET /api/v2/clientes/rut/{}", rut);
        return clienteAssembler.toModel(clienteService.getClientePorRut(rut));
    }

    @PostMapping
    public ResponseEntity<EntityModel<Cliente>> createCliente(@Valid @RequestBody Cliente cliente) {
        log.info("POST /api/v2/clientes");
        return new ResponseEntity<>(clienteAssembler.toModel(clienteService.saveCliente(cliente)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Cliente>> updateCliente(@PathVariable int id, @Valid @RequestBody Cliente cliente) {
        log.info("PUT /api/v2/clientes/{}", id);
        return ResponseEntity.ok(clienteAssembler.toModel(clienteService.updateCliente(id, cliente)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable int id) {
        log.info("DELETE /api/v2/clientes/{}", id);
        clienteService.deleteCliente(id);
        return ResponseEntity.noContent().build();
    }
}
