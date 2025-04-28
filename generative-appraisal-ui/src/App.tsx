import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/Header/Header';
import Navigation from './components/Navigation/Navigation';
import HomePage from './pages/HomePage';
import GoalsPage from './pages/GoalsPage';
import ObjectivesPage from './pages/ObjectivesPage';
import AccomplishmentsPage from './pages/AccomplishmentsPage';
import ReportsPage from './pages/ReportsPage';
import LoginPage from './pages/LoginPage';
import './App.css';

function App() {
  return (
    <Router>
      <div className="app">
        <Header/>
        <Navigation/>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/goals" element={<GoalsPage />} />
          <Route path="/objectives" element={<ObjectivesPage />} />
          <Route path="/accomplishments" element={<AccomplishmentsPage />} />
          <Route path="/reports" element={<ReportsPage />} />
          <Route path="/login" element={<LoginPage />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;