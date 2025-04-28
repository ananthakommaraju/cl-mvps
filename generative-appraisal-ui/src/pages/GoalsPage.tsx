import React from 'react';
import GoalList from '../components/Goal/GoalList';

const GoalsPage: React.FC = () => {
  return (
    <div>
      <h1>Goals</h1>
      <GoalList />
    </div>
  );
};

export default GoalsPage;