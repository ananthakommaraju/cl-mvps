import React, { useState, useEffect } from 'react';
import { Fab, Tooltip, Tabs, Tab, Box } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import Goals from '../Goals';
import Objectives from '../Objectives';
import { fetchGoals, fetchObjectives } from '../../api';

const HomePage = () => {
  const [activeTab, setActiveTab] = useState(0);
  const [goalsData, setGoalsData] = useState([]);
  const [objectivesData, setObjectivesData] = useState([]);

  useEffect(() => {
    const loadData = async () => {
      try {
        const [goals, objectives] = await Promise.all([fetchGoals(), fetchObjectives()]);
        setGoalsData(goals);
        setObjectivesData(objectives);
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };
    loadData();
  }, []);

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  const renderTabContent = () => {
    switch (activeTab) {
      case 0:
        return <Goals goalsData={goalsData} setGoalsData={setGoalsData} />;
      case 1:
        return <Objectives objectivesData={objectivesData} setObjectivesData={setObjectivesData} />;
      default:
        return null;
    }
  };

  return (
    <div style={{ width: '90%', margin: '0 auto', marginTop: '1%' }}>
      <Tabs value={activeTab} onChange={handleTabChange} centered>
        <Tab label="Goals" />
        <Tab label="Objectives" />
      </Tabs>
      <Box mt={2}>{renderTabContent()}</Box>
      <Tooltip title="Add Action" placement="top">
        <Fab
          color="primary"
          aria-label="add"
          style={{ position: 'fixed', bottom: 106, right: 56 }}
          onClick={() => {
            if (activeTab === 0) {
              document.dispatchEvent(new CustomEvent('openGoalsDialog'));
            } else if (activeTab === 1) {
              document.dispatchEvent(new CustomEvent('openObjectivesDialog'));
            }
          }}
        >
          <AddIcon />
        </Fab>
      </Tooltip>
    </div>
  );
};

export default HomePage;