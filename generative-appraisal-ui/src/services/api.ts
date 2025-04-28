import axios, { AxiosResponse } from 'axios';

const API_BASE_URL = 'http://localhost:8080'; // Replace with your backend URL

const api = {
  get: async <T>(url: string): Promise<T> => {
    const response: AxiosResponse<T> = await axios.get(`${API_BASE_URL}${url}`);
    return response.data;
  },
  post: async <T>(url: string, data: any): Promise<T> => {
    const response: AxiosResponse<T> = await axios.post(`${API_BASE_URL}${url}`, data);
    return response.data;
  },
  put: async <T>(url: string, data: any): Promise<T> => {
    const response: AxiosResponse<T> = await axios.put(`${API_BASE_URL}${url}`, data);
    return response.data;
  },
  delete: async <T>(url: string): Promise<T> => {
    const response: AxiosResponse<T> = await axios.delete(`${API_BASE_URL}${url}`);
    return response.data;
  },
};

export default api;