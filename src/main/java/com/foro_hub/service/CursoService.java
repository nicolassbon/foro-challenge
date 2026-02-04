package com.foro_hub.service;

import com.foro_hub.domain.Curso;
import com.foro_hub.dto.curso.CursoCreateDTO;
import com.foro_hub.dto.curso.CursoResponseDTO;
import com.foro_hub.dto.curso.CursoUpdateDTO;
import com.foro_hub.exception.ResourceNotFoundException;
import com.foro_hub.mapper.CursoMapper;
import com.foro_hub.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoResponseDTO crearCurso(final CursoCreateDTO createDTO) {
        log.info("Creando curso con nombre: {}", createDTO.nombre());

        final Curso cursoGuardado = cursoRepository.save(CursoMapper.toEntity(createDTO));
        log.info("Curso creado con ID: {}", cursoGuardado.getId());

        return CursoMapper.toResponseDTO(cursoGuardado);
    }

    @Transactional(readOnly = true)
    public CursoResponseDTO obtenerCursoPorId(Long id) {
        log.info("Buscando curso con id: {}", id);

        final Curso curso = cursoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el curso con ID: " + id));

        return CursoMapper.toResponseDTO(curso);
    }

    @Transactional(readOnly = true)
    public Page<CursoResponseDTO> listarCursos(final Pageable pageable) {
        log.info("Listando todos los cursos con paginación: {}", pageable);

        return cursoRepository.findByActivoTrue(pageable)
                .map(CursoMapper::toResponseDTO);
    }

    public CursoResponseDTO actualizarCurso(final Long id, final CursoUpdateDTO updateDTO) {
        log.info("Actualizando curso con id: {}", id);

        final Curso curso = cursoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el curso con ID: " + id));

        CursoMapper.updateEntityFromDTO(curso, updateDTO);
        final Curso cursoActualizado = cursoRepository.save(curso);
        log.info("Curso actualizado con ID: {}", id);

        return CursoMapper.toResponseDTO(cursoActualizado);
    }

    public void eliminarCurso(final Long id) {
        log.info("Eliminando curso con id: {}", id);

        final Curso curso = cursoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el curso con ID: " + id));

        curso.setActivo(false);
        cursoRepository.save(curso);

        log.info("Curso eliminado (soft delete) exitosamente con id: {}", id);
    }
}
