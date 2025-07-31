package br.edu.ufape.sguAuthService.comunicacao.dto.usuario;


import br.edu.ufape.sguAuthService.comunicacao.dto.tipoEtnia.TipoEtniaResponse;
import br.edu.ufape.sguAuthService.models.Usuario;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.UUID;

@Getter @Setter
public class UsuarioResponse {
    UUID id;
    String nome;
    String nomeSocial;
    String cpf;
    String email;
    String telefone;
    TipoEtniaResponse tipoEtnia;

    public UsuarioResponse(Usuario aluno, ModelMapper modelMapper){
        if (aluno == null) throw new IllegalArgumentException("Usuario não pode ser nulo");
        else modelMapper.map(aluno, this);
    }

    public UsuarioResponse() {
    }
}