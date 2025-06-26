package br.edu.ufape.sguAuthService.comunicacao.dto.tecnico;


import br.edu.ufape.sguAuthService.models.Tecnico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TecnicoRequest {
    @NotBlank(message = "O SIAPE é obrigatório")
    @Size(min = 1, max = 100, message = "O siape deve ter entre 1 e 100 caracteres")
    private String siape;

    @NotNull(message = "Os documentos são obrigatórios")
    private MultipartFile[] documentos;

    public Tecnico convertToEntity(ModelMapper modelMapper)  {

        modelMapper.typeMap(TecnicoRequest.class, Tecnico.class).addMappings(mapper -> {
            mapper.skip(Tecnico::setId);  // Ignora o campo ID
        });

        return modelMapper.map(this, Tecnico.class);
    }
}
