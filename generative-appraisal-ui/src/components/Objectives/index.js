import React, { useState, useEffect } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, TextField, MenuItem, Button, Table, TableBody, TableCell, TableHead, TableRow, Box, IconButton, Typography } from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
// import DeleteIcon from '@mui/icons-material/Delete';
import { createObjective, fetchGoals, updateObjective } from '../../api'; // Import fetchGoals and updateObjective API functions

const Objectives = ({ objectivesData, setObjectivesData }) => {
  const [dialogOpen, setDialogOpen] = useState(false);
  const [formData, setFormData] = useState({});
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [deleteIndex, setDeleteIndex] = useState(null);
  const [goalsList, setGoalsList] = useState([]); // State to store the list of goals

  useEffect(() => {
    const handleOpenDialog = () => setDialogOpen(true);
    document.addEventListener('openObjectivesDialog', handleOpenDialog);
    return () => document.removeEventListener('openObjectivesDialog', handleOpenDialog);
  }, []);

  useEffect(() => {
    const loadGoals = async () => {
      try {
        const goals = await fetchGoals(); // Fetch goals from the backend
        setGoalsList(goals); // Update the state with the fetched goals
      } catch (error) {
        console.error('Error fetching goals:', error);
      }
    };
    loadGoals();
  }, []);

  const handleDialogClose = () => {
    setDialogOpen(false);
    setFormData({});
  };

  const handleDeleteDialogClose = () => {
    setDeleteDialogOpen(false);
    setDeleteIndex(null);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSave = async () => {
    if (formData.kra && formData.description && formData.goal) { // Ensure mandatory fields are filled
      const objectiveData = {
        id: formData.kra, // Map KRA Id to id
        name: formData.description, // Map Description to name
        goalId: formData.goal, // Map Goal to goalId
      };

      try {
        if (formData.editing) {
          // Call the API to update the objective
          await updateObjective(objectiveData);
          const updatedObjectives = [...objectivesData];
          updatedObjectives[formData.index] = { ...objectiveData };
          setObjectivesData(updatedObjectives); // Update the local state
        } else {
          // Call the API to create a new objective
          await createObjective(objectiveData);
          setObjectivesData([...objectivesData, objectiveData]); // Update the local state
        }
        handleDialogClose();
      } catch (error) {
        console.error('Error saving objective:', error);
        alert('Failed to save the objective. Please try again.');
      }
    } else {
      alert('Please fill in all required fields: KRA Id, Description, and Goal.');
    }
  };

  const handleEdit = (index) => {
    const objectiveToEdit = objectivesData[index];
    setFormData({
      kra: objectiveToEdit.id, // Map id to KRA
      description: objectiveToEdit.name, // Map name to Description
      goal: objectiveToEdit.goalId, // Map goalId to Goal
      editing: true,
      index,
    });
    setDialogOpen(true);
  };

  // const handleDelete = (index) => {
  //   setDeleteIndex(index);
  //   setDeleteDialogOpen(true);
  // };

  const confirmDelete = () => {
    setObjectivesData(objectivesData.filter((_, i) => i !== deleteIndex));
    handleDeleteDialogClose();
  };

  return (
    <Box>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>KRA Id</TableCell>
            <TableCell>Description</TableCell>
            <TableCell>Status</TableCell>
            <TableCell>Related Issue</TableCell>
            <TableCell>Goal Id</TableCell>
            <TableCell>Assigned To</TableCell>
            <TableCell>Target Date</TableCell>
            <TableCell>Actual Date</TableCell>
            <TableCell>Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {objectivesData.map((objective, index) => (
            <TableRow key={index}>
              <TableCell>{objective.id}</TableCell>
              <TableCell>{objective.name}</TableCell>
              <TableCell>Active</TableCell>
              <TableCell>No new Issue</TableCell>
              <TableCell>{objective.goalId}</TableCell>
              <TableCell>Sonu</TableCell>
              <TableCell>30-06-2025</TableCell>
              <TableCell>15-06-2025</TableCell>
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
        <DialogTitle>{formData.editing ? 'Edit Objective' : 'Add Objective'}</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            margin="dense"
            size="small"
            label="KRA Id"
            type="number" // Set the type to number
            name="kra"
            value={formData.kra || ''}
            onChange={handleInputChange}
            disabled={formData.editing} // Disable the field when editing
          />
          <TextField
            fullWidth
            margin="dense"
            size="small"
            label="Description"
            name="description"
            value={formData.description || ''}
            multiline
            rows={4}
            onChange={handleInputChange}
          />
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
          <TextField
            fullWidth
            margin="dense"
            size="small"
            label="Related Issue"
            name="relatedIssue"
            value={formData.relatedIssue || ''}
            select
            onChange={handleInputChange}
          >
            <MenuItem value="Issue 1">Issue 1</MenuItem>
            <MenuItem value="Issue 2">Issue 2</MenuItem>
          </TextField>
          <TextField
            fullWidth
            margin="dense"
            size="small"
            label="Goal"
            name="goal"
            value={formData.goal || ''}
            select
            onChange={handleInputChange}
          >
            {goalsList.map((goal) => (
              <MenuItem key={goal.id} value={goal.id}>
                {goal.id} - {goal.name}
              </MenuItem>
            ))}
          </TextField>
          <TextField
            fullWidth
            margin="dense"
            size="small"
            label="Assigned To"
            name="assignedTo"
            value={formData.assignedTo || ''}
            onChange={handleInputChange}
          />
          <TextField
            fullWidth
            margin="dense"
            size="small"
            label="Target Date"
            name="targetDate"
            type="date"
            value={formData.targetDate || ''}
            InputLabelProps={{ shrink: true }}
            onChange={handleInputChange}
          />
          <TextField
            fullWidth
            margin="dense"
            size="small"
            label="Actual Date"
            name="actualDate"
            type="date"
            value={formData.actualDate || ''}
            InputLabelProps={{ shrink: true }}
            onChange={handleInputChange}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDialogClose} color="secondary" size="small">Cancel</Button>
          <Button onClick={handleSave} color="primary" size="small">Save</Button>
        </DialogActions>
      </Dialog>      <Dialog open={deleteDialogOpen} onClose={handleDeleteDialogClose}>        <DialogTitle>Confirm Deletion</DialogTitle>        <DialogContent>
          <Typography>Are you sure you want to delete this objective?</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDeleteDialogClose} color="secondary" size="small">Cancel</Button>
          <Button onClick={confirmDelete} color="primary" size="small">Delete</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Objectives;
