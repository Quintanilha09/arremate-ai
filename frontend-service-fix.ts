// imagensService.ts - CORRIGIDO
import api from "./api";
import { ImagemImovel } from "@/types";

export const imagensService = {
  async uploadMultiple(imovelId: string, files: File[]) {
    const formData = new FormData();
    files.forEach((file) => {
      formData.append("files", file);
    });

    const response = await api.post<ImagemImovel[]>(
      `/imoveis/${imovelId}/imagens`,
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );
    return response.data;
  },

  async upload(imovelId: string, file: File) {
    const formData = new FormData();
    formData.append("files", file);

    const response = await api.post<ImagemImovel[]>(
      `/imoveis/${imovelId}/imagens`,
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );
    return response.data;
  },

  async getByImovelId(imovelId: string) {
    const response = await api.get<ImagemImovel[]>(
      `/imoveis/${imovelId}/imagens`
    );
    return response.data;
  },

  async update(
    imagemId: string,
    data: { legenda?: string; ordem?: number }
  ) {
    const params = new URLSearchParams();
    if (data.legenda !== undefined) params.append("legenda", data.legenda);
    if (data.ordem !== undefined) params.append("ordem", data.ordem.toString());

    const response = await api.put<ImagemImovel>(
      `/imoveis/imagens/${imagemId}?${params.toString()}`
    );
    return response.data;
  },

  async setPrincipal(imagemId: string) {
    // CORRIGIDO: usar PATCH em /api/imoveis/imagens/{id}/principal
    const response = await api.patch(
      `/imoveis/imagens/${imagemId}/principal`
    );
    return response.data;
  },

  async delete(imagemId: string) {
    // CORRIGIDO: usar DELETE em /api/imoveis/imagens/{id}
    const response = await api.delete(`/imoveis/imagens/${imagemId}`);
    return response.data;
  },
};
