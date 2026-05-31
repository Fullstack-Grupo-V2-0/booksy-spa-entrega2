package com.example.booksyspa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booksyspa.exception.ResourceNotFoundException;
import com.example.booksyspa.model.Cliente;
import com.example.booksyspa.repository.ClienteRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> getClientes() {
        log.info("Consultando todos los clientes");
        return clienteRepository.findAll();
    }

    public Cliente saveCliente(Cliente cliente) {

        log.info("Intentando guardar cliente con rut={}", cliente.getRut());

        if (clienteRepository.findByRut(cliente.getRut()).isPresent()) {
            log.warn("Intento de registrar cliente con rut duplicado={}", cliente.getRut());
            throw new RuntimeException("Ya existe un cliente con el rut " + cliente.getRut());
        }

        Cliente clienteGuardado = clienteRepository.save(cliente);

        log.info("Cliente guardado correctamente con id={}", clienteGuardado.getIdCliente());

        return clienteGuardado;
    }

    public Cliente getClienteId(int id) {

        log.info("Buscando cliente con id={}", id);

        return clienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontró cliente con id={}", id);
                    return new ResourceNotFoundException("El cliente con id " + id + " no existe");
                });
    }

    public Cliente getClientePorRut(String rut) {

        log.info("Buscando cliente con rut={}", rut);

        return clienteRepository.findByRut(rut)
                .orElseThrow(() -> {
                    log.warn("No se encontró cliente con rut={}", rut);
                    return new ResourceNotFoundException("El cliente con rut " + rut + " no existe");
                });
    }

    @Transactional
    public Cliente updateCliente(int id, Cliente clienteActualizado) {

        log.info("Actualizando cliente con id={}", id);

        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontró cliente con id={} para actualizar", id);
                    return new ResourceNotFoundException("El cliente con id " + id + " no existe");
                });

        log.debug("Cliente encontrado para actualización: {}", clienteExistente);

        // Validar que el RUT no se duplique si se intenta cambiar
        if (clienteActualizado.getRut() != null
                && !clienteActualizado.getRut().equals(clienteExistente.getRut())) {

            log.info("Validando cambio de rut para cliente id={}", id);

            clienteRepository.findByRut(clienteActualizado.getRut()).ifPresent(c -> {
                log.warn("Intento de actualizar cliente id={} con rut duplicado={}",
                        id, clienteActualizado.getRut());

                throw new RuntimeException(
                        "El RUT " + clienteActualizado.getRut() + " ya está en uso por otro cliente.");
            });

            clienteExistente.setRut(clienteActualizado.getRut());
        }

        // Actualizar otros campos de forma selectiva
        Optional.ofNullable(clienteActualizado.getNombre()).ifPresent(clienteExistente::setNombre);
        Optional.ofNullable(clienteActualizado.getApellido()).ifPresent(clienteExistente::setApellido);
        Optional.ofNullable(clienteActualizado.getEmail()).ifPresent(clienteExistente::setEmail);
        Optional.ofNullable(clienteActualizado.getTelefono()).ifPresent(clienteExistente::setTelefono);

        log.info("Persistiendo cambios del cliente id={}", id);

        Cliente clienteGuardado = clienteRepository.save(clienteExistente);

        log.info("Cliente id={} actualizado correctamente", id);

        return clienteGuardado;
    }

    @Transactional
    public void deleteCliente(int id) {

        log.info("Eliminando cliente con id={}", id);

        clienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontró cliente con id={}", id);
                    return new ResourceNotFoundException("El cliente con id " + id + " no existe");
                });

        log.debug("Cliente id={} encontrado, procediendo a eliminar", id);

        clienteRepository.deleteById(id);

        log.info("Cliente id={} eliminado correctamente", id);
    }
}