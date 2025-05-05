import React, { useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Api from '../services/api';

const DeleteTodo: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  useEffect(() => {
    if (!id) {
      navigate('/');
    }
  }, [id, navigate]);

  const handleDelete = async () => {
    if (id) {
      try {
        await Api.delete(parseInt(id));
        navigate('/list');
      } catch (error) {
        navigate('/');
      }
    }
  };

  return (
    <div>
      <h2>Delete Todo</h2>
      <p>Are you sure you want to delete this todo?</p>
      <button onClick={handleDelete}>Delete</button>
    </div>
  );
};

export default DeleteTodo;