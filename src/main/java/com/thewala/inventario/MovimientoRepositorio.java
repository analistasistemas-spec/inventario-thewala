package com.thewala.inventario;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoRepositorio extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByEquipoIdOrderByFechaDesc(Long equipoId);
}