import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Api from '../services/api';

interface Todo {
  id?: number;
  description: string;
  completed: boolean;
}

const CreateTodo: React.FC = () => {
  const [description, setDescription] = useState<string>('');
  const navigate = useNavigate();

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    const api = new Api();
    const newTodo: Todo = { description, completed: false };
    await api.create(newTodo);
    navigate('/list');
  };

  return (
    <div>
      <h2>Create Todo</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="description">Description:</label>
          <input
            type="text"
            id="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            required
          />
        </div>
        <button type="submit">Create</button>
      </form>
    </div>
  );
};

export default CreateTodo;