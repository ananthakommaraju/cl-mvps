// quarkus-todo-ui/src/App.tsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import HomePage from './pages/HomePage';
import ListTodo from './pages/ListTodo';
import CreateTodo from './pages/CreateTodo';
import UpdateTodo from './pages/UpdateTodo';
import DeleteTodo from './pages/DeleteTodo';
import './App.css';

const Nav: React.FC = () => {
  return (
    <nav>
      <ul>
        <li><Link to="/">Home</Link></li>
        <li><Link to="/list">List</Link></li>
        <li><Link to="/create">Create</Link></li>
      </ul>
    </nav>
  );
};

const App: React.FC = () => {
  return (
    <Router>
      <Nav />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/list" element={<ListTodo />} />
        <Route path="/create" element={<CreateTodo />} />
        <Route path="/update/:id" element={<UpdateTodo />} />
        <Route path="/delete/:id" element={<DeleteTodo />} />
      </Routes>
    </Router>
  );
};

export default App;

// quarkus-todo-ui/src/pages/HomePage.tsx
import React from 'react';

const HomePage: React.FC = () => {
  return (
    <div>
      <h1>Home Page</h1>
    </div>
  );
};

export default HomePage;

// quarkus-todo-ui/src/pages/ListTodo.tsx
import React from 'react';

const ListTodo: React.FC = () => {
  return (
    <div>
      <h1>List Todo</h1>
    </div>
  );
};

export default ListTodo;

// quarkus-todo-ui/src/pages/CreateTodo.tsx
import React from 'react';

const CreateTodo: React.FC = () => {
  return (
    <div>
      <h1>Create Todo</h1>
    </div>
  );
};

export default CreateTodo;

// quarkus-todo-ui/src/pages/UpdateTodo.tsx
import React from 'react';
import { useParams } from 'react-router-dom';

const UpdateTodo: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  return (
    <div>
      <h1>Update Todo - ID: {id}</h1>
    </div>
  );
};

export default UpdateTodo;

// quarkus-todo-ui/src/pages/DeleteTodo.tsx
import React from 'react';
import { useParams } from 'react-router-dom';

const DeleteTodo: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  return (
    <div>
      <h1>Delete Todo - ID: {id}</h1>
    </div>
  );
};

export default DeleteTodo;

// quarkus-todo-ui/src/App.css
.App {
  text-align: center;
}

.App-logo {
  height: 40vmin;
  pointer-events: none;
}

@media (prefers-reduced-motion: no-preference) {
  .App-logo {
    animation: App-logo-spin infinite 20s linear;
  }
}

.App-header {
  background-color: #282c34;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: calc(10px + 2vmin);
  color: white;
}

.App-link {
  color: #61dafb;
}

@keyframes App-logo-spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
nav ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
  overflow: hidden;
  background-color: #333;
}

nav li {
  float: left;
}

nav li a {
  display: block;
  color: white;
  text-align: center;
  padding: 14px 16px;
  text-decoration: none;
}

nav li a:hover {
  background-color: #111;
}