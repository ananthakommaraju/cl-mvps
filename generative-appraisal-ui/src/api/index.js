import axios from 'axios';

const BASE_URL = "http://localhost:8081/api";

export const fetchGoals = async () => {
  const response = await axios.get(`${BASE_URL}/goals`);
  return response.data;
};

export const fetchObjectives = async () => {
  const response = await axios.get(`${BASE_URL}/objectives`);
  return response.data;
};

export const createGoal = async (goalData) => {
  const response = await axios.post(`${BASE_URL}/goals/add`, goalData);
  return response.data;
};

export const updateGoal = async (goalData) => {
  const response = await axios.put(`${BASE_URL}/goals/edit`, goalData);
  return response.data;
};

export const createObjective = async (objectiveData) => {
  const response = await axios.post(`${BASE_URL}/objectives`, objectiveData);
  return response.data;
};

export const updateObjective = async (objectiveData) => {
  const response = await axios.put(`${BASE_URL}/objectives`, objectiveData);
  return response.data;
};

// const BASE_URL = "http://localhost:8080/cl/autostatus";

// export const listApi = async () => {
//   const response = await axios.get(`${BASE_URL}/list`);
//   return response.data;
// };

// export const deleteApi = async (id) => {
//   const response = await axios.post(`${BASE_URL}/delete/${id}`);
//   return response.data;
// };

// export const updateApi = async (data) => {
//   await axios.post(`${BASE_URL}/update`, data);
// };

// export const createApi = async (data) => {
//   await axios.post(`${BASE_URL}/add`, data);
// };
