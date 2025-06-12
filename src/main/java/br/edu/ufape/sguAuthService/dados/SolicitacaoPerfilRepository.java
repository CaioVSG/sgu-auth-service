package br.edu.ufape.sguAuthService.dados;

import br.edu.ufape.sguAuthService.models.Enums.StatusSolicitacao;
import br.edu.ufape.sguAuthService.models.SolicitacaoPerfil;
import br.edu.ufape.sguAuthService.models.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface SolicitacaoPerfilRepository extends JpaRepository<SolicitacaoPerfil, Long> {
    List<SolicitacaoPerfil> findBySolicitanteAndStatusIn(Usuario solicitante, Collection<StatusSolicitacao> status);

    Page<SolicitacaoPerfil> findAllBySolicitante_Id(UUID id, Pageable pageable);

    Page<SolicitacaoPerfil> findAllByStatus(StatusSolicitacao status, Pageable pageable);

}