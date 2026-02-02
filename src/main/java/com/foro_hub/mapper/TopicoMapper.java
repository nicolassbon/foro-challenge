package com.foro_hub.mapper;

import com.foro_hub.domain.Curso;
import com.foro_hub.domain.Topico;
import com.foro_hub.domain.Usuario;
import com.foro_hub.dto.topico.TopicoCreateDTO;
import com.foro_hub.dto.topico.TopicoResponseDTO;
import com.foro_hub.dto.topico.TopicoUpdateDTO;

public class TopicoMapper {

    public static Topico toEntity(final TopicoCreateDTO dto, final Usuario autor, final Curso curso) {
        if (dto == null) {
            return null;
        }

        return Topico.builder()
                .withTitulo(dto.titulo())
                .withMensaje(dto.mensaje())
                .withAutor(autor)
                .withCurso(curso)
                .build();
    }

    public static TopicoResponseDTO toResponseDTO(final Topico topico) {
        if (topico == null) {
            return null;
        }

        return TopicoResponseDTO.builder()
                .withId(topico.getId())
                .withTitulo(topico.getTitulo())
                .withMensaje(topico.getMensaje())
                .withFechaCreacion(topico.getFechaCreacion())
                .build();
    }

    public static void updateEntityFromDTO(final Topico topico, final TopicoUpdateDTO dto) {
        if (topico == null || dto == null) {
            return;
        }

        topico.setTitulo(dto.titulo());
        topico.setMensaje(dto.mensaje());

        if (dto.status() != null) {
            topico.setStatus(dto.status());
        }
    }
}
