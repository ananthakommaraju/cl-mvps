import React, { useState, useEffect } from 'react';
import api, { getObjectives, deleteObjective } from '../../services/api';
import './Objectives.css';
import penImage from '../../assets/pen.png';
import trashImage from '../../assets/trash.png';
import plusImage from '../../assets/plus.png';

interface Objective {
  id: number;
  description: string;
}

const Objectives: React.FC = () => {
  const [objectives, setObjectives] = useState<Objective[]>([]);

  useEffect(() => {
    fetchObjectives();
  }, []);

  const fetchObjectives = async () => {
    try {
      const data = await api.get<Objective[]>('/objectives');
      setObjectives(data);
    } catch (error) {
      console.error('Error fetching objectives:', error);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await api.delete(`/objectives/${id}`);
      fetchObjectives();
    } catch (error) {
      console.error('Error deleting objective:', error);
    }
  };

  const handleEdit = (id: number) => {
    console.log('Editing objective with ID:', id);
  };

  const handleAdd = () => {
    console.log('Adding a new objective');
  };

  return (
    <div className="objectives-container">
      <h2>Objectives</h2>
      <button className="add-button" onClick={handleAdd}>
        <img src={plusImage} alt="Add" className="button-image" />
        Add Objective
      </button>
      <ul className="objectives-list">
        {objectives.map((objective) => (
          <li key={objective.id} className="objective-item">
            <span className="objective-description">{objective.description}</span>
            <div className="button-group">
              <button className="edit-button" onClick={() => handleEdit(objective.id)}>
                <img src={penImage} alt="Edit" className="button-image" />
              </button>
              <button className="delete-button" onClick={() => handleDelete(objective.id)}>
                <img src={trashImage} alt="Delete" className="button-image" />
              </button>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Objectives;