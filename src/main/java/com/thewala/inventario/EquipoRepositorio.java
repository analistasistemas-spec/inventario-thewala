package com.thewala.inventario;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EquipoRepositorio extends JpaRepository<Equipo, Long> {

    @Query("""
        SELECT e FROM Equipo e
        WHERE lower(e.placa) LIKE lower(concat('%', :texto, '%'))
           OR lower(e.marca) LIKE lower(concat('%', :texto, '%'))
           OR lower(e.modelo) LIKE lower(concat('%', :texto, '%'))
           OR lower(e.sede) LIKE lower(concat('%', :texto, '%'))
           OR lower(e.responsable) LIKE lower(concat('%', :texto, '%'))
           OR lower(e.tipo) LIKE lower(concat('%', :list, '%'))
           OR lower(e.procesador) LIKE lower(concat('%', :text, '%'))
           OR lower(e.ram) LIKE lower(concat('%', :text, '%'))
           OR lower(e.disco) LIKE lower(concat('%', :text, '%'))
           OR lower(e.estado) LIKE lower(concat('%', :list, '%'))                       
        """)
    List<Equipo> buscar(@Param("texto") String texto);
}