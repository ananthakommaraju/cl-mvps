import React from 'react'
import './Header.css';
import lloydsLogo from '../../assets/lloyds-logo.png';

const Header: React.FC = () => {
  return (
    <header className="header">
        <img src={lloydsLogo} alt="Lloyds Banking Group Logo" className="header-logo" />
        <h1 className="header-title">Lloyds Banking Group</h1>
    </header>
  )
}

export default Header
