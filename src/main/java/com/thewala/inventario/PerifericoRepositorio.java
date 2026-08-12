package com.thewala.inventario;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerifericoRepositorio extends JpaRepository<Periferico, Long> {

    List<Periferico> findByEquipoId(Long equipoId);
}