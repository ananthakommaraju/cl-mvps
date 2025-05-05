import axios, { AxiosInstance } from 'axios';

class Api {
  private axiosInstance: AxiosInstance;
  private baseUrl: string = 'http://localhost:8082/todo';

  constructor() {
    this.axiosInstance = axios.create({
      baseURL: this.baseUrl,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }

  async create(data: any): Promise<any> {
    const response = await this.axiosInstance.post('', data);
    return response.data;
  }

  async getById(id: number): Promise<any> {
    const response = await this.axiosInstance.get(`/${id}`);
    return response.data;
  }

  async listAll(): Promise<any[]> {
    const response = await this.axiosInstance.get('');
    return response.data;
  }

  async update(id: number, data: any): Promise<any> {
    const response = await this.axiosInstance.put(`/${id}`, data);
    return response.data;
  }

  async delete(id: number): Promise<void> {
    await this.axiosInstance.delete(`/${id}`);
  }
}

export default new Api();