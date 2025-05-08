import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import React from 'react';
import { useNavigate } from "react-router-dom";
import { Button, Typography } from '@mui/material';

const Back = ({ title }) => {
    const navigate = useNavigate();

    const handleBackClick = () => {
        navigate("/");
    };

    return (
        <div style={{ display: 'flex', alignItems: 'center', padding: "10px" }}>
            <Button startIcon={<ArrowBackIcon style={{ fontSize: '30px' }} />} onClick={handleBackClick} />
            <Typography variant="h5" style={{ fontWeight: 'bold' }}>{title}</Typography>
        </div>
    );
}

export default Back;