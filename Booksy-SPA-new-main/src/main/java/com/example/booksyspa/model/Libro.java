package com.example.booksyspa.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "libros")
@AllArgsConstructor
@NoArgsConstructor
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLibro;

    @NotBlank(message = "El ISBN no puede estar vacío")
    private String isbn;

    @NotBlank(message = "El título no puede estar vacío")
    private String titulo;

    // Muchos libros pueden pertenecer a un autor.
    // @JoinColumn define la columna de clave foránea en la tabla "libros".
    @ManyToOne
    @JoinColumn(name = "id_autor", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Autor autor;

    private String descripcion;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaPublicacion;
    
    @NotNull(message = "El precio no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;
    private String urlDescarga;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Categoria categoria;

    @OneToMany(mappedBy = "libro")
    @JsonIgnore
    private List<Pedido> pedidos;
}
