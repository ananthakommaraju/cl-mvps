import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Api from '../services/api';

interface Todo {
  id: number;
  description: string;
  completed: boolean;
}

const ListTodo: React.FC = () => {
  const [todos, setTodos] = useState<Todo[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const api = new Api();
        const data = await api.listAll();
        setTodos(data);
      } catch (err: any) {
        setError(err.message || 'An error occurred');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div>
      <h2>Todo List</h2>
      <ul>
        {todos.map((todo) => (
          <li key={todo.id}>
            {todo.description} - {todo.completed ? 'Completed' : 'Pending'}
            <Link to={`/update/${todo.id}`}>Update</Link>
            <Link to={`/delete/${todo.id}`}>Delete</Link>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ListTodo;