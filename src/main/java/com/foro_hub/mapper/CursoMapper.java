package com.foro_hub.mapper;

import com.foro_hub.domain.Curso;
import com.foro_hub.dto.curso.CursoCreateDTO;
import com.foro_hub.dto.curso.CursoResponseDTO;
import com.foro_hub.dto.curso.CursoUpdateDTO;

public class CursoMapper {

    public static Curso toEntity(final CursoCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }

        return Curso.builder()
                .withNombre(createDTO.nombre())
                .withCategoria(createDTO.categoria())
                .build();
    }

    public static CursoResponseDTO toResponseDTO(Curso cursoGuardado) {
        if (cursoGuardado == null) {
            return null;
        }

        return CursoResponseDTO.builder()
                .withId(cursoGuardado.getId())
                .withNombre(cursoGuardado.getNombre())
                .withCategoria(cursoGuardado.getCategoria())
                .build();
    }

    public static void updateEntityFromDTO(Curso curso, CursoUpdateDTO updateDTO) {
        if (curso == null || updateDTO == null) {
            return;
        }

        curso.setNombre(updateDTO.nombre());
        curso.setCategoria(updateDTO.categoria());
    }
}
