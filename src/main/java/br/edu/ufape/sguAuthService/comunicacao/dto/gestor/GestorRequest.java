package br.edu.ufape.sguAuthService.comunicacao.dto.gestor;

import br.edu.ufape.sguAuthService.models.Gestor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class GestorRequest {
    @NotBlank(message = "O SIAPE é obrigatório")
    @Size(min = 1, max = 100, message = "O siape deve ter entre 1 e 100 caracteres")
    private String siape;

    @NotNull(message = "Os documentos são obrigatórios")
    private MultipartFile[] documentos;

    public Gestor convertToEntity(ModelMapper modelMapper)  {

        modelMapper.typeMap(GestorRequest.class, Gestor.class).addMappings(mapper -> {
            mapper.skip(Gestor::setId);  // Ignora o campo ID
        });

        return modelMapper.map(this, Gestor.class);
    }
}
