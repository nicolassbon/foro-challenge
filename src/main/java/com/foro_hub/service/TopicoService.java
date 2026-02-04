package com.foro_hub.service;

import com.foro_hub.domain.Curso;
import com.foro_hub.domain.Topico;
import com.foro_hub.domain.Usuario;
import com.foro_hub.dto.topico.TopicoCreateDTO;
import com.foro_hub.dto.topico.TopicoResponseDTO;
import com.foro_hub.dto.topico.TopicoUpdateDTO;
import com.foro_hub.exception.DuplicateTopicoException;
import com.foro_hub.exception.ResourceNotFoundException;
import com.foro_hub.mapper.TopicoMapper;
import com.foro_hub.repository.CursoRepository;
import com.foro_hub.repository.TopicoRepository;
import com.foro_hub.repository.UsuarioRepository;
import com.foro_hub.util.AuthenticationUtils;
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
public class TopicoService {

    private final TopicoRepository topicoRepository;
    private final CursoRepository cursoRepository;

    public TopicoResponseDTO crearTopico(final TopicoCreateDTO createDTO) {
        log.info("Creando Topico con titulo: {}", createDTO.titulo());

        validarTopicoDuplicado(createDTO.titulo(), createDTO.mensaje());

        final Usuario autor = AuthenticationUtils.getAuthenticatedUser();
        log.info("Autor del topico obtenido del contexto de seguridad: {}", autor.getEmail());

        final Curso curso = cursoRepository.findByIdAndActivoTrue(createDTO.idCurso())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el curso con ID: " + createDTO.idCurso()));

        final Topico topicoGuardado = topicoRepository.save(TopicoMapper.toEntity(createDTO, autor, curso));

        log.info("Topico creado exitosamente con ID: {}", topicoGuardado.getId());

        return TopicoMapper.toResponseDTO(topicoGuardado);
    }

    @Transactional(readOnly = true)
    public TopicoResponseDTO obtenerTopicoPorId(final Long id) {
        log.info("Obteniendo Topico con ID: {}", id);

        final Topico topico = topicoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el tópico con ID: " + id));

        return TopicoMapper.toResponseDTO(topico);
    }


    @Transactional(readOnly = true)
    public Page<TopicoResponseDTO> listarTopicos(final Pageable pageable) {
        log.info("Listando todos los Topicos con paginación");

        return topicoRepository.findByActivoTrue(pageable)
                .map(TopicoMapper::toResponseDTO);
    }


    public TopicoResponseDTO actualizarTopico(final Long id, final TopicoUpdateDTO dto) {
        log.info("Actualizando Topico con ID: {}", id);

        final Topico topico = topicoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el tópico con ID: " + id));

        if (!topico.getTitulo().equals(dto.titulo()) || !topico.getMensaje().equals(dto.mensaje())) {
            validarTopicoDuplicado(dto.titulo(), dto.mensaje());
        }

        TopicoMapper.updateEntityFromDTO(topico, dto);
        final Topico topicoActualizado = topicoRepository.save(topico);

        log.info("Topico actualizado exitosamente con ID: {}", id);

        return TopicoMapper.toResponseDTO(topicoActualizado);
    }


    public void eliminarTopico(final Long id) {
        log.info("Eliminando Topico con ID: {}", id);

        final Topico topico = topicoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el tópico con ID: " + id));

        topico.setActivo(false);
        topicoRepository.save(topico);

        log.info("Topico eliminado (soft delete) exitosamente con id: {}", id);
    }

    private void validarTopicoDuplicado(final String titulo, final String mensaje) {
        final boolean existeTopico = topicoRepository.existsByTituloAndMensaje(titulo, mensaje);

        if (existeTopico) {
            log.warn("Intento de crear un Topico duplicado con titulo y mensaje: {}, {}", titulo, mensaje);
            throw new DuplicateTopicoException("Ya existe un tópico con el mismo título y mensaje.");
        }
    }
}
