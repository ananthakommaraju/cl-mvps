import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Api } from '../services/api';

interface Todo {
  id: number;
  description: string;
  completed: boolean;
}

const UpdateTodo: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [todo, setTodo] = useState<Todo | null>(null);
  const [description, setDescription] = useState<string>('');

  useEffect(() => {
    const fetchTodo = async () => {
      try {
        if (id) {
          const todoData = await Api.getById(parseInt(id));
          if (todoData) {
            setTodo(todoData);
            setDescription(todoData.description);
          } else {
            navigate('/');
          }
        }
      } catch (error) {
        console.error('Error fetching todo:', error);
        navigate('/');
      }
    };

    fetchTodo();
  }, [id, navigate]);

  const handleDescriptionChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setDescription(event.target.value);
  };

  const handleUpdate = async () => {
    if (todo && id) {
      try {
        await Api.update(parseInt(id), { ...todo, description });
        navigate('/list');
      } catch (error) {
        console.error('Error updating todo:', error);
      }
    }
  };

  if (!todo) {
    return <div>Loading...</div>;
  }

  return (
    <div>
      <h2>Update Todo</h2>
      <div>
        <label htmlFor="description">Description:</label>
        <input
          type="text"
          id="description"
          value={description}
          onChange={handleDescriptionChange}
        />
      </div>
      <button onClick={handleUpdate}>Update</button>
    </div>
  );
};

export default UpdateTodo;