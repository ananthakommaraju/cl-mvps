import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Api } from '../services/api';

interface Todo {
  id: number;
  description: string;
  completed: boolean;
}

const ListTodo: React.FC = () => {
  const [todos, setTodos] = useState<Todo[]>([]);
  const api = new Api();

  useEffect(() => {
    const fetchTodos = async () => {
      try {
        const data = await api.listAll();
        setTodos(data);
      } catch (error) {
        console.error('Error fetching todos:', error);
      }
    };

    fetchTodos();
  }, []);

  return (
    <div>
      <h2>Todo List</h2>
      <ul>
        {todos.map((todo) => (
          <li key={todo.id}>
            {todo.description}
            <Link to={`/update/${todo.id}`}> Update </Link>
            <Link to={`/delete/${todo.id}`}> Delete </Link>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ListTodo;