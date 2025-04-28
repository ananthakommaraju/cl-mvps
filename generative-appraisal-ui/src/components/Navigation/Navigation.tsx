import React from 'react';
import { Link } from 'react-router-dom';
import './Navigation.css';

const Navigation: React.FC = () => {
  return (
    <nav className="navigation">
      <ul>
        <li>
          <Link to="/goals">Goals</Link>
        </li>
        <li>
          <Link to="/objectives">Objectives</Link>
        </li>
        <li>
          <Link to="/accomplishments">Accomplishments</Link>
        </li>
        <li>
          <Link to="/reports">Reports</Link>
        </li>
      </ul>
    </nav>
  );
};

export default Navigation;
