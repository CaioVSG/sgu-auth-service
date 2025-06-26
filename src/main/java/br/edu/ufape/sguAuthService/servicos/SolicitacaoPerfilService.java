package br.edu.ufape.sguAuthService.servicos;


import br.edu.ufape.sguAuthService.config.AuthenticatedUserProvider;
import br.edu.ufape.sguAuthService.dados.SolicitacaoPerfilRepository;
import br.edu.ufape.sguAuthService.exceptions.SolicitacaoDuplicadaException;
import br.edu.ufape.sguAuthService.exceptions.SolicitacaoNaoPendenteException;
import br.edu.ufape.sguAuthService.exceptions.notFoundExceptions.SolicitacaoNotFoundException;
import br.edu.ufape.sguAuthService.models.Documento;
import br.edu.ufape.sguAuthService.models.Enums.StatusSolicitacao;
import br.edu.ufape.sguAuthService.models.Perfil;
import br.edu.ufape.sguAuthService.models.SolicitacaoPerfil;
import br.edu.ufape.sguAuthService.models.Usuario;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class SolicitacaoPerfilService implements br.edu.ufape.sguAuthService.servicos.interfaces.SolicitacaoPerfilService {
    private final SolicitacaoPerfilRepository solicitacaoPerfilRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;



    @Override
    @Transactional
    public SolicitacaoPerfil solicitarPerfil(Perfil perfil, Usuario solicitante, List<Documento> documentos) throws SolicitacaoDuplicadaException {
        SolicitacaoPerfil solicitacaoPerfil = new SolicitacaoPerfil();
        solicitacaoPerfil.setPerfil(perfil);
        solicitacaoPerfil.setSolicitante(solicitante);
        solicitacaoPerfil.setDocumentos(documentos);
        solicitacaoPerfil.setStatus(StatusSolicitacao.PENDENTE);
        solicitacaoPerfil.setDataSolicitacao(LocalDateTime.now());
        solicitacaoPerfil.setPerfilSolicitado(perfil.getClass().getSimpleName());
        List<SolicitacaoPerfil> solicitacoes = solicitacaoPerfilRepository.findBySolicitanteAndStatusIn(
                solicitacaoPerfil.getSolicitante(),
                List.of(StatusSolicitacao.PENDENTE, StatusSolicitacao.APROVADA));
        for (SolicitacaoPerfil solicitacao : solicitacoes) {
            if (solicitacao.getPerfil().getClass().equals(perfil.getClass())) {
                throw new SolicitacaoDuplicadaException(
                        "Já existe uma solicitação pendente ou aprovada para o perfil: " + perfil.getClass().getSimpleName());
            }
        }
        return solicitacaoPerfilRepository.save(solicitacaoPerfil);

    }

    @Override
    public SolicitacaoPerfil buscarSolicitacao(Long id) throws SolicitacaoNotFoundException {
        return solicitacaoPerfilRepository.findById(id)
                .orElseThrow(SolicitacaoNotFoundException::new);
    }

    @Override
    public Page<SolicitacaoPerfil> buscarSolicitacoesUsuarioAtual(Pageable pageable) {
        UUID sessionId = authenticatedUserProvider.getUserId();
        return solicitacaoPerfilRepository.findAllBySolicitante_Id(sessionId, pageable);
    }

    @Override
    public Page<SolicitacaoPerfil> buscarSolicitacoesPorId(UUID id, Pageable pageable) {
        return solicitacaoPerfilRepository.findAllBySolicitante_Id(id, pageable);
    }

    @Override
    public Page<SolicitacaoPerfil> listarSolicitacoes(Pageable pageable) {
        return solicitacaoPerfilRepository.findAll(pageable);
    }

    @Override
    public Page<SolicitacaoPerfil> listarSolicitacoesPendentes(Pageable pageable) {
        return solicitacaoPerfilRepository.findAllByStatus(StatusSolicitacao.PENDENTE, pageable);
    }


    @Override
    @Transactional
    public SolicitacaoPerfil aceitarSolicitacao(Long id, SolicitacaoPerfil parecer) throws SolicitacaoNotFoundException, SolicitacaoNaoPendenteException {
        SolicitacaoPerfil solicitacaoPerfil = buscarSolicitacao(id);
        if (solicitacaoPerfil.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new SolicitacaoNaoPendenteException();
        }
        solicitacaoPerfil.setParecer(parecer.getParecer());
        solicitacaoPerfil.setResponsavel(parecer.getResponsavel());
        solicitacaoPerfil.setDataAvaliacao(LocalDateTime.now());
        solicitacaoPerfil.setStatus(StatusSolicitacao.APROVADA);
        solicitacaoPerfil.getSolicitante().adicionarPerfil(solicitacaoPerfil.getPerfil());
        return solicitacaoPerfilRepository.save(solicitacaoPerfil);
    }

    @Override
    @Transactional
    public SolicitacaoPerfil rejeitarSolicitacao(Long id, SolicitacaoPerfil parecer) throws SolicitacaoNotFoundException {
        SolicitacaoPerfil solicitacaoPerfil = buscarSolicitacao(id);
        if (solicitacaoPerfil.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new SolicitacaoNaoPendenteException();
        }
        solicitacaoPerfil.setParecer(parecer.getParecer());
        solicitacaoPerfil.setResponsavel(parecer.getResponsavel());
        solicitacaoPerfil.setStatus(StatusSolicitacao.REJEITADA);
        return solicitacaoPerfilRepository.save(solicitacaoPerfil);
    }

}
