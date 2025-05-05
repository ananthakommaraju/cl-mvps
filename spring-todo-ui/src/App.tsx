import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import HomePage from './pages/HomePage';
import ListTodo from './components/ListTodo';
import CreateTodo from './components/CreateTodo';
import UpdateTodo from './components/UpdateTodo';
import DeleteTodo from './components/DeleteTodo';
import Nav from './components/Nav';

function App() {
  return (
    <Router>
      <div>
        <Nav />
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/list" element={<ListTodo />} />
          <Route path="/create" element={<CreateTodo />} />
          <Route path="/update/:id" element={<UpdateTodo />} />
          <Route path="/delete/:id" element={<DeleteTodo />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;