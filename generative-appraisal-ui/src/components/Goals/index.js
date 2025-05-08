import React, { useState, useEffect } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, TextField, MenuItem, Button, Chip, Table, TableBody, TableCell, TableHead, TableRow, Box, IconButton, Typography } from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
// import DeleteIcon from '@mui/icons-material/Delete';
import { createGoal, updateGoal } from '../../api'; // Import the createGoal and updateGoal API functions

const Goals = ({ goalsData, setGoalsData }) => {
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [challenges, setChallenges] = useState([]);
  const [challengeInput, setChallengeInput] = useState('');
  const [formData, setFormData] = useState({});
  const [deleteIndex, setDeleteIndex] = useState(null);

  useEffect(() => {
    const handleOpenDialog = () => setDialogOpen(true);
    document.addEventListener('openGoalsDialog', handleOpenDialog);
    return () => document.removeEventListener('openGoalsDialog', handleOpenDialog);
  }, []);

  const handleDialogClose = () => {
    setDialogOpen(false);
    setFormData({});
    setChallenges([]);
  };

  const handleDeleteDialogClose = () => {
    setDeleteDialogOpen(false);
    setDeleteIndex(null);
  };

  const handleAddChallenge = () => {
    if (challengeInput.trim()) {
      setChallenges([...challenges, challengeInput.trim()]);
      setChallengeInput('');
    }
  };

  const handleDeleteChallenge = (challengeToDelete) => {
    setChallenges(challenges.filter((challenge) => challenge !== challengeToDelete));
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSave = async () => {
    if (formData.kpa && formData.description && formData.category) { // Ensure mandatory fields are filled
      const goalData = {
        id: formData.kpa, // Map KPA Id to id
        name: formData.description, // Map Description to name
        category: formData.category, // Map Category to category
      };

      try {
        if (formData.editing) {
          // Call the API to update the goal
          await updateGoal(goalData);
          const updatedGoals = [...goalsData];
          updatedGoals[formData.index] = { ...goalData, challenges };
          setGoalsData(updatedGoals); // Update the local state
        } else {
          // Call the API to create a new goal
          await createGoal(goalData);
          setGoalsData([...goalsData, { ...goalData, challenges }]); // Update the local state
        }
        handleDialogClose();
      } catch (error) {
        console.error('Error saving goal:', error);
        alert('Failed to save the goal. Please try again.');
      }
    } else {
      alert('Please fill in all required fields: KPA Id, Description, and Category.');
    }
  };

  const handleEdit = (index) => {
    const goalToEdit = goalsData[index];
    setFormData({
      kpa: goalToEdit.id, // Map id to KPA
      description: goalToEdit.name, // Map name to Description
      category: goalToEdit.category, // Map category to Category
      status: goalToEdit.status || '', // Optional field
      plannedStartDate: goalToEdit.plannedStartDate || '', // Optional field
      actualStartDate: goalToEdit.actualStartDate || '', // Optional field
      targetDate: goalToEdit.targetDate || '', // Optional field
      dateCompleted: goalToEdit.dateCompleted || '', // Optional field
      editing: true, // Set editing flag to true
      index,
    });
    setChallenges(goalToEdit.challenges || []); // Prefill challenges if available
    setDialogOpen(true);
  };

  // const handleDelete = (index) => {
  //   setDeleteIndex(index);
  //   setDeleteDialogOpen(true);
  // };

  const confirmDelete = () => {
    setGoalsData(goalsData.filter((_, i) => i !== deleteIndex));
    handleDeleteDialogClose();
  };

  return (
    <Box>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>KPA Id</TableCell>
            <TableCell>Description</TableCell>
            <TableCell>Status</TableCell>
            <TableCell>Planned Start Date</TableCell>
            <TableCell>Actual Start Date</TableCell>
            <TableCell>Target Date</TableCell>
            <TableCell>Date Completed</TableCell>
            <TableCell>Challenges</TableCell>
            <TableCell>Category</TableCell>
            <TableCell>Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {goalsData.map((goal, index) => (
            <TableRow key={index}>
              <TableCell>{goal.id}</TableCell>
              <TableCell>{goal.name}</TableCell>
              <TableCell>Active</TableCell>
              <TableCell>30-04-2025</TableCell>
              <TableCell>03-05-2025</TableCell>
              <TableCell>10-06-2025</TableCell>
              <TableCell>12-05-2025</TableCell>
              <TableCell>Challenge 1</TableCell>
              <TableCell>{goal.category}</TableCell>
              <TableCell>
                <IconButton size="small" onClick={() => handleEdit(index)}>
                  <EditIcon />
                </IconButton>
                {/* <IconButton size="small" onClick={() => handleDelete(index)}>
                  <DeleteIcon />
                </IconButton> */}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <Dialog open={dialogOpen} onClose={handleDialogClose} fullWidth maxWidth="sm">
        <DialogTitle>{formData.editing ? 'Edit Goal' : 'Add Goal'}</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            margin="dense"
            size="small"
            label="KPA Id"
            name="kpa"
            type="number" // Set the type to number
            value={formData.kpa || ''}
            onChange={handleInputChange}
            disabled={formData.editing} // Disable the field when editing
          />
          <TextField fullWidth margin="dense" size="small" label="Description" name="description" value={formData.description || ''} multiline rows={4} onChange={handleInputChange} />
          <TextField
            fullWidth
            margin="dense"
            size="small"
            label="Status"
            name="status"
            value={formData.status || ''}
            select
            onChange={handleInputChange}
          >
            <MenuItem value="Open">Open</MenuItem>
            <MenuItem value="In Progress">In Progress</MenuItem>
            <MenuItem value="Completed">Completed</MenuItem>
          </TextField>
          <TextField fullWidth margin="dense" size="small" label="Planned Start Date" name="plannedStartDate" type="date" value={formData.plannedStartDate || ''} InputLabelProps={{ shrink: true }} onChange={handleInputChange} />
          <TextField fullWidth margin="dense" size="small" label="Actual Start Date" name="actualStartDate" type="date" value={formData.actualStartDate || ''} InputLabelProps={{ shrink: true }} onChange={handleInputChange} />
          <TextField fullWidth margin="dense" size="small" label="Target Date" name="targetDate" type="date" value={formData.targetDate || ''} InputLabelProps={{ shrink: true }} onChange={handleInputChange} />
          <TextField fullWidth margin="dense" size="small" label="Date Completed" name="dateCompleted" type="date" value={formData.dateCompleted || ''} InputLabelProps={{ shrink: true }} onChange={handleInputChange} />
          <TextField
            fullWidth
            margin="dense"
            size="small"
            label="Challenges"
            value={challengeInput}
            onChange={(e) => setChallengeInput(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleAddChallenge()}
          />
          <Box mt={1} mb={2}>
            {challenges.map((challenge, index) => (
              <Chip
                key={index}
                label={challenge}
                onDelete={() => handleDeleteChallenge(challenge)}
                style={{ marginRight: '8px', marginBottom: '8px' }}
              />
            ))}
          </Box>
          <TextField
            fullWidth
            margin="dense"
            size="small"
            label="Category"
            name="category"
            value={formData.category || ''}
            onChange={handleInputChange}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDialogClose} color="secondary" size="small">Cancel</Button>
          <Button onClick={handleSave} color="primary" size="small">Save</Button>
        </DialogActions>
      </Dialog>
      <Dialog open={deleteDialogOpen} onClose={handleDeleteDialogClose}>
        <DialogTitle>Confirm Deletion</DialogTitle>
        <DialogContent>
          <Typography>Are you sure you want to delete this goal?</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDeleteDialogClose} color="secondary" size="small">Cancel</Button>
          <Button onClick={confirmDelete} color="primary" size="small">Delete</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Goals;
