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
        """)
    List<Equipo> buscar(@Param("texto") String texto);
    @Query("SELECT e.sede, COUNT(e) FROM Equipo e GROUP BY e.sede ORDER BY e.sede")
    List<Object[]> contarPorSede();

    @Query("SELECT e.estado, COUNT(e) FROM Equipo e GROUP BY e.estado ORDER BY e.estado")
    List<Object[]> contarPorEstado();

    @Query("SELECT e.tipo, COUNT(e) FROM Equipo e GROUP BY e.tipo ORDER BY e.tipo")
    List<Object[]> contarPorTipo();
}