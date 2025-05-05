import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Api } from '../services/api';

const DeleteTodo: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    if (!id) {
      navigate('/');
    } else {
        setLoading(false)
    }
  }, [id, navigate]);

  const handleDelete = async () => {
    try {
      if (id) {
        await Api.delete(parseInt(id));
        navigate('/list');
      }
    } catch (error) {
        navigate('/');
    }
  };

  if (loading){
    return <p>Loading...</p>
  }

  return (
    <div>
      <h2>Delete Todo</h2>
      <p>Are you sure you want to delete this todo?</p>
      <button onClick={handleDelete}>Delete</button>
    </div>
  );
};

export default DeleteTodo;