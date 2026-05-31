package com.example.booksyspa.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booksyspa.exception.ResourceNotFoundException;
import com.example.booksyspa.model.Cliente;
import com.example.booksyspa.model.EstadoPedido;
import com.example.booksyspa.model.Libro;
import com.example.booksyspa.model.Pedido;
import com.example.booksyspa.repository.ClienteRepository;
import com.example.booksyspa.repository.LibroRepository;
import com.example.booksyspa.repository.PedidoRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Pedido> getPedidos() {
        log.info("Consultando todos los pedidos");
        return pedidoRepository.findAll();
    }

    @Transactional
    public Pedido savePedido(Pedido pedido) {

        log.info("Intentando registrar pedido para cliente id={} y libro id={}",
                pedido.getCliente().getIdCliente(),
                pedido.getLibro().getIdLibro());

        Cliente cliente = clienteRepository.findById(pedido.getCliente().getIdCliente())
                .orElseThrow(() -> {
                    log.warn("No se encontró cliente con id={}",
                            pedido.getCliente().getIdCliente());

                    return new ResourceNotFoundException(
                            "El cliente con id "
                                    + pedido.getCliente().getIdCliente()
                                    + " no existe");
                });

        Libro libro = libroRepository.findById(pedido.getLibro().getIdLibro())
                .orElseThrow(() -> {
                    log.warn("No se encontró libro con id={}",
                            pedido.getLibro().getIdLibro());

                    return new ResourceNotFoundException(
                            "El libro con id "
                                    + pedido.getLibro().getIdLibro()
                                    + " no existe");
                });

        Pedido nuevoPedido = new Pedido();

        nuevoPedido.setCliente(cliente);
        nuevoPedido.setLibro(libro);

        nuevoPedido.setFechaPedido(LocalDate.now());
        nuevoPedido.setTotal(libro.getPrecio());
        nuevoPedido.setEstado(EstadoPedido.PENDIENTE);
        nuevoPedido.setUrlDescarga(null);

        Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);

        log.info("Pedido registrado correctamente con id={}",
                pedidoGuardado.getIdPedido());

        return pedidoGuardado;
    }

    public Pedido getPedidoId(int id) {

        log.info("Buscando pedido con id={}", id);

        return pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontró pedido con id={}", id);
                    return new ResourceNotFoundException(
                            "El pedido con id " + id + " no existe");
                });
    }

    public List<Pedido> getPedidosPorCliente(int idCliente) {

        log.info("Consultando pedidos del cliente id={}", idCliente);

        if (!clienteRepository.existsById(idCliente)) {
            log.warn("No existe cliente con id={}", idCliente);
            return Collections.emptyList();
        }

        return pedidoRepository.findByClienteIdCliente(idCliente);
    }

    public List<Pedido> getPedidosPorLibro(int idLibro) {

        log.info("Consultando pedidos del libro id={}", idLibro);

        return pedidoRepository.findByLibroIdLibro(idLibro);
    }

    public List<Pedido> getPedidosPorEstado(String estadoStr) {

        log.info("Consultando pedidos con estado={}", estadoStr);

        try {

            EstadoPedido estado = EstadoPedido.valueOf(
                    estadoStr.toUpperCase());

            return pedidoRepository.findByEstado(estado);

        } catch (IllegalArgumentException e) {

            log.warn("Estado de pedido inválido: {}", estadoStr);

            return Collections.emptyList();
        }
    }

    @Transactional
    public Pedido updatePedido(int id, Pedido pedidoActualizado) {

        log.info("Actualizando pedido con id={}", id);

        Pedido pedidoExistente = pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontró pedido con id={} para actualizar",
                            id);

                    return new ResourceNotFoundException(
                            "El pedido con id " + id + " no existe");
                });

        log.debug("Pedido encontrado para actualización: {}",
                pedidoExistente);

        if (pedidoActualizado.getEstado() != null) {

            log.info("Cambiando estado del pedido id={} a {}",
                    id,
                    pedidoActualizado.getEstado());

            pedidoExistente.setEstado(
                    pedidoActualizado.getEstado());
        }

        if (pedidoExistente.getEstado() == EstadoPedido.PAGADO
                && pedidoExistente.getUrlDescarga() == null) {

            log.info("Generando URL de descarga para pedido id={}", id);

            pedidoExistente.setUrlDescarga(
                    "www.booksyspa.cl/descarga/"
                            + pedidoExistente.getLibro().getIsbn()
                            + "/"
                            + UUID.randomUUID().toString());
        }

        log.info("Persistiendo cambios del pedido id={}", id);

        Pedido pedidoGuardado = pedidoRepository.save(
                pedidoExistente);

        log.info("Pedido id={} actualizado correctamente", id);

        return pedidoGuardado;
    }

    @Transactional
    public void deletePedido(int id) {

        log.info("Eliminando pedido con id={}", id);

        pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontró pedido con id={}", id);

                    return new ResourceNotFoundException(
                            "El pedido con id " + id + " no existe");
                });

        log.debug("Pedido id={} encontrado, procediendo a eliminar",
                id);

        pedidoRepository.deleteById(id);

        log.info("Pedido id={} eliminado correctamente", id);
    }
}