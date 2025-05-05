import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Api } from '../services/api';

const CreateTodo: React.FC = () => {
  const [description, setDescription] = useState<string>('');
  const navigate = useNavigate();
  const api = new Api();

  const handleCreateTodo = async () => {
    try {
      await api.create({ description, completed: false });
      navigate('/list');
    } catch (error) {
      console.error('Error creating todo:', error);
    }
  };

  return (
    <div>
      <h2>Create Todo</h2>
      <div>
        <label htmlFor="description">Description:</label>
        <input
          type="text"
          id="description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
      </div>
      <button onClick={handleCreateTodo}>Create</button>
    </div>
  );
};

export default CreateTodo;