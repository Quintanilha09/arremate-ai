"use client";

import { useState, useEffect } from "react";
import { useQuery } from "@tanstack/react-query";
import { Header } from "@/components/Header";
import { Card } from "@/components/Card";
import { Input } from "@/components/Input";
import { Label } from "@/components/Label";
import { Button } from "@/components/Button";
import Link from "next/link";

interface FiltrosImoveis {
  tipoImovel: string;
  uf: string;
  cidade: string;
  valorMin: string;
  valorMax: string;
  quartosMin: string;
  banheirosMin: string;
  vagasMin: string;
  areaMin: string;
  areaMax: string;
  busca: string;
}

interface ImagemImovel {
  id: string;
  url: string;
  legenda: string;
  principal: boolean;
  ordem: number;
}

interface Imovel {
  id: string;
  numeroLeilao: string;
  descricao: string;
  valorAvaliacao: number;
  dataLeilao: string;
  uf: string;
  cidade: string;
  bairro?: string;
  tipoImovel?: string;
  quartos?: number;
  banheiros?: number;
  vagas?: number;
  areaTotal?: number;
  instituicao: string;
  imagens?: ImagemImovel[];
}

const TIPOS_IMOVEL = [
  "Todos",
  "Casa",
  "Apartamento",
  "Terreno",
  "Sala Comercial",
  "Galpão",
  "Sobrado",
  "Cobertura",
  "Loft"
];

const UFS_BRASIL = [
  "Todos",
  "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA",
  "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN",
  "RS", "RO", "RR", "SC", "SP", "SE", "TO"
];

export default function ImoveisPage() {
  const [filtros, setFiltros] = useState<FiltrosImoveis>({
    tipoImovel: "",
    uf: "",
    cidade: "",
    valorMin: "",
    valorMax: "",
    quartosMin: "",
    banheirosMin: "",
    vagasMin: "",
    areaMin: "",
    areaMax: "",
    busca: "",
  });

  const [filtrosAplicados, setFiltrosAplicados] = useState<FiltrosImoveis>(filtros);
  const [mostrarFiltros, setMostrarFiltros] = useState(false);

  const { data: imoveis, isLoading } = useQuery<Imovel[]>({
    queryKey: ["imoveis", filtrosAplicados],
    queryFn: async () => {
      const params = new URLSearchParams();
      
      if (filtrosAplicados.tipoImovel && filtrosAplicados.tipoImovel !== "Todos") {
        params.append("tipoImovel", filtrosAplicados.tipoImovel);
      }
      if (filtrosAplicados.uf && filtrosAplicados.uf !== "Todos") {
        params.append("uf", filtrosAplicados.uf);
      }
      if (filtrosAplicados.cidade) {
        params.append("cidade", filtrosAplicados.cidade);
      }
      if (filtrosAplicados.valorMin) {
        params.append("valorMin", filtrosAplicados.valorMin);
      }
      if (filtrosAplicados.valorMax) {
        params.append("valorMax", filtrosAplicados.valorMax);
      }
      if (filtrosAplicados.quartosMin) {
        params.append("quartosMin", filtrosAplicados.quartosMin);
      }
      if (filtrosAplicados.banheirosMin) {
        params.append("banheirosMin", filtrosAplicados.banheirosMin);
      }
      if (filtrosAplicados.vagasMin) {
        params.append("vagasMin", filtrosAplicados.vagasMin);
      }
      if (filtrosAplicados.areaMin) {
        params.append("areaMin", filtrosAplicados.areaMin);
      }
      if (filtrosAplicados.areaMax) {
        params.append("areaMax", filtrosAplicados.areaMax);
      }
      if (filtrosAplicados.busca) {
        params.append("busca", filtrosAplicados.busca);
      }

      const token = localStorage.getItem("token");
      const response = await fetch(
        `http://localhost:8080/api/imoveis?${params.toString()}`,
        {
          headers: {
            ...(token && { Authorization: `Bearer ${token}` }),
          },
        }
      );

      if (!response.ok) throw new Error("Erro ao buscar imóveis");
      
      const data = await response.json();
      // Se for Page<Imovel>, pega o content
      return Array.isArray(data) ? data : data.content || [];
    },
  });

  const aplicarFiltros = () => {
    setFiltrosAplicados(filtros);
  };

  const limparFiltros = () => {
    const filtrosLimpos = {
      tipoImovel: "",
      uf: "",
      cidade: "",
      valorMin: "",
      valorMax: "",
      quartosMin: "",
      banheirosMin: "",
      vagasMin: "",
      areaMin: "",
      areaMax: "",
      busca: "",
    };
    setFiltros(filtrosLimpos);
    setFiltrosAplicados(filtrosLimpos);
  };

  const formatarValor = (valor: number) => {
    return new Intl.NumberFormat("pt-BR", {
      style: "currency",
      currency: "BRL",
    }).format(valor);
  };

  const obterImagemPrincipal = (imovel: Imovel): string | null => {
    if (!imovel.imagens || imovel.imagens.length === 0) {
      return null;
    }
    const principal = imovel.imagens.find((img) => img.principal);
    return principal?.url || imovel.imagens[0]?.url || null;
  };

  return (
    <div className="min-h-screen bg-slate-50">
      <Header />

      <div className="max-w-7xl mx-auto px-4 py-8">
        {/* Header da Página */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-slate-900 mb-2">
            Imóveis em Leilão
          </h1>
          <p className="text-slate-600">
            Explore as melhores oportunidades em leilões de imóveis de todo o Brasil
          </p>
        </div>

        {/* Barra de Busca e Filtros */}
        <Card className="mb-6">
          <div className="p-4">
            <div className="flex gap-3">
              <div className="flex-1">
                <Input
                  type="text"
                  placeholder="Buscar por descrição, cidade, bairro..."
                  value={filtros.busca}
                  onChange={(e) =>
                    setFiltros({ ...filtros, busca: e.target.value })
                  }
                  onKeyPress={(e) => {
                    if (e.key === "Enter") aplicarFiltros();
                  }}
                  className="w-full"
                />
              </div>
              <Button
                onClick={() => setMostrarFiltros(!mostrarFiltros)}
                variant="outline"
                className="flex items-center gap-2"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="h-5 w-5"
                  viewBox="0 0 20 20"
                  fill="currentColor"
                >
                  <path
                    fillRule="evenodd"
                    d="M3 3a1 1 0 011-1h12a1 1 0 011 1v3a1 1 0 01-.293.707L12 11.414V15a1 1 0 01-.293.707l-2 2A1 1 0 018 17v-5.586L3.293 6.707A1 1 0 013 6V3z"
                    clipRule="evenodd"
                  />
                </svg>
                Mais Filtros
              </Button>
              <Button onClick={aplicarFiltros}>Buscar</Button>
            </div>

            {/* Painel de Filtros Expandido */}
            {mostrarFiltros && (
              <div className="mt-6 pt-6 border-t border-slate-200">
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                  {/* Tipo de Imóvel */}
                  <div>
                    <Label>Tipo de Imóvel</Label>
                    <select
                      value={filtros.tipoImovel}
                      onChange={(e) =>
                        setFiltros({ ...filtros, tipoImovel: e.target.value })
                      }
                      className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white text-slate-900"
                    >
                      {TIPOS_IMOVEL.map((tipo) => (
                        <option key={tipo} value={tipo === "Todos" ? "" : tipo} className="bg-white text-slate-900 py-2">
                          {tipo}
                        </option>
                      ))}
                    </select>
                  </div>

                  {/* Estado */}
                  <div>
                    <Label>Estado (UF)</Label>
                    <select
                      value={filtros.uf}
                      onChange={(e) =>
                        setFiltros({ ...filtros, uf: e.target.value })
                      }
                      className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white text-slate-900"
                    >
                      {UFS_BRASIL.map((uf) => (
                        <option key={uf} value={uf === "Todos" ? "" : uf} className="bg-white text-slate-900 py-2">
                          {uf}
                        </option>
                      ))}
                    </select>
                  </div>

                  {/* Cidade */}
                  <div>
                    <Label>Cidade</Label>
                    <Input
                      type="text"
                      placeholder="Ex: São Paulo"
                      value={filtros.cidade}
                      onChange={(e) =>
                        setFiltros({ ...filtros, cidade: e.target.value })
                      }
                    />
                  </div>

                  {/* Valor Mínimo */}
                  <div>
                    <Label>Preço mínimo</Label>
                    <Input
                      type="number"
                      placeholder="R$ 150.000"
                      value={filtros.valorMin}
                      onChange={(e) =>
                        setFiltros({ ...filtros, valorMin: e.target.value })
                      }
                    />
                  </div>

                  {/* Valor Máximo */}
                  <div>
                    <Label>Preço máximo</Label>
                    <Input
                      type="number"
                      placeholder="R$ 300.000"
                      value={filtros.valorMax}
                      onChange={(e) =>
                        setFiltros({ ...filtros, valorMax: e.target.value })
                      }
                    />
                  </div>

                  {/* Quartos */}
                  <div>
                    <Label>Quartos (mínimo)</Label>
                    <select
                      value={filtros.quartosMin}
                      onChange={(e) =>
                        setFiltros({ ...filtros, quartosMin: e.target.value })
                      }
                      className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white text-slate-900"
                    >
                      <option value="" className="bg-white text-slate-900 py-2">Tanto faz</option>
                      <option value="1" className="bg-white text-slate-900 py-2">1+</option>
                      <option value="2" className="bg-white text-slate-900 py-2">2+</option>
                      <option value="3" className="bg-white text-slate-900 py-2">3+</option>
                      <option value="4" className="bg-white text-slate-900 py-2">4+</option>
                    </select>
                  </div>

                  {/* Banheiros */}
                  <div>
                    <Label>Banheiros (mínimo)</Label>
                    <select
                      value={filtros.banheirosMin}
                      onChange={(e) =>
                        setFiltros({ ...filtros, banheirosMin: e.target.value })
                      }
                      className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white text-slate-900"
                    >
                      <option value="" className="bg-white text-slate-900 py-2">Tanto faz</option>
                      <option value="1" className="bg-white text-slate-900 py-2">1+</option>
                      <option value="2" className="bg-white text-slate-900 py-2">2+</option>
                      <option value="3" className="bg-white text-slate-900 py-2">3+</option>
                    </select>
                  </div>

                  {/* Vagas de Garagem */}
                  <div>
                    <Label>Vagas de garagem</Label>
                    <select
                      value={filtros.vagasMin}
                      onChange={(e) =>
                        setFiltros({ ...filtros, vagasMin: e.target.value })
                      }
                      className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white text-slate-900"
                    >
                      <option value="" className="bg-white text-slate-900 py-2">Tanto faz</option>
                      <option value="1" className="bg-white text-slate-900 py-2">1+</option>
                      <option value="2" className="bg-white text-slate-900 py-2">2+</option>
                      <option value="3" className="bg-white text-slate-900 py-2">3+</option>
                    </select>
                  </div>

                  {/* Área Mínima */}
                  <div>
                    <Label>Área mínima (m²)</Label>
                    <Input
                      type="number"
                      placeholder="Ex: 50"
                      value={filtros.areaMin}
                      onChange={(e) =>
                        setFiltros({ ...filtros, areaMin: e.target.value })
                      }
                    />
                  </div>

                  {/* Área Máxima */}
                  <div>
                    <Label>Área máxima (m²)</Label>
                    <Input
                      type="number"
                      placeholder="Ex: 200"
                      value={filtros.areaMax}
                      onChange={(e) =>
                        setFiltros({ ...filtros, areaMax: e.target.value })
                      }
                    />
                  </div>
                </div>

                <div className="flex gap-3 mt-6">
                  <Button onClick={aplicarFiltros} className="flex-1">
                    Aplicar Filtros
                  </Button>
                  <Button
                    onClick={limparFiltros}
                    variant="outline"
                    className="flex-1"
                  >
                    Limpar Filtros
                  </Button>
                </div>
              </div>
            )}
          </div>
        </Card>

        {/* Resultados */}
        {isLoading ? (
          <div className="text-center py-12">
            <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            <p className="mt-4 text-slate-600">Carregando imóveis...</p>
          </div>
        ) : (
          <>
            <div className="mb-4 text-slate-600">
              {imoveis?.length || 0} imóveis encontrados
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {imoveis?.map((imovel) => (
                <Link
                  key={imovel.id}
                  href={`/imoveis/${imovel.id}`}
                  className="block transition-transform hover:scale-105"
                >
                  <Card className="overflow-hidden h-full hover:shadow-xl transition-shadow">
                    {/* Imagem */}
                    <div className="relative h-48 bg-gradient-to-br from-slate-200 to-slate-300">
                      {obterImagemPrincipal(imovel) ? (
                        <img
                          src={obterImagemPrincipal(imovel)!}
                          alt={imovel.descricao}
                          className="w-full h-full object-cover"
                          onError={(e) => {
                            e.currentTarget.style.display = 'none';
                          }}
                        />
                      ) : (
                        <div className="w-full h-full flex items-center justify-center">
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            className="h-16 w-16 text-slate-400"
                            fill="none"
                            viewBox="0 0 24 24"
                            stroke="currentColor"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              strokeWidth={1.5}
                              d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"
                            />
                          </svg>
                        </div>
                      )}
                      {imovel.tipoImovel && (
                        <div className="absolute top-3 right-3 bg-blue-600 text-white px-3 py-1 rounded-full text-sm font-medium">
                          {imovel.tipoImovel}
                        </div>
                      )}
                    </div>

                    {/* Conteúdo */}
                    <div className="p-4">
                      {/* Preço */}
                      <div className="text-2xl font-bold text-blue-600 mb-2">
                        {formatarValor(imovel.valorAvaliacao)}
                      </div>

                      {/* Descrição */}
                      <p className="text-slate-900 font-medium mb-2 line-clamp-2">
                        {imovel.descricao}
                      </p>

                      {/* Localização */}
                      <div className="flex items-center text-slate-600 text-sm mb-3">
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          className="h-4 w-4 mr-1"
                          viewBox="0 0 20 20"
                          fill="currentColor"
                        >
                          <path
                            fillRule="evenodd"
                            d="M5.05 4.05a7 7 0 119.9 9.9L10 18.9l-4.95-4.95a7 7 0 010-9.9zM10 11a2 2 0 100-4 2 2 0 000 4z"
                            clipRule="evenodd"
                          />
                        </svg>
                        {imovel.cidade && imovel.uf
                          ? `${imovel.cidade} - ${imovel.uf}`
                          : imovel.uf || "Localização não informada"}
                      </div>

                      {/* Características */}
                      <div className="flex gap-4 text-sm text-slate-600 mb-3">
                        {imovel.quartos && (
                          <div className="flex items-center">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              className="h-4 w-4 mr-1"
                              viewBox="0 0 20 20"
                              fill="currentColor"
                            >
                              <path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z" />
                            </svg>
                            {imovel.quartos} quartos
                          </div>
                        )}
                        {imovel.banheiros && (
                          <div className="flex items-center">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              className="h-4 w-4 mr-1"
                              fill="currentColor"
                              viewBox="0 0 24 24"
                            >
                              <path d="M21 10H7V7c0-1.103.897-2 2-2s2 .897 2 2h2c0-2.206-1.794-4-4-4S5 4.794 5 7v3H3a1 1 0 00-1 1v2c0 2.606 1.674 4.823 4 5.65V22h2v-3h8v3h2v-3.35c2.326-.827 4-3.044 4-5.65v-2a1 1 0 00-1-1z" />
                            </svg>
                            {imovel.banheiros} banheiros
                          </div>
                        )}
                        {imovel.vagas && (
                          <div className="flex items-center">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              className="h-4 w-4 mr-1"
                              viewBox="0 0 20 20"
                              fill="currentColor"
                            >
                              <path d="M8 16.5a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0zM15 16.5a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0z" />
                              <path d="M3 4a1 1 0 00-1 1v10a1 1 0 001 1h1.05a2.5 2.5 0 014.9 0H10a1 1 0 001-1V5a1 1 0 00-1-1H3zM14 7a1 1 0 00-1 1v6.05A2.5 2.5 0 0115.95 16H17a1 1 0 001-1v-5a1 1 0 00-.293-.707l-2-2A1 1 0 0015 7h-1z" />
                            </svg>
                            {imovel.vagas} vagas
                          </div>
                        )}
                        {imovel.areaTotal && (
                          <div className="flex items-center">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              className="h-4 w-4 mr-1"
                              viewBox="0 0 20 20"
                              fill="currentColor"
                            >
                              <path d="M5 3a2 2 0 00-2 2v2a2 2 0 002 2h2a2 2 0 002-2V5a2 2 0 00-2-2H5zM5 11a2 2 0 00-2 2v2a2 2 0 002 2h2a2 2 0 002-2v-2a2 2 0 00-2-2H5zM11 5a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V5zM13 11a2 2 0 00-2 2v2a2 2 0 002 2h2a2 2 0 002-2v-2a2 2 0 00-2-2h-2z" />
                            </svg>
                            {imovel.areaTotal}m²
                          </div>
                        )}
                      </div>

                      {/* Instituição e Data do Leilão */}
                      <div className="pt-3 border-t border-slate-200">
                        <p className="text-xs text-slate-500 mb-1">
                          {imovel.instituicao}
                        </p>
                        <p className="text-xs text-slate-500">
                          Leilão: {new Date(imovel.dataLeilao).toLocaleDateString("pt-BR")}
                        </p>
                      </div>
                    </div>
                  </Card>
                </Link>
              ))}
            </div>

            {(!imoveis || imoveis.length === 0) && (
              <div className="text-center py-12">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="h-16 w-16 mx-auto text-slate-300 mb-4"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"
                  />
                </svg>
                <h3 className="text-xl font-medium text-slate-700 mb-2">
                  Nenhum imóvel encontrado
                </h3>
                <p className="text-slate-500">
                  Tente ajustar os filtros para encontrar mais resultados
                </p>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}
