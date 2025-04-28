import { get } from './api';

const API_BASE_URL = 'http://localhost:8080';

export const getAllGoals = async () => {
  try {
    const response = await get(`${API_BASE_URL}/goals`);
    return response;
  } catch (error) {
    console.error('Error fetching goals:', error);
    throw error;
  }
};