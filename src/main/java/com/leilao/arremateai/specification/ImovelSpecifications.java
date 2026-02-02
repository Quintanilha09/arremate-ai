package com.leilao.arremateai.specification;

import com.leilao.arremateai.domain.Imovel;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class ImovelSpecifications {

    private ImovelSpecifications() {
        throw new UnsupportedOperationException("Classe utilitária não deve ser instanciada");
    }

    public static Specification<Imovel> comUf(String uf) {
        return Optional.ofNullable(uf)
            .filter(valor -> !valor.isBlank())
            .map(valor -> (Specification<Imovel>) (root, query, cb) ->
                cb.equal(cb.upper(root.get("uf")), valor.toUpperCase()))
            .orElse(null);
    }

    public static Specification<Imovel> comCidade(String cidade) {
        return Optional.ofNullable(cidade)
            .filter(valor -> !valor.isBlank())
            .map(valor -> (Specification<Imovel>) (root, query, cb) ->
                cb.like(cb.upper(root.get("cidade")), "%" + valor.toUpperCase() + "%"))
            .orElse(null);
    }

    public static Specification<Imovel> comTipoImovel(String tipoImovel) {
        return Optional.ofNullable(tipoImovel)
            .filter(valor -> !valor.isBlank())
            .map(valor -> (Specification<Imovel>) (root, query, cb) ->
                cb.like(cb.upper(root.get("tipoImovel")), "%" + valor.toUpperCase() + "%"))
            .orElse(null);
    }

    public static Specification<Imovel> comInstituicao(String instituicao) {
        return Optional.ofNullable(instituicao)
            .filter(valor -> !valor.isBlank())
            .map(valor -> (Specification<Imovel>) (root, query, cb) ->
                cb.like(cb.upper(root.get("instituicao")), "%" + valor.toUpperCase() + "%"))
            .orElse(null);
    }

    public static Specification<Imovel> comValorMinimo(BigDecimal valorMinimo) {
        return Optional.ofNullable(valorMinimo)
            .map(valor -> (Specification<Imovel>) (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("valorAvaliacao"), valor))
            .orElse(null);
    }

    public static Specification<Imovel> comValorMaximo(BigDecimal valorMaximo) {
        return Optional.ofNullable(valorMaximo)
            .map(valor -> (Specification<Imovel>) (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("valorAvaliacao"), valor))
            .orElse(null);
    }

    public static Specification<Imovel> comBuscaTexto(String busca) {
        return Optional.ofNullable(busca)
            .filter(valor -> !valor.isBlank())
            .map(valor -> (Specification<Imovel>) (root, query, cb) ->
                cb.like(cb.upper(root.get("descricao")), "%" + valor.toUpperCase() + "%"))
            .orElse(null);
    }

    public static Specification<Imovel> apenasAtivos() {
        return (root, query, cb) -> cb.equal(root.get("ativo"), true);
    }

    public static Specification<Imovel> combinar(Specification<Imovel>... specs) {
        return List.of(specs).stream()
            .filter(spec -> spec != null)
            .reduce(Specification::and)
            .orElse(null);
    }

    public static class Builder {
        private final List<Specification<Imovel>> specifications = new ArrayList<>();

        public Builder comUf(String uf) {
            adicionarSePresente(uf, ImovelSpecifications::comUf);
            return this;
        }

        public Builder comCidade(String cidade) {
            adicionarSePresente(cidade, ImovelSpecifications::comCidade);
            return this;
        }

        public Builder comTipoImovel(String tipoImovel) {
            adicionarSePresente(tipoImovel, ImovelSpecifications::comTipoImovel);
            return this;
        }

        public Builder comInstituicao(String instituicao) {
            adicionarSePresente(instituicao, ImovelSpecifications::comInstituicao);
            return this;
        }

        public Builder comValorMinimo(BigDecimal valorMinimo) {
            adicionarSePresente(valorMinimo, ImovelSpecifications::comValorMinimo);
            return this;
        }

        public Builder comValorMaximo(BigDecimal valorMaximo) {
            adicionarSePresente(valorMaximo, ImovelSpecifications::comValorMaximo);
            return this;
        }

        public Builder comBuscaTexto(String busca) {
            adicionarSePresente(busca, ImovelSpecifications::comBuscaTexto);
            return this;
        }

        public Specification<Imovel> construir() {
            return specifications.stream()
                .reduce(Specification::and)
                .orElse(Specification.where(null));
        }

        private <T> void adicionarSePresente(T valor, Function<T, Specification<Imovel>> specFactory) {
            Optional.ofNullable(specFactory.apply(valor))
                .ifPresent(specifications::add);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
