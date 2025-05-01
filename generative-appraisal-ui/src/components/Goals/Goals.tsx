import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import './Goals.css';
import penImage from '../../assets/pen.png';
import trashImage from '../../assets/trash.png';
import plusImage from '../../assets/plus.png';

interface Goal {
  id: number;
  description: string;
  dueDate?: string;
  status?: string;
}

const Goals: React.FC = () => {
  const [goals, setGoals] = useState<Goal[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

    const fetchGoals = async () => {
      setLoading(true);
      try {
        const data = await api.get<Goal[]>('/goals');
        setGoals(data);
        
        
      } catch (error) {
        console.error('Error fetching goals:', error);
      } finally {
        setLoading(false);
      }
    };

  useEffect(() => {
    
    fetchGoals();
  }, []);

  const handleDelete = async (id: number) => {
    try {
      await api.delete(`/goals/${id}`);
       fetchGoals();
    } catch (error) {
      console.error('Error deleting goal:', error);
    }
  };

  const handleEdit = async (goal: Goal) => {    
      try {
        await api.put(`/goals/${goal.id}`, goal);
        fetchGoals();
      } catch (error) {
        console.error('Error editing goal:', error);
      }
  };

  const addGoal = async (description: string, dueDate:string, status:string) => {
    try {
      await api.post(`/goals`, { description, dueDate, status });
      fetchGoals();
    } catch (error) {
      console.error('Error adding goal:', error);
      }
  };

    const handleAdd = () => {
    console.log('Adding a new goal');
    try {
        addGoal('New Goal', '02/02/2024', 'New');
    } catch (error) {
      console.error('Error adding goal:', error);
    }
    };



  if (loading) {
    return <div>Loading...</div>;
  }

    return (
    <div className='goals-container'>
        <h2>Goals</h2>
      <button className='add-button' onClick={handleAdd}>
        <img src={plusImage} alt='Add' className='button-image' />
        Add Goal
        </button>
      <ul className='goals-list'>
        {goals.map((goal) => (
            <li key={goal.id} className='goal-item'>
               <span className='goal-description'>{goal.description}</span>
              <div className='button-group'>
                <button className='edit-button' onClick={() => handleEdit({id:goal.id, description:goal.description, dueDate:goal.dueDate, status:goal.status})}>
                   <img src={penImage} alt='Edit' className='button-image' />
                </button>
                <button className='delete-button' onClick={() => handleDelete(goal.id)}>
                   <img src={trashImage} alt='Delete' className='button-image' />
                </button>
               </div>
          </li>
        ))}
      </ul>
    </div>
  );
}; 
export default Goals;