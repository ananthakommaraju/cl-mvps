import React from 'react';
import { Link } from 'react-router-dom';

const Nav: React.FC = () => {
  return (
    <nav>
      <ul>
        <li>
          <Link to="/">Home</Link>
        </li>
        <li>
          <Link to="/list">List</Link>
        </li>
        <li>
          <Link to="/create">Create</Link>
        </li>
        <li>
          <Link to="/update">Update</Link>
        </li>
        <li>
          <Link to="/delete">Delete</Link>
        </li>
      </ul>
    </nav>
  );
};

export default Nav;