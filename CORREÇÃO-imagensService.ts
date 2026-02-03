import api from "@/lib/api";
import { ImagemImovel } from "@/types";

export const imagensService = {
  async upload(imovelId: string, file: File, legenda?: string, ordem?: number): Promise<ImagemImovel> {
    const formData = new FormData();
    formData.append("files", file);
    if (legenda) formData.append("legenda", legenda);
    if (ordem !== undefined) formData.append("ordem", ordem.toString());

    const response = await api.post<ImagemImovel[]>(`/imoveis/${imovelId}/imagens`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data[0]; // Backend retorna array, pegamos o primeiro
  },

  async uploadMultiple(imovelId: string, files: File[]): Promise<ImagemImovel[]> {
    const formData = new FormData();
    files.forEach((file) => {
      formData.append("files", file);
    });

    const response = await api.post<ImagemImovel[]>(`/imoveis/${imovelId}/imagens`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  },

  async getByImovelId(imovelId: string): Promise<ImagemImovel[]> {
    const response = await api.get<ImagemImovel[]>(`/imoveis/${imovelId}/imagens`);
    return response.data;
  },

  async update(imagemId: string, data: { legenda?: string; ordem?: number }): Promise<ImagemImovel> {
    // CORRIGIDO: /imoveis/imagens/{id} ao invés de /imagens/{id}
    const params = new URLSearchParams();
    if (data.legenda !== undefined) params.append("legenda", data.legenda);
    if (data.ordem !== undefined) params.append("ordem", data.ordem.toString());
    
    const response = await api.put<ImagemImovel>(`/imoveis/imagens/${imagemId}?${params.toString()}`);
    return response.data;
  },

  async setPrincipal(imagemId: string): Promise<void> {
    // CORRIGIDO: /imoveis/imagens/{id}/principal ao invés de /imagens/{id}/principal
    await api.patch(`/imoveis/imagens/${imagemId}/principal`);
  },

  async delete(imagemId: string): Promise<void> {
    // CORRIGIDO: /imoveis/imagens/{id} ao invés de /imagens/{id}
    await api.delete(`/imoveis/imagens/${imagemId}`);
  },
};